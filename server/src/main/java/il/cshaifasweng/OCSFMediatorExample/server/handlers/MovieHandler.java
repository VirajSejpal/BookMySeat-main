package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieInstanceMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.RegisteredUser;
import il.cshaifasweng.OCSFMediatorExample.server.SimpleServer;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.EmailSender;
import il.cshaifasweng.OCSFMediatorExample.server.scheduler.OrderScheduler;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MovieHandler extends MessageHandler
{
    private MovieMessage message;

    public MovieHandler(MovieMessage message, ConnectionToClient client, Session session)
    {
        super(client,session);
        this.message = message;
    }

    public void handleMessage()
    {
        switch (message.requestType)
        {
            case ADD_MOVIE -> add_movie();
            case DEACTIVATE_MOVIE -> deactivate_movie();
            case GET_ALL_MOVIES -> get_all_movies();
            case UPDATE_MOVIE -> update_movie();
            case GET_MOVIES_PRESENTED_IN_THEATER -> getMoviesPresentedInTheater();
            case GET_MOVIES_PRESENTED_IN_HOME_VIEWING -> getMoviesPresentedInHomeViewing();
            case GET_MOVIES_FILTERED_BY_SCREENING_TYPE_AND_GENRE -> getMoviesFilteredByScreeningTypeAndGenre();
            case GET_UPCOMING_MOVIES -> getUpcomingMovies();
            case GET_MOVIES_PRESENTED_IN_THEATER_CONTENT_MANAGER -> getMoviesPresentedInTheaterContentManager();

        }
    }

    private void getUpcomingMovies() {
        try {
            // Create an HQL query to fetch movies with streamingType THEATER_VIEWING or BOTH
            Query<Movie> query = session.createQuery(
                    "FROM Movie WHERE  available = :available",
                    Movie.class
            );
            query.setParameter("available", Movie.Availability.COMING_SOON);

            // Execute the query and get the result list
            message.movies = query.getResultList();

            // Set the response type
            message.responseType = MovieMessage.ResponseType.RETURN_MOVIES;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

    private void getMoviesPresentedInTheaterContentManager() {
        try {
            // Create an HQL query to fetch movies with streamingType HOME_VIEWING or BOTH
            Query<Movie> query = session.createQuery(
                    "FROM Movie WHERE (streamingType = :theater OR streamingType = :both) AND available = :available",
                    Movie.class
            );
            query.setParameter("theater", Movie.StreamingType.THEATER_VIEWING);
            query.setParameter("both", Movie.StreamingType.BOTH);
            query.setParameter("available", Movie.Availability.AVAILABLE);

            // Execute the query and get the result list
            message.movies = query.getResultList();

            // Set the response type
            message.responseType = MovieMessage.ResponseType.RETURN_MOVIES;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }



    private void getMoviesFilteredByScreeningTypeAndGenre() {
        System.out.println("filter by " + message.Screening + " and " + message.Genre);

        try {
            if (message.Screening == Movie.StreamingType.THEATER_VIEWING) {
                String hql = "FROM Movie WHERE (streamingType = :theater OR streamingType = :both) AND available = :available";
                if (!"all".equalsIgnoreCase(message.Genre)) {
                    hql += " and genre=:genre";
                }

                Query<Movie> query = session.createQuery(hql, Movie.class);
                query.setParameter("theater", Movie.StreamingType.THEATER_VIEWING);
                query.setParameter("both", Movie.StreamingType.BOTH);
                query.setParameter("available", Movie.Availability.AVAILABLE);

                if (!"all".equalsIgnoreCase(message.Genre)) {
                    query.setParameter("genre", message.Genre);
                }

                message.movies = query.getResultList();
                message.responseType = MovieMessage.ResponseType.RETURN_MOVIES;
                return;
            }

            String hql = "FROM Movie WHERE (streamingType =:home or streamingType =:both) and available =:available";
            if (!"all".equalsIgnoreCase(message.Genre)) {
                hql += " and genre=:genre";
            }

            Query<Movie> query = session.createQuery(hql, Movie.class);
            query.setParameter("home", Movie.StreamingType.HOME_VIEWING);
            query.setParameter("both", Movie.StreamingType.BOTH);
            query.setParameter("available", Movie.Availability.AVAILABLE);
            if (!"all".equalsIgnoreCase(message.Genre)) {
                query.setParameter("genre", message.Genre);
            }

            message.movies = query.getResultList();
            message.responseType = MovieMessage.ResponseType.RETURN_MOVIES;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }



    private void getMoviesPresentedInHomeViewing() {
        try {
            // Create an HQL query to fetch movies with streamingType HOME_VIEWING or BOTH
            Query<Movie> query = session.createQuery(
                    "FROM Movie WHERE (streamingType = :home OR streamingType = :both) AND available = :available",
                    Movie.class
            );
            query.setParameter("home", Movie.StreamingType.HOME_VIEWING);
            query.setParameter("both", Movie.StreamingType.BOTH);
            query.setParameter("available", Movie.Availability.AVAILABLE);


            // Execute the query and get the result list
            message.movies = query.getResultList();

            // Set the response type
            message.responseType = MovieMessage.ResponseType.RETURN_MOVIES;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

    private void getMoviesPresentedInTheater() {
        try {
            // Create an HQL query to fetch movies with streamingType THEATER_VIEWING or BOTH

            Query<Movie> query = session.createQuery(
                    "FROM Movie WHERE (streamingType = :theater OR streamingType = :both) AND available = :available",
                    Movie.class
            );
            query.setParameter("theater", Movie.StreamingType.THEATER_VIEWING);
            query.setParameter("both", Movie.StreamingType.BOTH);
            query.setParameter("available", Movie.Availability.AVAILABLE);

            message.movies = query.getResultList();

            // Set the response type
            message.responseType = MovieMessage.ResponseType.RETURN_MOVIES;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

    @Override
    public void setMessageTypeToResponse()
    {
        message.messageType= Message.MessageType.RESPONSE;
    }

    private void add_movie()
    {
        if(message.movies.getFirst() != null) {
            // Create an HQL query to fetch all complaints
            // Searching if the movie is existed in DB
            Query<Movie> query = session.createQuery("FROM Movie WHERE (englishName = :_englishName or hebrewName = :_hebrewName) and  available=:_availability", Movie.class);
            query.setParameter("_englishName", message.movies.getFirst().getEnglishName());
            query.setParameter("_hebrewName", message.movies.getFirst().getHebrewName());
            query.setParameter("_availability", Movie.Availability.AVAILABLE);

            List<Movie> movies = query.getResultList();

            if (movies.isEmpty()) {
                session.save(message.movies.getFirst());
                session.flush();
                message.responseType = MovieMessage.ResponseType.MOVIE_ADDED;
            } else
                message.responseType = MovieMessage.ResponseType.MOVIE_ALREADY_EXISTS;

        }
        else // if we don't have any movie to add
            message.responseType = MovieMessage.ResponseType.MOVIE_NOT_ADDED;
    }

    private void deactivate_movie() {
        Query<Movie> query = session.createQuery("FROM Movie WHERE id = :_id", Movie.class);
        query.setParameter("_id", message.id);

        Movie movie = query.uniqueResult();
        message.movies.addFirst(movie);

        if (movie != null) {
            System.out.println(movie.getId());
            movie.setActive(Movie.Availability.NOT_AVAILABLE);
            session.update(movie);
            session.flush();
            message.responseType = MovieMessage.ResponseType.MOVIE_UPDATED;
        } else {
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
        }
    }
    private void get_all_movies()
    {
        try {
            Query<Movie> query = session.createQuery("FROM Movie where available= :_available or available =:_available2", Movie.class);
            query.setParameter("_available", Movie.Availability.AVAILABLE);
            query.setParameter("_available2", Movie.Availability.COMING_SOON);
            message.movies = query.getResultList();
            message.responseType = MovieMessage.ResponseType.RETURN_MOVIES;

        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }
    private void update_movie() {
        try {
            // Get the movie ID from the incoming message
            int movieId = message.movies.getFirst().getId();

            // Retrieve the current persistent instance of the movie from the session
            Movie persistentMovie = session.get(Movie.class, movieId);

            if (persistentMovie != null) {
                // Update the persistent movie with the new values from the message
                persistentMovie.setEnglishName(message.movies.getFirst().getEnglishName());
                persistentMovie.setHebrewName(message.movies.getFirst().getHebrewName());
                persistentMovie.setProducer(message.movies.getFirst().getProducer());
                persistentMovie.setDuration(message.movies.getFirst().getDuration());
                persistentMovie.setTheaterPrice(message.movies.getFirst().getTheaterPrice());
                persistentMovie.setHomeViewingPrice(message.movies.getFirst().getHomeViewingPrice());
                persistentMovie.setGenre(message.movies.getFirst().getGenre());
                persistentMovie.setStreamingType(message.movies.getFirst().getStreamingType());
                persistentMovie.setInfo(message.movies.getFirst().getInfo());
                String joinedString = String.join("_", message.movies.getFirst().getMainActors());
                persistentMovie.setMainActors(joinedString);
                persistentMovie.setActive(message.movies.getFirst().getAvailability());

                if (!Arrays.equals(persistentMovie.getImageBytes(), message.movies.getFirst().getImageBytes())) {
                    persistentMovie.setImage(message.movies.getFirst().getImage());
                    persistentMovie.setImageBytes(message.movies.getFirst().getImageBytes());
                }
                // Save the changes
                session.update(persistentMovie);
                session.flush();
                message.responseType = MovieMessage.ResponseType.MOVIE_UPDATED;
            } else {
                message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = MovieMessage.ResponseType.MOVIE_MESSAGE_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }
}
