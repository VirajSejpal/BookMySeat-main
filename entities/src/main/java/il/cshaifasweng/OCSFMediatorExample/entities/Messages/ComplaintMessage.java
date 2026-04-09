package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Theater;

import java.util.ArrayList;
import java.util.List;

public class ComplaintMessage extends Message
{
    public List<Complaint> compliants;
    public String customerId;
    public Theater theater;
    public ResponseType responseType;
    public RequestType requestType;

    public ComplaintMessage(){}

    /**
     * Ctor that support GET_ALL_COMPLIANTS.
     * @param messageType is the MessageType
     * @param requestType is what request we want
     */
    public ComplaintMessage(MessageType messageType, RequestType requestType)
    {
        //GET_ALL_COMPLIANTS
        super(messageType);
        this.requestType = requestType;
    }
    /**
     * Ctor that support GET_COMPLIANT_BY_CUSTOMER_ID.
     * @param messageType is the MessageType
     * @param requestType is what request we want
     * @param customerId is the id of the user
     */
    public ComplaintMessage(MessageType messageType, RequestType requestType, String customerId)
    {
        //GET_COMPLIANT_BY_CUSTOMER_ID
        super(messageType);
        this.requestType = requestType;
        this.customerId = customerId;
    }
    /**
     * Ctor that support GET_COMPLIANT_BY_CUSTOMER_ID.
     * @param messageType is the MessageType
     * @param requestType is what request we want
     * @param theater is the theater that we want all complaints associate with it
     */
    public ComplaintMessage(MessageType messageType, RequestType requestType , Theater theater)
    {
        //GET_COMPLIANT_BY_THEATER
        super(messageType);
        this.requestType = requestType;
        this.theater = theater;
    }
    /**
     * Ctor that support GET_COMPLIANT_BY_CUSTOMER_ID.
     * @param messageType is the MessageType
     * @param requestType is what request we want
     * @param compliant is a complaint
     */
    public ComplaintMessage(MessageType messageType, RequestType requestType, Complaint compliant)
    {
        // ADD_COMPLIANT, ANSWER_COMPLIANT
        super(messageType);
        this.requestType = requestType;
        this.compliants = new ArrayList<>();
        this.compliants.add(compliant);
    }

    public enum ResponseType
    {
        FILTERED_COMPLAINTS_LIST,
        COMPLIANT_ADDED,
        COMPLIANT_MESSAGE_FAILED,
        COMPLIANT_WAS_ANSWERED,
    }
    public enum RequestType
    {
        GET_ALL_COMPLAINTS,
        GET_COMPLAINTS_BY_CUSTOMER_ID,
        GET_COMPLAINTS_BY_THEATER,
        ADD_COMPLIANT,
        ANSWER_COMPLIANT,
        GET_OPEN_COMPLAINTS
    }
}
