package il.cshaifasweng.OCSFMediatorExample.server.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Seat;

import java.util.List;

public class SeatStatusChangedEvent extends Event{

    public List<Seat> seats;
    public int movieInstanceId;

    public SeatStatusChangedEvent(List<Seat> seats, int movieInstanceId)
    {
        isItSeatMessage = true;
        this.seats=seats;
        this.movieInstanceId = movieInstanceId;
    }
}
