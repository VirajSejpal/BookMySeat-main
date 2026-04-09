package il.cshaifasweng.OCSFMediatorExample.client.controllers;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message.MessageType;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage.RequestType;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;

import java.util.List;

public class MovieController {

    public static void requestAllMovies() {
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, RequestType.GET_ALL_MOVIES);
        SimpleClient.getClient().sendRequest(requestMessage);
    }


    public static void addMovie(String hebrewName, String info, String producer, String englishName, String mainActors, String image, byte[] imageBytes, Movie.StreamingType streamingType, int duration, int theaterPrice, int homeViewingPrice, String genre, Movie.Availability availability) {
        Movie movie = new Movie(hebrewName, info, producer, englishName, mainActors, image, streamingType, duration, theaterPrice, homeViewingPrice, genre, availability, false);
        movie.setImageBytes(imageBytes);
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, movie, RequestType.ADD_MOVIE);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void getMoviesPresentedInTheaterContentManager()
    {
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, RequestType.GET_MOVIES_PRESENTED_IN_THEATER_CONTENT_MANAGER);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void updateMovie(Movie movie, String hebrewName, String info, String producer, String englishName, String mainActors, String image, byte[] imageBytes, Movie.StreamingType streamingType, int duration, String genre, Movie.Availability availability) {
        movie.setHebrewName(hebrewName);
        movie.setInfo(info);
        movie.setProducer(producer);
        movie.setEnglishName(englishName);
        movie.setMainActors(mainActors);
        movie.setImage(image);
        movie.setImageBytes(imageBytes);
        movie.setStreamingType(streamingType);
        movie.setDuration(duration);
        movie.setGenre(genre);
        movie.setActive(availability);

        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, movie, RequestType.UPDATE_MOVIE);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    // id of the movie in mySql
    public static void deleteMovie(int id) {
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, RequestType.DEACTIVATE_MOVIE, id);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void getMoviesPresentedInTheater()
    {
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, RequestType.GET_MOVIES_PRESENTED_IN_THEATER);
        SimpleClient.getClient().sendRequest(requestMessage);
    }

    public static void getMoviesPresentedInHomeViewing()
    {
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, RequestType.GET_MOVIES_PRESENTED_IN_HOME_VIEWING);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void getMoviesFilteredByScreeningTypeAndGenre(String screening, String Genre)
    {
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, RequestType.GET_MOVIES_FILTERED_BY_SCREENING_TYPE_AND_GENRE, screening, Genre);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
    public static void getUpcomingMovies()
    {
        MovieMessage requestMessage = new MovieMessage(MessageType.REQUEST, RequestType.GET_UPCOMING_MOVIES);
        SimpleClient.getClient().sendRequest(requestMessage);
    }
}
