package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.Hall;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HallMessage extends Message {
    public List<Hall> halls =new ArrayList<>();
    public List<LocalTime> availableTimes;
    public LocalDate date;
    public RequestType requestType;
    public ResponseType responseType;
    public int id;

    public HallMessage(MessageType messageType, RequestType requestType, Hall hall, LocalDate date) {
        super(messageType);
        this.halls.add(hall);
        this.requestType = requestType;
        this.date=date;
    }
    public HallMessage(MessageType messageType, RequestType requestType, int id) {
        super(messageType);
        this.id = id;
        this.requestType = requestType;
    }

    public enum RequestType {
        GET_AVAILABLE_TIMES,
        GET_ALL_HALLS_BY_THEATER_ID,
        GET_HALL_BY_ID
    }

    public enum ResponseType {
        ALL_AVAILABLE_TIMES,
        RETURN_HALLS_BY_ID,
        RETURN_HALLS_FAILED,
        RETURN_TIMES_FAILED,
        REQUESTED_HALL
    }


}
