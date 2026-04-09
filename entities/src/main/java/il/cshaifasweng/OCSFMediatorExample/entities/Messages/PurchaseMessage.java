package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.Purchase;

import java.util.ArrayList;

public class PurchaseMessage extends Message
{
    public ArrayList<Purchase> purchases;
    public int key;
    public String stringKey;
    public RequestType requestType;
    public ResponseType responseType;

    public PurchaseMessage(MessageType messageType, RequestType requestType , int key)
    {
        //GET_PURCHASES_BY_THEATER_ID , GET_PURCHASES_BY_CUSTOMER_ID ,
        super(messageType);
        this.key = key;
        this.requestType = requestType;
    }
    public PurchaseMessage(MessageType messageType, RequestType requestType , String stringKey)
    {
        super(messageType);
        this.stringKey = stringKey;
        this.requestType = requestType;
    }
    public PurchaseMessage(MessageType messageType, RequestType requestType , Purchase purchase , int customerId)
    {
        // ADD_PURCHASE , REMOVE_PURCHASE
        super(messageType);
        this.requestType = requestType;
        this.purchases = new ArrayList<>();
        purchases.add(purchase);
        this.key = customerId;
    }
    public PurchaseMessage(MessageType messageType, RequestType requestType)
    {
        // GET_ALL_MOVIE_PACKAGES_AND_MULTI_TICKETS_PURCHASES_THIS_MONTH , GET_ALL_PURCHASES
        super(messageType);
        this.requestType = requestType;
    }

    public enum ResponseType
    {
        PURCHASE_ADDED,
        PURCHASE_REMOVED_SUCCESSFULLY,
        PURCHASE_REMOVED_UNSUCCESSFULLY,
        PURCHASES_LIST,
        PURCHASE_FOUND,
        PURCHASE_FAILED,
        PURCHASE_REMOVED,
        PURCHASE_NOT_FOUND,

    }
    public enum RequestType
    {
        ADD_PURCHASE,
        REMOVE_PURCHASE,
        GET_PURCHASES_BY_CUSTOMER_ID,
        GET_PURCHASES_BY_THEATER_ID,
        GET_ALL_MOVIE_PACKAGES_AND_MULTI_TICKETS_PURCHASES_THIS_MONTH,
        GET_ALL_PURCHASES,
        GET_ALL_MOVIE_PACKAGES_BY_CUSTOMER_ID,
    }


}
