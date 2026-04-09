package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.RegisteredUserMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.RegisteredUser;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class RegisteredUserHandler extends MessageHandler
{
    private RegisteredUserMessage message;

    public RegisteredUserHandler(RegisteredUserMessage message, ConnectionToClient client, Session session)
    {
        super(client, session);
        this.message = message;
    }

    @Override
    public void setMessageTypeToResponse()
    {
        message.messageType= Message.MessageType.RESPONSE;
    }

    public void handleMessage()
    {
        switch (message.requestType)
        {
            case ADD_NEW_USER -> add_new_user();
            case GET_USER_BY_ID -> getUserByID();
            case LOWER_CARD_PACKAGE_COUNT -> lowerCardPackageCount();
        }
    }

    private void lowerCardPackageCount()
    {
        Query<RegisteredUser> query = session.createQuery("FROM RegisteredUser where  id_number= :_id_number", RegisteredUser.class);
        query.setParameter("_id_number", message.user_id);
        // Execute the query and get the result list
        RegisteredUser registeredUser = query.uniqueResult();

        registeredUser.setTicket_counter(registeredUser.getTicket_counter() - message.number_to_lower);
        session.update(registeredUser);
        session.flush();

        message.responseType = RegisteredUserMessage.ResponseType.CARD_PACKAGE_NUMBER_UPDATED;
    }

    private void getUserByID() {
        try {
            // Create an HQL query to fetch all complaints
            Query<RegisteredUser> query = session.createQuery("FROM RegisteredUser where  id_number= :_id_number", RegisteredUser.class);
            query.setParameter("_id_number", message.user_id);
            // Execute the query and get the result list
            message.registeredUser = query.uniqueResult();

            // Set the response type
            message.responseType = RegisteredUserMessage.ResponseType.RETURN_USER;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = RegisteredUserMessage.ResponseType.RETURN_USER_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

    private void add_new_user()
    {
        Query<RegisteredUser> query = session.createQuery("FROM RegisteredUser WHERE id_number = :id", RegisteredUser.class);
        query.setParameter("id", message.user_id);

        if(query.getResultList().isEmpty())
        {
            RegisteredUser new_user = new RegisteredUser(message.user_id, message.name, false, message.email, 0);
            session.save(new_user);
            session.flush();
            message.responseType = RegisteredUserMessage.ResponseType.USER_ADDED;

            message.registeredUser = new_user;
        }
        else
        {
            message.responseType = RegisteredUserMessage.ResponseType.USER_DID_NOT_ADDED;
            message.registeredUser = query.getResultList().getFirst();
        }
    }
}
