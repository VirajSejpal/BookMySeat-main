package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Hall;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieInstanceMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message.MessageType;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovieInstanceController {

    public static void requestAllMovieInstances() {
        // Create a request to get all movie instances
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES, "");
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void requestMovieInstancesByTheaterName(String TheaterName) {
        // Create a request to get a movie instance by ID
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES_BY_THEATER_NAME, TheaterName);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void addMovieInstance(int movieId, LocalDateTime dateTime, int hallId) {
        // Since there is no constructor that directly accepts movieId, dateTime, and hallId,
        // use the constructor with an id and handle the details server-side
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.ADD_MOVIE_INSTANCE, movieId);
        requestMessage.date = dateTime;
        requestMessage.key = String.valueOf(hallId); // Reusing the key field to pass the hallId
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void updateMovieInstance(MovieInstance movieInstance) {

        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.UPDATE_MOVIE_INSTANCE, movieInstance);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void deleteMovieInstance(int id) {
        // Create a request to delete a movie instance by ID
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.DELETE_MOVIE_INSTANCE, id);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestMovieInstancesByMovieId(int id) {
        // Create a request to get a movie instance by movie ID
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID, id);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestMovieInstancesByMovieIdAndTheaterName(int movieId, String TheaterName) {
        // Create a request to get all movie instances by theater name
        System.out.println("Sending movieID= "+ movieId + " theater name = " +TheaterName);
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID_AND_THEATER_NAME, movieId, TheaterName);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestMovieInstancesByGenre(String genre) {
        // Create a request to get all movie instances by genre
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES_BY_GENRE, genre);
        SimpleClient.getClient().sendRequest(requestMessage);
    }


    public static void requestMovieInstancesByMovieIdTheaterNameDate(int movieId, String TheaterName, LocalDateTime date) {
        // Create a request to get all movie instances by theater name
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID_THEATER_ID_DATE, movieId, TheaterName, date);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestMovieInstanceAfterSelection (int movieId, String TheaterName, LocalDateTime date)
    {
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_MOVIE_INSTANCE_AFTER_SELECTION, movieId, TheaterName, date);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestMovieInstancesByName(String movieName) {
        // Create a request to get all movie instances by movie name
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES_BY_NAME, movieName);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void requestMovieInstancesBetweenDates(LocalDate before, LocalDate after) {
        // Create a request to get all movie instances by movie name
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_MOVIE_INSTANCES_BETWEEN_DATES, before, after);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void addMovieInstance (Movie movie, LocalDateTime time, Hall hall)
    {
        MovieInstance movieInstance = new MovieInstance(movie, time, hall, true);
        MovieInstanceMessage requestMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.ADD_MOVIE_INSTANCE, movieInstance);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void getMovieInstanceById(int id)
    {
        MovieInstanceMessage movieInstanceMessage = new MovieInstanceMessage(MessageType.REQUEST, MovieInstanceMessage.RequestType.GET_MOVIE_INSTANCE, id);
        SimpleClient.getClient().sendRequest(movieInstanceMessage);
    }
}