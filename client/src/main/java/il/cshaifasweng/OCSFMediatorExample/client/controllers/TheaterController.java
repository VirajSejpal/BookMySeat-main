package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.TheaterMessage;

public class TheaterController {

    public static void getAllTheaters()
    {
        TheaterMessage requestMessage = new TheaterMessage(Message.MessageType.REQUEST, TheaterMessage.RequestType.GET_ALL_THEATERS);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void getTheaterById(int theaterId)
    {
        TheaterMessage requestMessage = new TheaterMessage(Message.MessageType.REQUEST,theaterId, TheaterMessage.RequestType.GET_ALL_THEATERS);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void getTheaterNameByTheaterManagerID (String managerID)
    {
        TheaterMessage requestMessage = new TheaterMessage(Message.MessageType.REQUEST,managerID, TheaterMessage.RequestType.GET_THEATER_BY_MANAGER_ID);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
}
