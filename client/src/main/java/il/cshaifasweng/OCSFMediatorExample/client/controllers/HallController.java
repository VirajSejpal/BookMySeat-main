package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Hall;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.HallMessage;

import java.time.LocalDate;
import java.util.List;

public class HallController {


    public static void requestAvailableTimes(Hall hall, LocalDate date) {
        HallMessage requestMessage = new HallMessage(HallMessage.MessageType.REQUEST, HallMessage.RequestType.GET_AVAILABLE_TIMES, hall, date);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestHallsByTheaterID(int theaterID) {
        HallMessage requestMessage = new HallMessage(HallMessage.MessageType.REQUEST, HallMessage.RequestType.GET_ALL_HALLS_BY_THEATER_ID, theaterID);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void requestHallByID(int hallID) {
        HallMessage requestMessage = new HallMessage(HallMessage.MessageType.REQUEST, HallMessage.RequestType.GET_HALL_BY_ID, hallID);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

}
