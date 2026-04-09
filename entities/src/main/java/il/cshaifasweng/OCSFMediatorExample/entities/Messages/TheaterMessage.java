package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.Theater;

import java.util.ArrayList;

public class TheaterMessage extends Message
{
    public ArrayList<Theater> theaterList;
    public int id;
    public String managerID;
    public RequestType requestType;
    public ResponseType responseType;

    public TheaterMessage(MessageType messageType , int id , RequestType requestType )
    {
        // THEATER_BY_ID
        super(messageType);
        this.id= id;
        this.requestType = requestType;
    }
    public TheaterMessage(MessageType messageType , String managerID , RequestType requestType )
    {
        // THEATER_BY_ID
        super(messageType);
        this.id= id;
        this.requestType = requestType;
        this.managerID=managerID;
    }
    public TheaterMessage(MessageType messageType , RequestType requestType )
    {
        // GET_ALL_THEATERS
        super(messageType);
        this.requestType = requestType;
    }
    public enum ResponseType
    {
        ALL_THEATERS,
        THEATER_BY_ID,
        RETURN_THEATER

    }
    public enum RequestType
    {
        GET_ALL_THEATERS,
        GET_THEATER,
        GET_THEATER_BY_MANAGER_ID
    }

}
