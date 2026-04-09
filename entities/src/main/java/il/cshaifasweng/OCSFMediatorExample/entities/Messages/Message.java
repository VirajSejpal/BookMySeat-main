package il.cshaifasweng.OCSFMediatorExample.entities.Messages;


import java.io.Serializable;


public class Message implements Serializable {

    public MessageType messageType;

    public Message(){}

    public Message(MessageType message_type)
    {
        this.messageType = message_type;
    }

    public enum MessageType
    {
        REQUEST,
        RESPONSE
    }
}


