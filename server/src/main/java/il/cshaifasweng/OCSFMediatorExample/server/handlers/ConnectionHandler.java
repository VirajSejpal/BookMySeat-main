package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ConnectionMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.SubscribedClient;
import org.hibernate.Session;

import java.util.ArrayList;

public class ConnectionHandler extends MessageHandler
{
    private final ConnectionMessage message;
    public ArrayList<SubscribedClient> clients;

    public ConnectionHandler(ConnectionMessage message, ConnectionToClient client, Session session, ArrayList<SubscribedClient> clients)
    {
        super(client, session);
        this.message = message;
        this.clients = clients;
    }
    public void handleMessage()
    {
        switch (message.requestType)
        {
            case FIRST_CONNECTION -> first_connection();
            case DELETE_CONNECTION -> delete_connection();
        }
    }

    public void setMessageTypeToResponse()
    {
        message.messageType= Message.MessageType.RESPONSE;
    }

    public void first_connection()
    {
        SubscribedClient connection = new SubscribedClient(client);
        clients.add(connection);
        System.out.println("client added successfully, the size of SubscribedClients are: " + clients.size());
    }
    public void delete_connection()
    {
        for(int i = 0; i < clients.size(); i++)
            if(clients.get(i).getClient().equals(client)) //ver2: clients.get(i).getClient().threadId() == client.threadId()
            {
                clients.remove(i);
                System.out.println("client deleted successfully, the size of SubscribedClients are: " + clients.size());
                return;
            }
        System.out.println("client did not delete successfully");
    }


}
