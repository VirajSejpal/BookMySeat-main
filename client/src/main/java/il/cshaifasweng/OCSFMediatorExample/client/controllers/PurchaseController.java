package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.PurchaseMessage;

import java.time.LocalDateTime;

public class PurchaseController
{
    public static void AddMovieTicket(LocalDateTime purchaseDate, RegisteredUser owner, String purchaseValidation, MovieInstance movieInstance, Seat seat)
    {
        MovieTicket newTicket = new MovieTicket(purchaseDate, owner, purchaseValidation, movieInstance, seat,true,movieInstance.getMovie().getTheaterPrice());
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.ADD_PURCHASE,newTicket,owner.getId());
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void AddMultiEntryTicket(LocalDateTime purchaseDate, RegisteredUser owner, String purchaseValidation)
    {
        MultiEntryTicket newTicket = new MultiEntryTicket(purchaseDate, owner, purchaseValidation,true,2800);
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.ADD_PURCHASE,newTicket,owner.getId());
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void AddHomeViewing(LocalDateTime purchaseDate, RegisteredUser owner, String purchaseValidation,Movie movie, LocalDateTime viewingDate, String link)
    {
        viewingDate.plusHours(3);//time difference
        HomeViewingPackageInstance newTicket = new HomeViewingPackageInstance(purchaseDate, owner, purchaseValidation, movie, viewingDate,true,link, movie.getHomeViewingPrice());
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.ADD_PURCHASE,newTicket,owner.getId());
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void RemovePurchase(Purchase purchase)
    {
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.REMOVE_PURCHASE,purchase, 0);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void GetPurchasesByCustomerID(String CustomerId)
    {
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.GET_PURCHASES_BY_CUSTOMER_ID,CustomerId);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void GetPurchasesByTheaterID(int TheaterId)
    {
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.GET_PURCHASES_BY_THEATER_ID,TheaterId);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void GetAllPurchases()
    {
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.GET_ALL_PURCHASES);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void GetMoviePackagesByCustomerID(int CustomerId)
    {
        PurchaseMessage requestMessage = new PurchaseMessage(Message.MessageType.REQUEST, PurchaseMessage.RequestType.GET_ALL_MOVIE_PACKAGES_BY_CUSTOMER_ID,CustomerId);
        SimpleClient.getClient().sendRequest(requestMessage);
    }



}
