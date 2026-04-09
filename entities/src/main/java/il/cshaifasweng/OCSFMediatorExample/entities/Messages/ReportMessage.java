package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Purchase;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportMessage extends Message {
    public List<Purchase> purchases;
    public List<Complaint> complaints;
    public Map<String, Map<LocalDateTime, Long>> salesData; // Updated to LocalDateTime
    public RequestType requestType;
    public ResponseType responseType;
    public int year;
    public int month;
    public int theaterID;

    public ReportMessage(MessageType messageType, RequestType requestType, int year, int month, int theaterID) {
        super(messageType);
        this.requestType = requestType;
        this.year = year;
        this.month = month;
        this.theaterID = theaterID;
        this.purchases = new ArrayList<>();
        this.complaints = new ArrayList<>();
        this.salesData = new HashMap<>();
    }

    public ReportMessage(MessageType messageType, ResponseType responseType, List<Purchase> purchases, List<Complaint> complaints) {
        super(messageType);
        this.responseType = responseType;
        this.purchases = purchases != null ? purchases : new ArrayList<>();
        this.complaints = complaints != null ? complaints : new ArrayList<>();
        this.salesData = new HashMap<>();
    }

    // Getters and Setters
    public List<Purchase> getPurchases() {
        return purchases;
    }

    public List<Complaint> getComplaints() {
        return complaints;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }


    public Map<String, Map<LocalDateTime, Long>> getSalesData() { // Updated to LocalDateTime
        return salesData;
    }

    public void setSalesData(Map<String, Map<LocalDateTime, Long>> salesData) { // Updated to LocalDateTime
        this.salesData = salesData;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public enum RequestType {
        TICKET_SALES_REPORT,
        PACKAGE_SALES_REPORT,
        MULTI_ENTRY_SALES_REPORT,
        COMPLAINT_STATUS_REPORT,
        THEATER_TICKET_SALES_REPORT
    }

    public enum ResponseType {
        TICKET_SALES_DATA,
        PACKAGE_SALES_DATA,
        MULTI_ENTRY_SALES_DATA,
        COMPLAINT_STATUS_DATA,
        THEATER_TICKET_SALES_DATA,
        ERROR
    }
}
