package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.Messages.LoginMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.RegisteredUser;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class LoginHandler extends MessageHandler {

    private LoginMessage message;

    public LoginHandler(LoginMessage message, ConnectionToClient client, Session session) {
        super(client, session);
        this.message = message;
    }

    public void handleMessage() {
        switch (message.requestType) {
            case LOGIN -> login();
            case LOGOUT -> logout();
        }
    }

    @Override
    public void setMessageTypeToResponse() {
        message.messageType = Message.MessageType.RESPONSE;
    }

    protected void login() {
        try {
            Query<RegisteredUser> query = session.createQuery("FROM RegisteredUser WHERE id_number = :id", RegisteredUser.class);
            query.setParameter("id", message.id);

            RegisteredUser user = query.uniqueResult();

            if (user != null) {
                if (user.isOnline()) {
                    message.responseType = LoginMessage.ResponseType.ALREADY_LOGGED;
                } else {
                    user.setOnline(true);
                    session.update(user);
                    session.flush();
                    message.responseType = LoginMessage.ResponseType.LOGIN_SUCCESFUL;
                }
            } else {
                message.responseType = LoginMessage.ResponseType.LOGIN_FAILED;
            }
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            message.responseType = LoginMessage.ResponseType.LOGIN_FAILED;
            e.printStackTrace();
        }
    }

    protected void logout() {
        try {
            Query<RegisteredUser> query = session.createQuery("FROM RegisteredUser WHERE id_number = :id", RegisteredUser.class);
            query.setParameter("id", message.id);

            RegisteredUser user = query.uniqueResult();

            if (user != null && user.isOnline()) {
                user.setOnline(false);
                session.update(user);
                session.flush();
                message.responseType = LoginMessage.ResponseType.LOGOUT_SUCCESFUL;
            } else {
                message.responseType = LoginMessage.ResponseType.LOGOUT_FAILED;
            }
        } catch (Exception e) {
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            message.responseType = LoginMessage.ResponseType.LOGOUT_FAILED;
            e.printStackTrace();
        }
    }
}
