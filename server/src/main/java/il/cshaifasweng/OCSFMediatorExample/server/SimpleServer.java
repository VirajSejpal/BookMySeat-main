package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.entities.Messages.*;
import il.cshaifasweng.OCSFMediatorExample.server.events.*;
import il.cshaifasweng.OCSFMediatorExample.server.handlers.*;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.*;

import il.cshaifasweng.OCSFMediatorExample.server.scheduler.ComplaintFollowUpScheduler;
import il.cshaifasweng.OCSFMediatorExample.server.scheduler.HomeViewingScheduler;
import il.cshaifasweng.OCSFMediatorExample.server.scheduler.MovieInstanceScheduler;
import il.cshaifasweng.OCSFMediatorExample.server.scheduler.OrderScheduler;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleServer extends AbstractServer
{
	private static SimpleServer instance;  // Static variable to hold the single instance
	private static ArrayList<SubscribedClient> clients = new ArrayList<>();
	public static Session session;
	public SimpleServer(int port)
	{
		super(port);
		session = getSessionFactory().openSession();
		instance=this;
		// Initialize schedulers as singletons
		ComplaintFollowUpScheduler complaintScheduler = ComplaintFollowUpScheduler.getInstance();
		OrderScheduler emailNotificationScheduler = OrderScheduler.getInstance();
		HomeViewingScheduler homeViewingScheduler = HomeViewingScheduler.getInstance();
		MovieInstanceScheduler movieInstanceScheduler = MovieInstanceScheduler.getInstance();

		GenerateDB db = new GenerateDB(session);
		db.initializeDatabase();
		
		// Reset all logged-in states just in case the server was forcefully stopped
		try {
			session.beginTransaction();
			session.createQuery("UPDATE Employee SET isOnline = false").executeUpdate();
			session.createQuery("UPDATE RegisteredUser SET isOnline = false").executeUpdate();
			session.getTransaction().commit();
			System.out.println("All logged-in states have been reset successfully.");
		} catch (Exception e) {
			if (session.getTransaction().isActive()) {
				session.getTransaction().rollback();
			}
			System.out.println("Could not reset logged in states: " + e.getMessage());
		}
		// Schedule all active complaints
		complaintScheduler.scheduleAllActiveComplaints();
		// Schedule all home viewing packages
		HomeViewingScheduler.scheduleHomeViewingPackages();
		// Schedule all future movie instances
		movieInstanceScheduler.scheduleMovieInstances();
	}


	public static SimpleServer getServer() {
		return instance;
	}

	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client){
		try {
			Message message = (Message) msg;
			MessageHandler messageHandler = null;

			session.clear();  // Clear session to ensure fresh data is fetched.
			if (!session.getTransaction().isActive()) {
				session.beginTransaction();
			} else {
				System.out.println("Warning: Transaction already active. Using existing transaction.");
			}

			if (message.messageType == Message.MessageType.REQUEST) {
				if (msg instanceof ComplaintMessage) {
					messageHandler = new ComplaintHandler((ComplaintMessage) msg, client, session);
				} else if (msg instanceof EmployeeLoginMessage) {
					messageHandler = new EmployeeLoginHandler((EmployeeLoginMessage) msg, client, session);
				} else if (msg instanceof LoginMessage) {
					messageHandler = new LoginHandler((LoginMessage) msg, client, session);
				} else if (msg instanceof MovieMessage) {
					messageHandler = new MovieHandler((MovieMessage) msg, client, session);
				} else if (msg instanceof MovieInstanceMessage) {
					messageHandler = new MovieInstanceHandler((MovieInstanceMessage) msg, client, session);
				} else if (msg instanceof PriceRequestMessage) {
					messageHandler = new PriceRequestHandler((PriceRequestMessage) msg, client, session);
				} else if (msg instanceof PurchaseMessage) {
					messageHandler = new PurchaseHandler((PurchaseMessage) msg, client, session);
				} else if (msg instanceof SeatMessage) {
					messageHandler = new SeatHandler((SeatMessage) msg, client, session);
				} else if (msg instanceof TheaterMessage) {
					messageHandler = new TheaterHandler((TheaterMessage) msg, client, session);
				} else if (msg instanceof ConnectionMessage) {
					messageHandler = new ConnectionHandler((ConnectionMessage) msg , client, session ,clients);
				} else if (msg instanceof ReportMessage) {
					messageHandler = new ReportHandler((ReportMessage) msg, client, session);
				} else if (msg instanceof RegisteredUserMessage) {
					messageHandler = new RegisteredUserHandler((RegisteredUserMessage)msg , client, session);
				} else if (msg instanceof HallMessage) {
					messageHandler = new HallHandler((HallMessage) msg, client, session);
				}

				if (messageHandler != null) {
					messageHandler.handleMessage();            	// handle the message ,and change DB if needed
					session.getTransaction().commit();          // save changes in DB
					messageHandler.setMessageTypeToResponse();  //change message to response that client will know it is a response from server

					if(msg instanceof MovieInstanceMessage &&(
							((MovieInstanceMessage) msg).requestType == MovieInstanceMessage.RequestType.DELETE_MOVIE_INSTANCE
							||((MovieInstanceMessage) msg).requestType == MovieInstanceMessage.RequestType.UPDATE_MOVIE_INSTANCE
							||((MovieInstanceMessage) msg).requestType == MovieInstanceMessage.RequestType.ADD_MOVIE_INSTANCE))
					{
						if(((MovieInstanceMessage) msg).requestType == MovieInstanceMessage.RequestType.DELETE_MOVIE_INSTANCE)
						{
							Query<MovieInstance> query = session.createQuery("FROM MovieInstance where movie.id = :id and isActive=true", MovieInstance.class);
							query.setParameter("id", ((MovieInstanceMessage) msg).id);
							if(query.getResultList().isEmpty())
								sendToAllClients(new MovieEvent(((MovieInstanceMessage) msg).movies.getFirst().getMovie(),"delete"));
								
							sendToAllClients(new MovieInstanceCanceledEvent(((MovieInstanceMessage) msg).movies.getFirst()));
						}
						if(((MovieInstanceMessage) msg).requestType == MovieInstanceMessage.RequestType.ADD_MOVIE_INSTANCE)
						{
							Query<MovieInstance> query = session.createQuery("FROM MovieInstance where movie.id = :id and isActive=true", MovieInstance.class);
							query.setParameter("id", ((MovieInstanceMessage) msg).id);
							List<MovieInstance> movieList = query.getResultList();
							if(movieList.size()==1)
								sendToAllClients(new MovieEvent(movieList.getFirst().getMovie(),"add"));
						}
					}
					else if(msg instanceof SeatMessage &&
							(((SeatMessage) msg).requestType == SeatMessage.RequestType.SEATS_CANCELATION
							|| ((SeatMessage) msg).requestType == SeatMessage.RequestType.SEATS_RESERVED))
					{
						sendToAllClients(new SeatStatusChangedEvent(((SeatMessage) msg).hallSeats, ((SeatMessage) msg).movieInstance.getId()));
					}
					else if (msg instanceof PurchaseMessage) {
						if (((PurchaseMessage) msg).requestType == PurchaseMessage.RequestType.REMOVE_PURCHASE) {
							if (((PurchaseMessage) msg).purchases.getFirst() instanceof MovieTicket) {
								MovieTicket ticket = (MovieTicket) ((PurchaseMessage) msg).purchases.getFirst();
								sendToAllClients(new SeatStatusChangedEvent(List.of(ticket.getSeat()), ticket.getMovieInstance().getId()));
								System.out.println("Sent SeatStatusChangedEvent to all clients for MovieTicket removal.");
							}
						}
					}
					 else if ((msg instanceof ComplaintMessage && ((ComplaintMessage)msg).requestType==ComplaintMessage.RequestType.ADD_COMPLIANT)) {
						sendToAllClients(new ComplaintEvent(((ComplaintMessage)msg).compliants.getFirst()));
					}

					else if(msg instanceof PriceRequestMessage priceRequestMessage
							&& (((PriceRequestMessage) msg).requestType == PriceRequestMessage.RequestType.APPROVE_PRICE_REQUEST ||
							((PriceRequestMessage) msg).requestType == PriceRequestMessage.RequestType.CREATE_NEW_PRICE_REQUEST))
					{
						sendToAllClients(new PriceChangeEvent(priceRequestMessage.requests.getFirst().getMovie()));
					}
					else if(msg instanceof MovieMessage
							&& ((MovieMessage) msg).requestType == MovieMessage.RequestType.ADD_MOVIE)
					{
						if (((MovieMessage) msg).movies.getFirst().getStreamingType()==Movie.StreamingType.HOME_VIEWING || ((MovieMessage) msg).movies.getFirst().getStreamingType()==Movie.StreamingType.BOTH) {
							sendToAllClients(new HomeViewingEvent(((MovieMessage) msg).movies.getFirst(),"add"));
						}
						if (((MovieMessage) msg).movies.getFirst().getStreamingType()==Movie.StreamingType.THEATER_VIEWING || ((MovieMessage) msg).movies.getFirst().getStreamingType()==Movie.StreamingType.BOTH) {
							sendToAllClients(new MovieEvent(((MovieMessage) msg).movies.getFirst(),"add"));
						}
					}
					else if(msg instanceof MovieMessage
							&& ((MovieMessage) msg).requestType == MovieMessage.RequestType.DEACTIVATE_MOVIE
							&& (((MovieMessage) msg).movies.getFirst().getStreamingType()==Movie.StreamingType.HOME_VIEWING || ((MovieMessage) msg).movies.getFirst().getStreamingType()==Movie.StreamingType.BOTH))

					{
						sendToAllClients(new HomeViewingEvent(((MovieMessage) msg).movies.getFirst(), "delete"));
					}
					else if(msg instanceof MovieMessage
							&& ((MovieMessage) msg).requestType == MovieMessage.RequestType.UPDATE_MOVIE)
					{
						sendToAllClients(new HomeViewingEvent(((MovieMessage) msg).movies.getFirst(), "update"));
						sendToAllClients(new MovieEvent(((MovieMessage) msg).movies.getFirst(),"update"));
					}
					System.out.println("message handled");

                    if (msg instanceof ConnectionMessage && ((ConnectionMessage) msg).requestType == ConnectionMessage.RequestType.DELETE_CONNECTION){
                        System.out.println("message didn't sent");
                        return;
                    }

					if(msg instanceof PriceRequestMessage && ((PriceRequestMessage) msg).requestType== PriceRequestMessage.RequestType.APPROVE_PRICE_REQUEST)
					{
						sendToAllClients(new HomeViewingEvent(((PriceRequestMessage) msg).requests.getFirst().getMovie(), "update"));
						sendToAllClients(new MovieEvent(((PriceRequestMessage) msg).requests.getFirst().getMovie(),"update"));
					}

					client.sendToClient(msg);					//send the message to the client
					System.out.println("message sent");
				}
			}

		}
		catch (Exception exception) {
			if (session != null)
				session.getTransaction().rollback();
			exception.printStackTrace();
		}

	}

	private static SessionFactory getSessionFactory() throws HibernateException {
		Configuration configuration = new Configuration();

		// Add ALL of your entities here. You can also try adding a whole package.
		configuration.addAnnotatedClass(Complaint.class);
		configuration.addAnnotatedClass(Person.class);
		configuration.addAnnotatedClass(Hall.class);
		configuration.addAnnotatedClass(HomeViewingPackageInstance.class);
		configuration.addAnnotatedClass(Movie.class);
		configuration.addAnnotatedClass(MovieInstance.class);
		configuration.addAnnotatedClass(PriceRequest.class);
		configuration.addAnnotatedClass(Seat.class);
		configuration.addAnnotatedClass(Theater.class);
		configuration.addAnnotatedClass(MultiEntryTicket.class);
		configuration.addAnnotatedClass(RegisteredUser.class);
		configuration.addAnnotatedClass(TheaterManager.class);
		configuration.addAnnotatedClass(Employee.class);
		configuration.addAnnotatedClass(MovieTicket.class);
		configuration.addAnnotatedClass(Purchase.class);

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties())
				.build();

		return configuration.buildSessionFactory(serviceRegistry);
	}

	@Override
	public void sendToAllClients(Object message) {
		try {
			for (SubscribedClient SubscribedClient : clients)
			{
				if(SubscribedClient.getClient().isAlive()) // for insurance
					SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}


	public void sendToAllClientsExceptMe(Object message, ConnectionToClient client) {
		try {
			for (SubscribedClient SubscribedClient : clients)
			{
				if(!SubscribedClient.getClient().equals(client) &&  SubscribedClient.getClient().isAlive()) // for insurance
					SubscribedClient.getClient().sendToClient(message);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
