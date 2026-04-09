package il.cshaifasweng.OCSFMediatorExample.server.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;

public class MovieEvent extends Event
{
    public Movie movie;
    public String action;
    public MovieEvent(Movie movie, String action)
    {
        this.movie=movie;
        this.action = action;
    }
}
