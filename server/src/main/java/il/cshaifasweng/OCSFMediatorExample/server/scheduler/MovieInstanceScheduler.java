package il.cshaifasweng.OCSFMediatorExample.server.scheduler;

import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieTicket;
import il.cshaifasweng.OCSFMediatorExample.entities.Seat;
import il.cshaifasweng.OCSFMediatorExample.server.SimpleServer;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MovieInstanceScheduler {

    private static MovieInstanceScheduler instance;
    private final ScheduledThreadPoolExecutor scheduler;
    private final Map<String, ScheduledFuture<?>> scheduledTasks;

    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String CLASS_NAME = "MovieInstanceScheduler:: ";
    /**
     * Private constructor to initialize the scheduler and task map.
     */
    private MovieInstanceScheduler() {
        scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        scheduledTasks = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the singleton instance of the MovieInstanceScheduler.
     *
     * @return The singleton instance.
     */
    public static synchronized MovieInstanceScheduler getInstance() {
        if (instance == null) {
            instance = new MovieInstanceScheduler();
        }
        return instance;
    }

    /**
     * Schedules the deactivation of all active movie instances that are yet to be screened.
     */
    public void scheduleMovieInstances() {
        System.out.println(ANSI_PURPLE + CLASS_NAME + "Scheduling movie instances..." + ANSI_RESET);
        try (Session session = SimpleServer.session.getSession().getSessionFactory().openSession()) {
            List<MovieInstance> instances = session.createQuery(
                            "FROM MovieInstance WHERE isActive = true", MovieInstance.class)
                    .list();
            System.out.println(ANSI_PURPLE + CLASS_NAME + "Found " + instances.size() + " movie instances to schedule." + ANSI_RESET);

            for (MovieInstance instance : instances) {
                LocalDateTime endTime = instance.getTime().plusMinutes(instance.getMovie().getDuration()).minusHours(3);
                if (LocalDateTime.now().isAfter(endTime)) {
                     scheduleImmediateDeactivation(instance);
                } else {
                     scheduleMovieInstanceDeactivation(instance);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Immediately deactivates a movie instance that has already ended.
     *
     * @param movieInstance The movie instance to deactivate.
     */
    private void scheduleImmediateDeactivation(MovieInstance movieInstance) {
        System.out.println(ANSI_PURPLE + CLASS_NAME + "The movie instance has already ended." + ANSI_RESET);
        try (Session session = SimpleServer.session.getSession().getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            MovieInstance managedInstance = (MovieInstance) session.merge(movieInstance);
            deactivateMovieInstance(session, managedInstance);

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedules the deactivation of a movie instance after it ends.
     *
     * @param movieInstance The movie instance to schedule.
     */
    public void scheduleMovieInstanceDeactivation(MovieInstance movieInstance) {
        LocalDateTime deactivationTime = movieInstance.getTime().plusMinutes(movieInstance.getMovie().getDuration());
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(deactivationTime)) {
            scheduleImmediateDeactivation(movieInstance);
        } else {
            long delay = Duration.between(now, deactivationTime).toMillis();
            System.out.println(ANSI_PURPLE + CLASS_NAME + "Scheduling deactivation in " + delay + " milliseconds." + ANSI_RESET);

            scheduleTask(movieInstance, delay, () -> {
                try (Session session = SimpleServer.session.getSession().getSessionFactory().openSession()) {
                    Transaction transaction = session.beginTransaction();
                    MovieInstance managedInstance = (MovieInstance) session.merge(movieInstance);
                    deactivateMovieInstance(session, managedInstance);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "deactivation");
        }
    }

    /**
     * Deactivates a movie instance and updates the database.
     *
     * @param session       The current Hibernate session.
     * @param movieInstance The movie instance to deactivate.
     */
    private void deactivateMovieInstance(Session session, MovieInstance movieInstance) {
        System.out.println(ANSI_PURPLE + CLASS_NAME + "Deactivating movie instance ID: " + movieInstance.getId() + ANSI_RESET);
        movieInstance.setIsActive(false);
        session.update(movieInstance);

        List<MovieTicket> tickets = fetchTicketsForInstance(session, movieInstance);
        for (MovieTicket movieTicket : tickets) {
            movieTicket.setisActive(false);
            session.update(movieTicket);

            Seat seat = session.get(Seat.class, movieTicket.getSeat().getId());
            seat.deleteMovieInstance(movieInstance);
            session.update(seat);
        }

        session.flush();
        session.refresh(movieInstance);
    }

    /**
     * Fetches the list of tickets associated with a given movie instance.
     *
     * @param session       The current Hibernate session.
     * @param movieInstance The movie instance.
     * @return The list of associated tickets.
     */
    private List<MovieTicket> fetchTicketsForInstance(Session session, MovieInstance movieInstance) {
        Query<MovieTicket> ticketQuery = session.createQuery(
                "FROM MovieTicket WHERE movieInstance = :movie", MovieTicket.class
        );
        ticketQuery.setParameter("movie", movieInstance);
        return ticketQuery.list();
    }

    /**
     * Schedules a task with a given delay and associates it with a movie instance.
     *
     * @param movieInstance The movie instance associated with the task.
     * @param delay         The delay before the task is executed.
     * @param task          The task to execute.
     * @param taskType      The type of the task (e.g., "deactivation").
     */
    private void scheduleTask(MovieInstance movieInstance, long delay, Runnable task, String taskType) {
        String taskId = taskType + "-" + movieInstance.getId();
        ScheduledFuture<?> futureTask = scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
        scheduledTasks.put(taskId, futureTask);
    }

    /**
     * Cancels all scheduled tasks for a movie instance.
     *
     * @param movieInstance The movie instance whose tasks should be canceled.
     */
    public void cancelMovieInstanceTasks(MovieInstance movieInstance) {
        cancelTask(movieInstance, "deactivation");
    }

    /**
     * Cancels a specific task associated with a movie instance.
     *
     * @param movieInstance The movie instance associated with the task.
     * @param taskType      The type of the task (e.g., "deactivation").
     */
    private void cancelTask(MovieInstance movieInstance, String taskType) {
        String taskId = taskType + "-" + movieInstance.getId();
        ScheduledFuture<?> futureTask = scheduledTasks.get(taskId);
        if (futureTask != null && !futureTask.isDone()) {
            futureTask.cancel(true);
            System.out.println(ANSI_PURPLE + CLASS_NAME + "Canceled " + taskType + " for movie instance ID " + movieInstance.getId() + ANSI_RESET);
        }
        scheduledTasks.remove(taskId);
    }
}
