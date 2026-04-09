package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

public class ConnectionMessage extends Message
{
    public RequestType requestType;
    public ResponseType responseType;

    public ConnectionMessage(MessageType type, RequestType requestType)
    {
        super(type);
        this.requestType = requestType;
    }

    public enum RequestType
    {
        FIRST_CONNECTION,
        DELETE_CONNECTION
    }
    public enum ResponseType
    {
        SUCCESS,
        FAILED
    }
}
