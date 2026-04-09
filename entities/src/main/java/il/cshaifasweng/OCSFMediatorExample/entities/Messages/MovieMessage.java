package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.Movie;

import java.util.ArrayList;
import java.util.List;

public class MovieMessage extends Message
{
    public List<Movie> movies = new ArrayList<>();
    public int id;
    public RequestType requestType;
    public ResponseType responseType;
    public String Genre;
    public Movie.StreamingType Screening;

    public MovieMessage(MessageType messageType,Movie movie,RequestType requestType)
    {
        // ADD_MOVIE ,UPDATE_MOVIE
        super(messageType);
        movies.add(movie);
        this.requestType = requestType;

    }
    public MovieMessage(MessageType messageType,RequestType requestType)
    {
        // GET_ALL_MOVIES
        super(messageType);
        this.requestType = requestType;
     }
    public MovieMessage(MessageType messageType,RequestType requestType, int id)
    {
        // DELETE_MOVIE
        super(messageType);
        this.requestType = requestType;
        this.id = id;
    }
    public MovieMessage(MessageType messageType,RequestType requestType, String screening, String Genre)
    {
        // GET_MOVIES_FILTERED_BY_SCREENING_TYPE_AND_GENRE
        super(messageType);
        this.requestType = requestType;
        this.Genre = Genre;
        switch (screening)
        {
            case "Home Viewing":
                Screening= Movie.StreamingType.HOME_VIEWING;
                break;
            case "Theater":
                Screening= Movie.StreamingType.THEATER_VIEWING;
                break;
            case "All":
                Screening= Movie.StreamingType.BOTH;
                break;
        }
    }

    public enum ResponseType
    {
        MOVIE_ADDED,
        MOVIE_UPDATED,
        MOVIE_DELETED,
        MOVIE_NOT_ADDED,
        MOVIE_ALREADY_EXISTS,
        MOVIE_NOT_UPDATED,
        MOVIE_NOT_DELETED,
        RETURN_MOVIES,
        MOVIE_MESSAGE_FAILED

    }
    public enum RequestType
    {
        ADD_MOVIE,
        DEACTIVATE_MOVIE,
        GET_ALL_MOVIES,
        UPDATE_MOVIE,
        GET_MOVIES_PRESENTED_IN_THEATER,
        GET_MOVIES_PRESENTED_IN_HOME_VIEWING,
        GET_MOVIES_FILTERED_BY_SCREENING_TYPE_AND_GENRE,
        GET_UPCOMING_MOVIES,
        GET_MOVIES_PRESENTED_IN_THEATER_CONTENT_MANAGER

    }

}
