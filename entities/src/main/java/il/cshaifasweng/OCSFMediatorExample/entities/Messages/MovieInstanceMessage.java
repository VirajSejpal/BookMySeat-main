package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovieInstanceMessage extends Message
{
    public List<MovieInstance> movies = new ArrayList<>();
    public String key;
    public int id;
    public String theaterName;
    public LocalDateTime date;
    public RequestType requestType;
    public ResponseType responseType;
    public LocalDate beforeDate;
    public LocalDate afterDate;


    public MovieInstanceMessage(MessageType messageType, RequestType requestType , MovieInstance movieInstance)
    {
        //ADD_MOVIE_INSTANCE,UPDATE_MOVIE_INSTANCE
        super(messageType);
        movies.add(movieInstance);
        //this.theater_name = movieInstance.getTheater();
        this.requestType = requestType;
    }
    public MovieInstanceMessage(MessageType messageType, RequestType requestType, int id)
    {
        //GET_MOVIE_INSTANCE,DELETE_MOVIE_INSTANCE

        super(messageType);
        this.id = id;
        this.requestType = requestType;
    }
    public MovieInstanceMessage(MessageType messageType, RequestType requestType, int movieID, String theaterName)
    {
        super(messageType);
        this.id = movieID;
        this.requestType = requestType;
        this.theaterName= theaterName;
    }
    public MovieInstanceMessage(MessageType messageType, RequestType requestType, int movieID, String theaterName, LocalDateTime date)
    {
        super(messageType);
        this.id = movieID;
        this.requestType = requestType;
        this.theaterName= theaterName;
        this.date = date;
    }

    public MovieInstanceMessage(MessageType messageType, RequestType requestType, String key)
    {
        // GET_ALL_MOVIE_INSTANCES , GET_ALL_MOVIE_INSTANCES_BY_THEATER_NAME, GET_ALL_MOVIE_INSTANCES_BY_GENRE , GET_ALL_MOVIE_INSTANCES_BY_NAME
        super(messageType);
        this.requestType = requestType;
        this.key = key;
    }

    public MovieInstanceMessage(MessageType messageType, RequestType requestType, LocalDateTime date)
    {
        // GET_ALL_MOVIE_INSTANCES_BY_DATE
        super(messageType);
        this.requestType = requestType;
        this.date = date;
    }

    public MovieInstanceMessage(MessageType messageType, RequestType requestType, LocalDate before, LocalDate after)
    {
        super(messageType);
        this.requestType = requestType;
        this.beforeDate = before;
        this.afterDate = after;
    }


    public enum ResponseType
    {
        FILLTERD_LIST,
        MOVIE_INSTANCE_ADDED,
        MOVIE_INSTANCE_REMOVED,
        MOVIE_INSTANCE_UPDATED,
        MOVIE_INSTANCE,
        MOVIE_INSTANCE_MESSAGE_FAILED

    }
    public enum RequestType
    {
        ADD_MOVIE_INSTANCE,
        GET_MOVIE_INSTANCE,
        DELETE_MOVIE_INSTANCE,
        UPDATE_MOVIE_INSTANCE,
        GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID,
        GET_ALL_MOVIE_INSTANCES_BY_GENRE,
        GET_ALL_MOVIE_INSTANCES,
        GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID_AND_THEATER_NAME,
        GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID_THEATER_ID_DATE,
        GET_ALL_MOVIE_INSTANCES_BY_NAME,
        GET_ALL_MOVIE_INSTANCES_BY_THEATER_NAME,
        GET_MOVIE_INSTANCES_BETWEEN_DATES,
        GET_MOVIE_INSTANCE_AFTER_SELECTION


    }
}
