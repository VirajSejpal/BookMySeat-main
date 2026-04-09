package il.cshaifasweng.OCSFMediatorExample.server.handlers;

import il.cshaifasweng.OCSFMediatorExample.entities.Hall;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.HallMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HallHandler extends MessageHandler {
    private HallMessage message;

    public HallHandler(HallMessage message, ConnectionToClient client, Session session) {
        super(client, session);
        this.message = message;
    }

    public void handleMessage() {
        switch (message.requestType) {
            case GET_AVAILABLE_TIMES -> get_available_times_for_hall_and_date();
            case GET_ALL_HALLS_BY_THEATER_ID -> get_all_halls_by_theater_id();
            case GET_HALL_BY_ID -> get_hall_by_id();
        }
    }

    private void get_hall_by_id() {
        Hall hall = session.get(Hall.class,message.id);
        message.halls.add(hall);
        message.responseType = HallMessage.ResponseType.REQUESTED_HALL;
    }

    @Override
    public void setMessageTypeToResponse() {
        message.messageType = HallMessage.MessageType.RESPONSE;
    }

    private void get_all_halls_by_theater_id()
    {
        try {
            Query<Hall> query = session.createQuery("FROM Hall where theater.id= :id", Hall.class);
            query.setParameter("id", message.id);
            message.halls = query.getResultList();
            message.responseType = HallMessage.ResponseType.RETURN_HALLS_BY_ID;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = HallMessage.ResponseType.RETURN_HALLS_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }

    private void get_available_times_for_hall_and_date() {
        try {
            // Query to fetch all MovieInstance records for the specified hall
            Query<MovieInstance> query = session.createQuery("FROM MovieInstance WHERE hall.id = :id", MovieInstance.class);
            query.setParameter("id", message.halls.getFirst().getId());

            // Fetch all movie instances for the hall
            List<MovieInstance> allMovieInstances = query.getResultList();

            // Filter movie instances based on matching date
            List<MovieInstance> filteredMovieInstances = allMovieInstances.stream()
                    .filter(instance -> instance.getTime().toLocalDate().equals(message.date))
                    .collect(Collectors.toList());

            message.availableTimes = getAvailableTimes(filteredMovieInstances);
            message.responseType = HallMessage.ResponseType.ALL_AVAILABLE_TIMES;
        } catch (Exception e) {
            e.printStackTrace();
            message.responseType = HallMessage.ResponseType.RETURN_TIMES_FAILED;
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
        }
    }


    public List<LocalTime> getAvailableTimes(List<MovieInstance> movieInstances) {
        List<LocalTime> allTimes = new ArrayList<>();

        // Generate all round times between 12:00 and 23:00
        for (int hour = 12; hour <= 23; hour++) {
            allTimes.add(LocalTime.of(hour, 0));
        }
        for(MovieInstance movieInstance : movieInstances)
            System.out.println(movieInstance.getTime() + ", Duration: "+movieInstance.getMovie().getDuration());
        // Filter out times that are taken by existing movie instances
        List<LocalTime> availableTimes = allTimes.stream().filter(time -> {
            for (MovieInstance instance : movieInstances) {
                LocalTime instanceStartTime = instance.getTime().toLocalTime().minusHours(3);
                LocalTime instanceEndTime = instanceStartTime.plusMinutes(instance.getMovie().getDuration());

                // Check if the time conflicts with any existing movie instance
                if (!time.isBefore(instanceStartTime) && time.isBefore(instanceEndTime)) {
                    return false; // Time is within an existing movie instance
                }
            }
            return true; // Time is free
        }).collect(Collectors.toList());
        for(LocalTime time : availableTimes)
            System.out.println(time);
        return availableTimes;
    }
}
