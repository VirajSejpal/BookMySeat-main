package il.cshaifasweng.OCSFMediatorExample.server.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;

public class ComplaintEvent extends Event{
    public Complaint complaint;

    public ComplaintEvent (Complaint complaint)
    {
        this.complaint = complaint;
    }
}
