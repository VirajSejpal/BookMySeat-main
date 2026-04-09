package il.cshaifasweng.OCSFMediatorExample.server.events;

import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;

import java.io.Serializable;

public class MovieInstanceCanceledEvent extends Event
{
    public MovieInstance movieInstance;

    public MovieInstanceCanceledEvent(MovieInstance movieInstance)
    {
        this.movieInstance = movieInstance;
    }
}

