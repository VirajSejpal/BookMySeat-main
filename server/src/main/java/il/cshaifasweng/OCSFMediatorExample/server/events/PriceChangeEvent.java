package il.cshaifasweng.OCSFMediatorExample.server.events;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;

public class PriceChangeEvent extends Event
{
    public Movie movie;

    public PriceChangeEvent(Movie movie)
    {
        this.movie = movie;
    }
}
