package il.cshaifasweng.OCSFMediatorExample.server.scheduler;

import il.cshaifasweng.OCSFMediatorExample.entities.HomeViewingPackageInstance;
import il.cshaifasweng.OCSFMediatorExample.server.SimpleServer;
import il.cshaifasweng.OCSFMediatorExample.server.events.HomeViewingEvent;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.EmailSender;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * This class is responsible for scheduling the activation, deactivation,
 * and email notifications for home viewing packages.
 */
public class HomeViewingScheduler {

    private static HomeViewingScheduler instance;
    private static ScheduledThreadPoolExecutor scheduler = null;
    private static Map<String, ScheduledFuture<?>> scheduledTasks = Map.of();

    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String CLASS_NAME = "HomeViewingScheduler:: ";
    private static final SimpleServer server = SimpleServer.getServer();

    private HomeViewingScheduler() {
        scheduler = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        scheduledTasks = new ConcurrentHashMap<>();
    }

    /**
     * Retrieves the singleton instance of the HomeViewingScheduler.
     *
     * @return The singleton instance.
     */
    public static synchronized HomeViewingScheduler getInstance() {
        if (instance == null) {
            instance = new HomeViewingScheduler();
        }
        return instance;
    }

    /**
     * Schedules the activation and deactivation of all active home viewing packages
     * that have not yet been activated or have ended.
     */
    public static void scheduleHomeViewingPackages() {
        System.out.println(ANSI_GREEN + CLASS_NAME + "Scheduling home viewing packages..." + ANSI_RESET);
        try (Session session = SimpleServer.session.getSession().getSessionFactory().openSession()) {
            List<HomeViewingPackageInstance> packages = session.createQuery(
                    "FROM HomeViewingPackageInstance WHERE isActive=true", HomeViewingPackageInstance.class).list();
            System.out.println(ANSI_GREEN + CLASS_NAME + "Found " + packages.size() + " home viewing packages to schedule." + ANSI_RESET);

            // Iterate over the packages to either deactivate past ones or schedule future ones
            for (HomeViewingPackageInstance pkg : packages) {
                LocalDateTime viewingEndTime = pkg.getViewingDate().plusWeeks(1).minusHours(3); // Link is active for one week after viewing date
                if (LocalDateTime.now().isAfter(viewingEndTime)) {
                    deactivateViewingLink(pkg);
                } else if (!pkg.isLinkActive()){
                    scheduleLinkActivation(pkg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Schedules the activation, email notification, and deactivation of a viewing link.
     *
     * @param booking The home viewing package instance to schedule.
     */
    public static void scheduleLinkActivation(HomeViewingPackageInstance booking) {
        System.out.println(ANSI_GREEN + CLASS_NAME + "Scheduling link activation for home viewing package ID: " + booking.getId() + ANSI_RESET);

        long linkActivationDelay = Duration.between(LocalDateTime.now(), booking.getViewingDate().minusHours(4)).toMillis();
        LocalDateTime deactivationTime = booking.getViewingDate().plusWeeks(1); // Deactivate after one week
        long deactivationDelay = Duration.between(LocalDateTime.now(), deactivationTime).toMillis();

        if (linkActivationDelay <= 0) {
            activateViewingLink(booking); // Activate immediately if the activation date is in the past
        } else {
            scheduleTask(booking, linkActivationDelay, () -> activateViewingLink(booking), "activation");
        }

        scheduleEmailNotification(booking);
        scheduleScreeningAvailible(booking);
        scheduleTask(booking, deactivationDelay, () -> deactivateViewingLink(booking), "deactivation");

        System.out.println(ANSI_GREEN + CLASS_NAME + "Scheduled activation, email, and deactivation tasks for home viewing package ID: " +
                booking.getId() + " Movie Name: " + booking.getMovie().getEnglishName() + ANSI_RESET);
    }

    private static void scheduleTask(HomeViewingPackageInstance booking, long delay, Runnable task, String taskType) {
        String taskId = taskType + "-" + booking.getId();
        ScheduledFuture<?> futureTask = scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
        scheduledTasks.put(taskId, futureTask);
    }

    private static void scheduleEmailNotification(HomeViewingPackageInstance booking) {
        long emailDelay = Duration.between(LocalDateTime.now(), booking.getViewingDate().minusHours(4)).toMillis();
        scheduleTask(booking, emailDelay, () -> sendEmailNotification(booking), "email");
    }

    private static void scheduleScreeningAvailible(HomeViewingPackageInstance booking) {
        long emailDelay = Duration.between(LocalDateTime.now(), booking.getViewingDate().minusHours(3)).toMillis();
        scheduleTask(booking, emailDelay, () -> sendAvailableEvent(booking), "event");
    }

    private static void sendAvailableEvent(HomeViewingPackageInstance booking) {
        System.out.println("Home Viewing just became available");
        server.sendToAllClients(new HomeViewingEvent(Integer.parseInt(booking.getOwner().getId_number()), "Home Viewing Available"));
    }

    /**
     * Activates the viewing link for a home viewing package.
     *
     * @param booking The home viewing package instance to activate.
     */
    private static void activateViewingLink(HomeViewingPackageInstance booking) {
        booking.activateLink();
        updateBookingInDatabase(booking);
    }

    /**
     * Deactivates the viewing link for a home viewing package and updates the database.
     *
     * @param booking The home viewing package instance to deactivate.
     */
    public static void deactivateViewingLink(HomeViewingPackageInstance booking) {
        System.out.println(ANSI_GREEN + CLASS_NAME + "Deactivating viewing link for home viewing package ID: " + booking.getId() + ANSI_RESET);
        booking.deactivateLink();
        booking.setisActive(false);
        updateBookingInDatabase(booking);
    }

    /**
     * Updates the database entry for a home viewing package instance.
     *
     * @param booking The home viewing package instance to update.
     */
    private static void updateBookingInDatabase(HomeViewingPackageInstance booking) {
        try {
            boolean active = true;
            if(!SimpleServer.session.getTransaction().isActive()) {
                active = false;
                SimpleServer.session.beginTransaction();
            }
            SimpleServer.session.update(booking);
            if(!active) SimpleServer.session.getTransaction().commit();
            System.out.println(ANSI_GREEN + CLASS_NAME + "Updated booking in database for booking ID " + booking.getId() + ANSI_RESET);
            System.out.println("link just became available");
            server.sendToAllClients(new HomeViewingEvent(Integer.parseInt(booking.getOwner().getId_number()), "Home Viewing Available"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an email notification about an upcoming viewing link.
     *
     * @param booking The home viewing package instance for which the email should be sent.
     */
    private static void sendEmailNotification(HomeViewingPackageInstance booking) {
        String email = booking.getOwner().getEmail();
        String movieLink = booking.getLink();
        EmailSender.sendEmail(
                email,
                "Your Movie Link from BookMySeat",
                "Dear Customer,\n\nYour movie is ready to watch at "+booking.getActivationDate().toLocalDate()+", "+booking.getActivationDate().toLocalTime().minusHours(3)
                        +"\nYour link is " + booking.getLink()
                        +". \n\nThank you,\nBookMySeat"
        );
        System.out.println(ANSI_GREEN + CLASS_NAME + "Sending movie link email to " + email + " at " + LocalDateTime.now() + ANSI_RESET);
    }

    /**
     * Cancels all scheduled tasks for a home viewing package instance.
     *
     * @param booking The home viewing package instance whose tasks should be canceled.
     */
    public void cancelScheduledTasks(HomeViewingPackageInstance booking) {
        cancelTask(booking, "activation");
        cancelTask(booking, "email");
        cancelTask(booking, "deactivation");
    }

    private void cancelTask(HomeViewingPackageInstance booking, String taskType) {
        String taskId = taskType + "-" + booking.getId();
        ScheduledFuture<?> futureTask = scheduledTasks.get(taskId);
        if (futureTask != null && !futureTask.isDone()) {
            futureTask.cancel(true);
            System.out.println(ANSI_GREEN + CLASS_NAME + "Canceled " + taskType + " for booking ID " + booking.getId() + ANSI_RESET);
        }
        scheduledTasks.remove(taskId);
    }
}
