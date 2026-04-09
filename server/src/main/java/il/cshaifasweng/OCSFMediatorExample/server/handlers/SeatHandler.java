package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.SeatMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.RegisteredUser;
import il.cshaifasweng.OCSFMediatorExample.entities.Seat;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class SeatHandler extends MessageHandler
{
    private SeatMessage message;

    public SeatHandler(SeatMessage message, ConnectionToClient client, Session session)
    {
        super(client,session);
        this.message = message;
    }

    public void handleMessage()
    {
        switch (message.requestType)
        {
            case SEATS_RESERVED -> seat_reserved();
            case SEATS_CANCELATION -> seat_cancellation();
            case GET_ALL_SEAT_BY_HALL -> get_all_seat_by_hall();
        }
    }
    @Override
    public void setMessageTypeToResponse()
    {
        message.messageType= Message.MessageType.RESPONSE;
    }

    private void seat_reserved() {
        try {
            for(Seat seat : message.hallSeats)
            {
                Query<Seat> query = session.createQuery("FROM Seat WHERE id = :id", Seat.class);
                query.setParameter("id", seat.getId());
                Seat found = query.uniqueResult();

                if (found.getMoviesIds().contains(message.movieInstance.getId()))
                {
                    message.responseType = SeatMessage.ResponseType.SEATS_IS_ALREADY_TAKEN;
                    return;
                }
            }
            for(Seat seat : message.hallSeats)
            {
                Query<Seat> query = session.createQuery("FROM Seat WHERE id = :id", Seat.class);
                query.setParameter("id", seat.getId());
                Seat found = query.uniqueResult();
                found.addMovieInstanceId(message.movieInstance);
                session.update(found);
                session.flush();
            }
            message.responseType = SeatMessage.ResponseType.SEATS_WAS_RESERVED;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            message.responseType = SeatMessage.ResponseType.MESSAGE_FAIL;
        }
    }
    private void seat_cancellation() {
        try {
            for (Seat seat : message.hallSeats) {
                // Fetch the seat from the database
                Query<Seat> query = session.createQuery("FROM Seat WHERE id = :id", Seat.class);
                query.setParameter("id", seat.getId());
                Seat found = query.uniqueResult();

                // Remove the movie instance from the seat
                found.deleteMovieInstance(message.movieInstance);

                // Update the seat in the database
                session.update(found);
                session.flush();
            }

            message.responseType = SeatMessage.ResponseType.SEATS_HAS_BEEN_CANCELED;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = SeatMessage.ResponseType.MESSAGE_FAIL;
        }
    }

    private void get_all_seat_by_hall() {
        try {
            Query<Seat> query = session.createQuery("from Seat where hall.id = :hallId", Seat.class);
            query.setParameter("hallId", message.hallId);
            List<Seat> seats = query.list();
            message.hallSeats = new ArrayList<>(seats);
            message.responseType = SeatMessage.ResponseType.SEATS_LIST;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = SeatMessage.ResponseType.MESSAGE_FAIL;
        }
    }
}
