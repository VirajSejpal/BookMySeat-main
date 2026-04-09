package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ReportMessage;

public class ReportController {

    public static void requestTheaterTicketSalesReport(int year, int month, int theaterId) {

         ReportMessage requestMessage = new ReportMessage(
                ReportMessage.MessageType.REQUEST,
                ReportMessage.RequestType.THEATER_TICKET_SALES_REPORT,
                year,
                month,
                 theaterId
        );
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestTicketSalesReport(int year, int month) {
         ReportMessage requestMessage = new ReportMessage(
                ReportMessage.MessageType.REQUEST,
                ReportMessage.RequestType.TICKET_SALES_REPORT,
                year,
                month,
                0
        );
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestPackageSalesReport(int year, int month, int theaterId) {
         ReportMessage requestMessage = new ReportMessage(
                ReportMessage.MessageType.REQUEST,
                ReportMessage.RequestType.PACKAGE_SALES_REPORT,
                year,
                month,
                 theaterId
        );
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestMultiEntrySalesReport(int year, int month, int theaterId) {
         ReportMessage requestMessage = new ReportMessage(
                ReportMessage.MessageType.REQUEST,
                ReportMessage.RequestType.MULTI_ENTRY_SALES_REPORT,
                year,
                month,
                 theaterId
        );
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestComplaintStatusReport(int year, int month,  int theaterId) {
         ReportMessage requestMessage = new ReportMessage(
                ReportMessage.MessageType.REQUEST,
                ReportMessage.RequestType.COMPLAINT_STATUS_REPORT,
                year,
                month,
                 theaterId
        );
        SimpleClient.getClient().sendRequest(requestMessage);
    }
}
