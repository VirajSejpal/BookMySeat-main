package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.HomeViewingPackageInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ReportMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieTicket;
import il.cshaifasweng.OCSFMediatorExample.entities.MultiEntryTicket;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportHandler extends MessageHandler {
    private ReportMessage message;

    public ReportHandler(ReportMessage message, ConnectionToClient client, Session session) {
        super(client, session);
        this.message = message;
    }

    @Override
    public void setMessageTypeToResponse() {
        message.messageType = Message.MessageType.RESPONSE;
    }

    public void handleMessage() {
        switch (message.getRequestType()) {
            case TICKET_SALES_REPORT -> handleTicketSalesReport();
            case PACKAGE_SALES_REPORT -> handlePackageSalesReport();
            case MULTI_ENTRY_SALES_REPORT -> handleMultiEntrySalesReport();
            case COMPLAINT_STATUS_REPORT -> handleComplaintStatusReport();
            case THEATER_TICKET_SALES_REPORT -> handleTheaterTicketSalesReport();
        }
    }

    // Handle package sales report
    private void handlePackageSalesReport() {
        try {
            YearMonth reportMonth = YearMonth.of(message.getYear(), message.getMonth());
            LocalDate startOfMonth = reportMonth.atDay(1);
            LocalDate endOfMonth = reportMonth.atEndOfMonth();

            // Query for HomeViewingPackageInstance
            Query<HomeViewingPackageInstance> packageQuery = session.createQuery(
                    "from HomeViewingPackageInstance where purchaseDate between :start and :end",
                    HomeViewingPackageInstance.class);
            packageQuery.setParameter("start", startOfMonth.atStartOfDay());
            packageQuery.setParameter("end", endOfMonth.atTime(23, 59, 59));
            List<HomeViewingPackageInstance> packagePurchases = packageQuery.list();

            message.purchases.addAll(packagePurchases);
            message.responseType = ReportMessage.ResponseType.PACKAGE_SALES_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = ReportMessage.ResponseType.PACKAGE_SALES_DATA;
        }
    }

    // Handle multi-entry sales report
    private void handleMultiEntrySalesReport() {
        try {
            YearMonth reportMonth = YearMonth.of(message.getYear(), message.getMonth());
            LocalDate startOfMonth = reportMonth.atDay(1);
            LocalDate endOfMonth = reportMonth.atEndOfMonth();

             Query<MultiEntryTicket> query = session.createQuery(
                    "FROM MultiEntryTicket WHERE purchaseDate BETWEEN :start AND :end",
                    MultiEntryTicket.class);
            query.setParameter("start", startOfMonth.atStartOfDay());
            query.setParameter("end", endOfMonth.atTime(23, 59, 59));

             List<MultiEntryTicket> multiEntrySales = query.list();

             message.purchases.addAll(multiEntrySales);
            message.responseType = ReportMessage.ResponseType.MULTI_ENTRY_SALES_DATA;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = ReportMessage.ResponseType.MULTI_ENTRY_SALES_DATA;
        }
    }


    // Handle complaint status report
    private void handleComplaintStatusReport() {
        try {
            YearMonth reportMonth = YearMonth.of(message.getYear(), message.getMonth());
            LocalDate startOfMonth = reportMonth.atDay(1);
            LocalDate endOfMonth = reportMonth.atEndOfMonth();

             Query<Complaint> query = session.createQuery(
                    "FROM Complaint WHERE creationDate BETWEEN :start AND :end",
                    Complaint.class);
            query.setParameter("start", startOfMonth.atStartOfDay());
            query.setParameter("end", endOfMonth.atTime(23, 59, 59));

             List<Complaint> complaints = query.list();

             message.complaints.addAll(complaints);
            message.responseType = ReportMessage.ResponseType.COMPLAINT_STATUS_DATA;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = ReportMessage.ResponseType.COMPLAINT_STATUS_DATA;
        }
    }

    // Handle ticket sales report
    private void handleTicketSalesReport() {
        try {
            YearMonth reportMonth = YearMonth.of(message.getYear(), message.getMonth());
            LocalDate startOfMonth = reportMonth.atDay(1);
            LocalDate endOfMonth = reportMonth.atEndOfMonth();

            // Query to get sales data, grouped by theater location and day
            Query<Object[]> query = session.createQuery(
                    "SELECT movieInstance.hall.theater.location, DATE(purchaseDate), COUNT(id) " +
                            "FROM MovieTicket " +
                            "WHERE purchaseDate BETWEEN :start AND :end " +
                            "GROUP BY movieInstance.hall.theater.location, DATE(purchaseDate) " +
                            "ORDER BY DATE(purchaseDate)", Object[].class
            );
            query.setParameter("start", startOfMonth.atStartOfDay());
            query.setParameter("end", endOfMonth.atTime(23, 59, 59));

            List<Object[]> results = query.list();

            for (Object[] result : results) {
                String theaterLocation = (String) result[0];
                java.sql.Date sqlDate = (java.sql.Date) result[1]; // Cast to java.sql.Date
                LocalDate purchaseDate = sqlDate.toLocalDate(); // Convert to LocalDate

                Long salesCount = (Long) result[2];

                message.getSalesData().putIfAbsent(theaterLocation, new HashMap<>());
                message.getSalesData().get(theaterLocation).put(purchaseDate.atStartOfDay(), salesCount);
            }

            message.responseType = ReportMessage.ResponseType.TICKET_SALES_DATA;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = ReportMessage.ResponseType.ERROR;
        }
    }

    // Handle theater ticket sales report
    private void handleTheaterTicketSalesReport() {
        try {
            YearMonth reportMonth = YearMonth.of(message.getYear(), message.getMonth());
            LocalDate startOfMonth = reportMonth.atDay(1);
            LocalDate endOfMonth = reportMonth.atEndOfMonth();

            // Adjust the query to filter by theater
            Query<MovieTicket> query = session.createQuery(
                    "FROM MovieTicket mt WHERE mt.movieInstance.hall.theater.id = :theaterId " +
                            "AND mt.purchaseDate BETWEEN :start AND :end",
                    MovieTicket.class);
            query.setParameter("theaterId", message.theaterID);
            query.setParameter("start", startOfMonth.atStartOfDay());
            query.setParameter("end", endOfMonth.atTime(23, 59, 59));

            List<MovieTicket> theaterSales = query.list();

            message.purchases.addAll(theaterSales);
            message.responseType = ReportMessage.ResponseType.THEATER_TICKET_SALES_DATA;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = ReportMessage.ResponseType.ERROR;
        }
    }
}
