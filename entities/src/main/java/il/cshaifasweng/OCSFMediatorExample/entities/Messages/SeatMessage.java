package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.Seat;

import java.util.ArrayList;
import java.util.List;

public class SeatMessage extends Message
{
    public List<Seat> hallSeats;
    public RequestType requestType;
    public ResponseType responseType;
    public MovieInstance movieInstance;
    public int hallId;

    public SeatMessage(MovieInstance movieInstance, MessageType messageType, RequestType requestType, List<Seat> seats)
    {
        //SEATS_RESERVED , SEATS_CANCELATION

        super(messageType);
        this.movieInstance = movieInstance;
        this.requestType = requestType;
        hallSeats = seats;

    }
    public SeatMessage(int hall,MessageType messageType, RequestType requestType)
    {
        //GET_ALL_SEAT_BY_HALL
        super(messageType);
        this.hallId = hall;
        this.requestType = requestType;
    }

    public enum ResponseType
    {
        SEATS_WAS_RESERVED,
        SEATS_HAS_BEEN_CANCELED,
        SEATS_LIST,
        SEATS_IS_ALREADY_TAKEN,
        MESSAGE_FAIL
    }
    public enum RequestType
    {
        SEATS_RESERVED,
        SEATS_CANCELATION,
        GET_ALL_SEAT_BY_HALL
    }
}
