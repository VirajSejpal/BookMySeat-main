package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.SeatMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message.MessageType;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.Seat;

import java.util.List;

public class SeatController {

     public static void getAllSeatsByHall(int hallId) {
        SeatMessage requestMessage = new SeatMessage(hallId, MessageType.REQUEST, SeatMessage.RequestType.GET_ALL_SEAT_BY_HALL);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

     public static void reserveSeats(List<Seat> seats, MovieInstance movieInstance) {
        SeatMessage requestMessage = new SeatMessage(movieInstance, MessageType.REQUEST, SeatMessage.RequestType.SEATS_RESERVED , seats);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

     public static void cancelSeatReservation(List<Seat> seats, MovieInstance movieInstance) {
        SeatMessage requestMessage = new SeatMessage(movieInstance, MessageType.REQUEST, SeatMessage.RequestType.SEATS_CANCELATION, seats);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
}
