package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.PriceRequest;

import java.util.ArrayList;
import java.util.List;

public class PriceRequestMessage extends Message
{
    public List<PriceRequest> requests;
    public ResponseType responseType;
    public RequestType requestType;

    public PriceRequestMessage(MessageType type,RequestType requestType)
    {
        // GET_ALL_PRICE_REQUESTS,
        super(type);
        this.requestType = requestType;
    }
    public PriceRequestMessage(MessageType type,RequestType requestType , PriceRequest priceRequest)
    {
        // CREATE_NEW_PRICE_REQUEST, APPROVE_PRICE_REQUEST,DECLINE_PRICE_REQUEST
        super(type);
        this.requestType = requestType;
        this.requests= new ArrayList<>();
        this.requests.add(priceRequest);
    }

    public enum RequestType
    {

        PRICE_REQUEST_DECIDE,
        PRICE_REQUEST_MESSAGE_FAILED,
        APPROVE_PRICE_REQUEST,
        DECLINE_PRICE_REQUEST,
        CREATE_NEW_PRICE_REQUEST,
        GET_ALL_PRICE_REQUESTS

    }
    public enum ResponseType
    {
        MOVIE_PRICE_CHANGED,
        MOVIE_PRICE_NOT_CHANGED,
        NEW_REQUEST,
        ALL_REQUESTS,
        PRICE_REQUEST_MESSAGE_FAILED
    }

}
