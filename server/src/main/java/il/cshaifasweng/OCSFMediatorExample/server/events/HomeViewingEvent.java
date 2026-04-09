package il.cshaifasweng.OCSFMediatorExample.server.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;

public class HomeViewingEvent extends Event{
    public Movie movie;
    public String action;
    public int id;
    public HomeViewingEvent(Movie movie, String action)
    {
        this.movie = movie;
        this.action = action;
    }
    public HomeViewingEvent(int id, String action) {
        this.id = id;
        this.action = action;
    }

}
