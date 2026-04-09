package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.PurchaseMessage;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.scheduler.HomeViewingScheduler;
import il.cshaifasweng.OCSFMediatorExample.server.scheduler.OrderScheduler;
import org.hibernate.Query;
import org.hibernate.Session;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.YearMonth;


public class PurchaseHandler extends MessageHandler
{
    private PurchaseMessage message;

    public PurchaseHandler(PurchaseMessage message, ConnectionToClient client, Session session)
    {
        super(client,session);
        this.message = message;
    }
    @Override
    public void setMessageTypeToResponse()
    {
        message.messageType= Message.MessageType.RESPONSE;
    }

    public void handleMessage()
    {
        switch (message.requestType)
        {
            case ADD_PURCHASE -> add_purchase();
            case REMOVE_PURCHASE -> remove_purchase();
            case GET_PURCHASES_BY_CUSTOMER_ID -> get_purchases_by_customer_id();
            case GET_PURCHASES_BY_THEATER_ID -> get_purchases_by_theater_id();
            case GET_ALL_MOVIE_PACKAGES_AND_MULTI_TICKETS_PURCHASES_THIS_MONTH -> get_all_movie_packages_and_multi_entry_tickets_purchases_this_month();
            case GET_ALL_PURCHASES -> get_all_purchases();

        }
    }

    private void add_purchase() {
        if (message.purchases.getFirst() != null) {
            try {
                if(message.purchases.getFirst() instanceof MultiEntryTicket)
                {
                    Query<RegisteredUser> query = session.createQuery("from RegisteredUser where id = :id", RegisteredUser.class);
                    query.setParameter("id", message.key);
                    RegisteredUser user = query.getResultList().getFirst();
                    user.setTicket_counter(user.getTicket_counter() + 20);
                    session.update(user);
                }
                session.save(message.purchases.getFirst());
                session.flush();

                // If the purchase is a HomeViewingPackageInstance, schedule link activation and email notification
                if (message.purchases.getFirst() instanceof HomeViewingPackageInstance homeViewingPackage) {
                    scheduleLinkAndEmail(homeViewingPackage);
                }

                // Schedule email notification for the purchase
                OrderScheduler.getInstance().schedulePurchaseConfirmation(message.purchases.getFirst());

                message.responseType = PurchaseMessage.ResponseType.PURCHASE_ADDED;
            } catch (Exception e) {
                e.printStackTrace();
                message.responseType = PurchaseMessage.ResponseType.PURCHASE_FAILED;
            }
        } else {
            message.responseType = PurchaseMessage.ResponseType.PURCHASE_FAILED;
        }
    }

    private void scheduleLinkAndEmail(HomeViewingPackageInstance homeViewingPackage) {
        LocalDateTime movieTime = homeViewingPackage.getViewingDate();
        String movieLink = homeViewingPackage.getLink();
        String customerEmail = homeViewingPackage.getOwner().getEmail();

        // Schedule email one hour before the movie time
        LocalDateTime emailTime = movieTime.minusHours(1);

        // Schedule link activation at the movie time
        HomeViewingScheduler.scheduleLinkActivation(homeViewingPackage);
    }

    private void remove_purchase() {
        try {
            Purchase purchase = session.get(Purchase.class, message.purchases.getFirst().getId());
            if (purchase != null) {
                purchase.setisActive(false);

                if (purchase instanceof MovieTicket) { // if the purchase is a seat, release it
                    Seat seat = session.get(Seat.class, ((MovieTicket) purchase).getSeat().getId());
                    seat.deleteMovieInstance(((MovieTicket) purchase).getMovieInstance());
                    session.update(seat);
                }

                if (purchase instanceof HomeViewingPackageInstance homeViewingPackage) {
                    HomeViewingScheduler.getInstance().cancelScheduledTasks(homeViewingPackage);
                    homeViewingPackage.deactivateLink();
                 }

                session.update(purchase);
                session.flush();

                message.responseType = PurchaseMessage.ResponseType.PURCHASE_REMOVED;
                message.purchases.add(purchase);
                // Send the cancellation email using the EmailNotificationScheduler
                OrderScheduler.getInstance().scheduleEmailCancellation(purchase);

            } else {
                message.responseType = PurchaseMessage.ResponseType.PURCHASE_NOT_FOUND;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = PurchaseMessage.ResponseType.PURCHASE_FAILED;
        }
    }


    private void get_purchases_by_customer_id() {
        try {
            // Retrieve string key properly 
            String idNumber = message.stringKey;

            // Create a query to fetch purchases based on the owner's id_number
            Query<Purchase> query = session.createQuery("from Purchase where owner.id_number = :id", Purchase.class);
            query.setParameter("id", idNumber);

            // Execute the query and get the list of purchases
            List<Purchase> purchases = query.list();

            // Set the purchases in the message
            message.purchases = new ArrayList<>(purchases);
            message.responseType = PurchaseMessage.ResponseType.PURCHASES_LIST;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = PurchaseMessage.ResponseType.PURCHASE_FAILED;
        }
    }

    private void get_purchases_by_theater_id() {
        try {
            // Query for purchases of type MovieTicket
            Query<MovieTicket> movieTicketQuery = session.createQuery(
                    "from MovieTicket  where movieInstance.hall.theater.id = :theaterId", MovieTicket.class);
            movieTicketQuery.setParameter("theaterId", message.key);
            List<MovieTicket> movieTicketPurchases = movieTicketQuery.list();
            message.purchases = (ArrayList<Purchase>) movieTicketQuery;
            message.responseType = PurchaseMessage.ResponseType.PURCHASES_LIST;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = PurchaseMessage.ResponseType.PURCHASE_FAILED;
        }
    }

    private void get_all_movie_packages_and_multi_entry_tickets_purchases_this_month() {
        try {
            YearMonth currentMonth = YearMonth.now();
            LocalDate startOfMonth = currentMonth.atDay(1);
            LocalDate endOfMonth = currentMonth.atEndOfMonth();

            // Query for package purchases
            Query<HomeViewingPackageInstance> packageQuery = session.createQuery(
                    "from HomeViewingPackageInstance where purchaseDate between :start and :end",
                    HomeViewingPackageInstance.class);
            packageQuery.setParameter("start", startOfMonth);
            packageQuery.setParameter("end", endOfMonth);
            List<HomeViewingPackageInstance> packagePurchases = packageQuery.list();

            // Query for multiEntry purchases
            Query<MultiEntryTicket> multiEntryQuery = session.createQuery(
                    "from MultiEntryTicket where purchaseDate between :start and :end",
                    MultiEntryTicket.class);
            multiEntryQuery.setParameter("start", startOfMonth);
            multiEntryQuery.setParameter("end", endOfMonth);
            List<MultiEntryTicket> multiEntryPurchases = multiEntryQuery.list();

            // Combine both lists
            List<Purchase> allPurchases = new ArrayList<>();
            allPurchases.addAll(packagePurchases);
            allPurchases.addAll(multiEntryPurchases);

            // Set the combined list to the message
            message.purchases = (ArrayList<Purchase>) allPurchases;
            message.responseType = PurchaseMessage.ResponseType.PURCHASES_LIST;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = PurchaseMessage.ResponseType.PURCHASE_FAILED;
        }
    }

    private void get_all_purchases() {
        try {
            Query<Purchase> query = session.createQuery("from Purchase", Purchase.class);
            List<Purchase> purchases = query.list();
            message.purchases = (ArrayList<Purchase>) purchases;
            message.responseType = PurchaseMessage.ResponseType.PURCHASES_LIST;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = PurchaseMessage.ResponseType.PURCHASE_FAILED;
        }
    }
}
