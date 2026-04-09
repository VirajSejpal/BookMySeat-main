package il.cshaifasweng.OCSFMediatorExample.entities.Messages;


import il.cshaifasweng.OCSFMediatorExample.entities.RegisteredUser;

public class RegisteredUserMessage extends Message
{
    public String user_id;
    public String name;
    public String email;
    public int number_to_lower;

    public RequestType requestType;
    public ResponseType responseType;

    public RegisteredUser registeredUser = null;

    public RegisteredUserMessage(MessageType messageType,String user_id, String firstName, String lastName, String email, RequestType requestType)
    {
        super(messageType);
        this.user_id = user_id;
        this.name = firstName + " " + lastName;
        this.email = email;
        this.requestType = requestType;
    }
    public RegisteredUserMessage(MessageType messageType,String user_id, RequestType requestType)
    {
        super(messageType);
        this.user_id = user_id;
        this.requestType = requestType;
    }

    public RegisteredUserMessage(MessageType messageType,String user_id, RequestType requestType , int number_to_lower)
    {
        super(messageType);
        this.user_id = user_id;
        this.requestType = requestType;
        this.number_to_lower = number_to_lower;
    }

    public enum RequestType
    {
        ADD_NEW_USER,
        GET_USER_BY_ID,
        LOWER_CARD_PACKAGE_COUNT
    }
    public enum ResponseType
    {
        USER_ADDED,
        USER_DID_NOT_ADDED,
        RETURN_USER,
        RETURN_USER_FAILED,
        CARD_PACKAGE_NUMBER_UPDATED
    }
}
