package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ComplaintMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.PurchaseMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message.MessageType;
import il.cshaifasweng.OCSFMediatorExample.entities.Theater;

public class ReportsPageController {

    public static void requestAllComplaints() {
        ComplaintMessage requestMessage = new ComplaintMessage(MessageType.REQUEST, ComplaintMessage.RequestType.GET_ALL_COMPLAINTS);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestComplaintsByCustomerId(String customerId) {
        ComplaintMessage requestMessage = new ComplaintMessage(MessageType.REQUEST, ComplaintMessage.RequestType.GET_COMPLAINTS_BY_CUSTOMER_ID, customerId);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestComplaintsByTheater(Theater theater) {
        ComplaintMessage requestMessage = new ComplaintMessage(MessageType.REQUEST, ComplaintMessage.RequestType.GET_COMPLAINTS_BY_THEATER, theater);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestAllPurchases() {
        PurchaseMessage requestMessage = new PurchaseMessage(MessageType.REQUEST, PurchaseMessage.RequestType.GET_ALL_PURCHASES, 0);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestPurchasesByCustomerId(int customerId) {
        PurchaseMessage requestMessage = new PurchaseMessage(MessageType.REQUEST, PurchaseMessage.RequestType.GET_PURCHASES_BY_CUSTOMER_ID, customerId);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestPurchasesByTheaterId(int theaterId) {
        PurchaseMessage requestMessage = new PurchaseMessage(MessageType.REQUEST, PurchaseMessage.RequestType.GET_PURCHASES_BY_THEATER_ID, theaterId);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestAllMoviePackagesAndMultiTicketsPurchasesThisMonth() {
        PurchaseMessage requestMessage = new PurchaseMessage(MessageType.REQUEST, PurchaseMessage.RequestType.GET_ALL_MOVIE_PACKAGES_AND_MULTI_TICKETS_PURCHASES_THIS_MONTH);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestAllMoviePackagesByCustomerId(int customerId) {
        PurchaseMessage requestMessage = new PurchaseMessage(MessageType.REQUEST, PurchaseMessage.RequestType.GET_ALL_MOVIE_PACKAGES_BY_CUSTOMER_ID, customerId);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
}