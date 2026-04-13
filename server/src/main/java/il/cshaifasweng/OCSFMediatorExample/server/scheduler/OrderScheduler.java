package il.cshaifasweng.OCSFMediatorExample.server.scheduler;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.EmailSender;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class OrderScheduler {

    private static OrderScheduler instance; // Singleton instance
    private final ExecutorService emailExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    private final Map<String, ScheduledFuture<?>> scheduledTasks; // Map to keep track of scheduled tasks

    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String CLASS_NAME = "OrderScheduler:: ";

    private OrderScheduler() {
        emailExecutor = Executors.newFixedThreadPool(5);
        scheduledExecutor = Executors.newScheduledThreadPool(5);
        scheduledTasks = new ConcurrentHashMap<>();
    }

    /**
     * Returns the singleton instance of the OrderScheduler.
     *
     * @return the singleton instance of the OrderScheduler.
     */
    public static synchronized OrderScheduler getInstance() {
        if (instance == null) {
            instance = new OrderScheduler();
        }
        return instance;
    }

    /**
     * Schedules a purchase confirmation email to be sent to the customer.
     *
     * @param purchase The purchase for which the confirmation email should be sent.
     */
    public void schedulePurchaseConfirmation(Purchase purchase) {
        System.out.println(ANSI_BLUE +CLASS_NAME+ "Scheduling purchase confirmation email for purchase ID: " + purchase.getId() + ANSI_RESET);
        emailExecutor.submit(() -> sendPurchaseConfirmationEmail(purchase));
    }

    /**
     * Schedules a cancellation email to be sent to the customer for the given purchase.
     *
     * @param purchase The purchase for which the cancellation email should be sent.
     */
    public void scheduleEmailCancellation(Purchase purchase) {
        System.out.println(ANSI_BLUE +CLASS_NAME+ "Scheduling cancellation email for purchase ID: " + purchase.getId() + ANSI_RESET);
        emailExecutor.submit(() -> sendCancellationEmail(purchase));
    }

    /**
     * Schedules emails to be sent to customers informing them that their movie screening has been canceled.
     *
     * @param customerData  A list of MovieTicket objects representing the customers whose screenings were canceled.
     * @param movieInstance The movie instance that was canceled.
     */
    public void scheduleEmailsForCanceledScreening(List<MovieTicket> customerData, MovieInstance movieInstance) {
        System.out.println(ANSI_BLUE +CLASS_NAME+ "Scheduling emails for canceled screening of movie instance ID: " + movieInstance.getId() + ANSI_RESET);
        for (MovieTicket customer : customerData) {
            String email = customer.getOwner().getEmail();
            String name = customer.getOwner().getName();

            emailExecutor.submit(() -> {
                System.out.println(ANSI_BLUE +CLASS_NAME+ "Sending cancellation email to " + email + " for movie: " + movieInstance.getMovie().getEnglishName() + ANSI_RESET);
                EmailSender.sendEmail(email, "Canceled Ticket from BookMySeat",
                        String.format("Dear %s,\n\nYour ticket for the movie '%s' scheduled on %s has been canceled. We apologize for the inconvenience. You will receive a full refund.\n\nThank you,\nBookMySeat",
                                name,
                                movieInstance.getMovie().getEnglishName(),
                                movieInstance.getTime().toLocalDate().toString()));
                EmailSender.sendEmail("bookmyseatofficial@gmail.com", "Canceled Ticket from BookMySeat",
                        String.format("Dear %s,\n\nYour ticket for the movie '%s' scheduled on %s has been canceled. We apologize for the inconvenience. You will receive a full refund.\n\nThank you,\nBookMySeat",
                                name,
                                movieInstance.getMovie().getEnglishName(),
                                movieInstance.getTime().toLocalDate().toString()));
            });
        }
    }

    /**
     * Schedules emails to be sent to customers informing them that the details of their movie screening have been updated.
     *
     * @param customerData  A list of customer data containing their email addresses and names.
     * @param movieInstance The movie instance whose screening details were updated.
     */
    public void scheduleEmailsForUpdatedScreening(List<Object[]> customerData, MovieInstance movieInstance) {
        System.out.println(ANSI_BLUE +CLASS_NAME+ "Scheduling emails for updated screening of movie instance ID: " + movieInstance.getId() + ANSI_RESET);
        for (Object[] customer : customerData) {
            String email = (String) customer[0];
            String name = (String) customer[1];

            emailExecutor.submit(() -> {
                System.out.println(ANSI_BLUE +CLASS_NAME+ "Sending updated screening email to " + email + " for movie: " + movieInstance.getMovie().getEnglishName() + ANSI_RESET);
                EmailSender.sendEmail(
                        email,
                        "Updated Movie Screening Notification from BookMySeat",
                        String.format(
                                "Dear %s,\n\nWe would like to inform you that the screening of '%s' that you booked has been updated. " +
                                        "The updated details are as follows:\n\n" +
                                        "Movie: %s\n" +
                                        "Date & Time: %s\n" +
                                        "Location: %s, Hall %d\n\n" +
                                        "Please contact us if you have any questions.\n\n" +
                                        "Thank you,\nBookMySeat",
                                name,
                                movieInstance.getMovie().getEnglishName(),
                                movieInstance.getMovie().getEnglishName(),
                                movieInstance.getTime().toString(),
                                movieInstance.getHall().getTheater().getLocation(),
                                movieInstance.getHall().getId()
                        )
                );
                EmailSender.sendEmail(
                        "bookmyseatofficial@gmail.com",
                        "Updated Movie Screening Notification from BookMySeat",
                        String.format(
                                "Dear %s,\n\nWe would like to inform you that the screening of '%s' that you booked has been updated. " +
                                        "The updated details are as follows:\n\n" +
                                        "Movie: %s\n" +
                                        "Date & Time: %s\n" +
                                        "Location: %s, Hall %d\n\n" +
                                        "Please contact us if you have any questions.\n\n" +
                                        "Thank you,\nBookMySeat",
                                name,
                                movieInstance.getMovie().getEnglishName(),
                                movieInstance.getMovie().getEnglishName(),
                                movieInstance.getTime().toString(),
                                movieInstance.getHall().getTheater().getLocation(),
                                movieInstance.getHall().getId()
                        )
                );
            });
        }
    }

    /**
     * Sends notification emails to registered users about a new movie being added.
     *
     * @param movie  The movie that is being added.
     * @param users  The list of registered users to notify.
     */
    public void notifyNewMovie(Movie movie, List<RegisteredUser> users) {
        System.out.println(ANSI_BLUE +CLASS_NAME+ "Scheduling notification emails for new movie: " + movie.getEnglishName() + ANSI_RESET);
        for (RegisteredUser user : users) {
            emailExecutor.submit(() -> {
                String emailBody = String.format(
                        "Dear %s,\n\nWe are excited to announce a new movie in our collection: %s (%s).\n" +
                                "Produced by %s, this %s film features an incredible cast including %s.\n" +
                                "With a runtime of %d minutes, this movie is available for %s.\n\n" +
                                "Don't miss out on this exciting release!\n\nBest regards,\nYour Cinema Team",
                        user.getName(),
                        movie.getEnglishName(), movie.getHebrewName(),
                        movie.getProducer(),
                        movie.getGenre(), String.join(", ", movie.getMainActors()),
                        movie.getDuration(), movie.getStreamingType()
                );

                System.out.println(ANSI_BLUE + "Sending new movie email to " + user.getEmail() + " for movie: " + movie.getEnglishName() + ANSI_RESET);
                EmailSender.sendEmail(user.getEmail(), "New Movie Available: " + movie.getEnglishName(), emailBody);
                // Optionally, email the company
                EmailSender.sendEmail("bookmyseatofficial@gmail.com", "New Movie Available: " + movie.getEnglishName(), emailBody);
            });
        }
    }

    /**
     * Schedules notification emails to be sent to registered users one day before a movie is screened.
     *
     * @param movie           The movie that is being screened.
     * @param users           The list of registered users to notify.
     * @param screeningDateTime The date and time of the movie screening.
     */
    public void scheduleNotifyNewMovieOneDayBefore(Movie movie, List<RegisteredUser> users, LocalDateTime screeningDateTime) {
        System.out.println(ANSI_BLUE +CLASS_NAME+ "Scheduling notification email one day before screening for movie: " + movie.getEnglishName() + ANSI_RESET);
        // Calculate the delay until 24 hours before the screening
        long delay = Duration.between(LocalDateTime.now(), screeningDateTime.minusDays(1)).toMillis();
        String taskKey = "notify-" + movie.getId();

        ScheduledFuture<?> scheduledTask = scheduledExecutor.schedule(() -> notifyNewMovie(movie, users), delay, TimeUnit.MILLISECONDS);
        scheduledTasks.put(taskKey, scheduledTask);
    }

    /**
     * Cancels the scheduled notification email for a new movie, if it exists.
     *
     * @param movie The movie for which the notification email should be canceled.
     */
    public void cancelScheduledNotifyNewMovie(Movie movie) {
        String taskKey = "notify-" + movie.getId();
        ScheduledFuture<?> scheduledTask = scheduledTasks.get(taskKey);
        if (scheduledTask != null && !scheduledTask.isDone()) {
            System.out.println(ANSI_BLUE +CLASS_NAME+ "Canceling scheduled notification email for movie: " + movie.getEnglishName() + ANSI_RESET);
            scheduledTask.cancel(true);
            scheduledTasks.remove(taskKey);
            System.out.println(ANSI_BLUE + "Canceled scheduled notification for movie: " + movie.getEnglishName() + ANSI_RESET);
        }
    }

    /**
     * Sends a purchase confirmation email to the customer for the given purchase.
     *
     * @param purchase The purchase for which the confirmation email should be sent.
     */
    private void sendPurchaseConfirmationEmail(Purchase purchase) {
        String purchaseType = "";
        String additionalInfo = "";
        String paymentType = "";

        if (purchase instanceof MovieTicket) {

            MovieTicket ticket = (MovieTicket) purchase;
            MovieInstance movieInstance = ticket.getMovieInstance();
            purchaseType = movieInstance.getMovie().getEnglishName() + " ticket";
            if(purchase.getPurchaseValidation().equals("Card Package"))
                paymentType = "Payed via Card Package";
            else paymentType = String.valueOf(movieInstance.getMovie().getTheaterPrice());
            additionalInfo = String.format("\n\nTicket Details:\n" +
                            "Theater: %s\n" +
                            "Hall: %s\n" +
                            "Seat Row: %s\n" +
                            "Seat Col: %s\n" +
                            "Date and Time: %s\n"+
                            "Price Paid: %s",
                    movieInstance.getHall().getTheater().getLocation(),
                    movieInstance.getHall().getName(),
                    ticket.getSeat().getCol(),
                    ticket.getSeat().getRow(),
                    movieInstance.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                    paymentType);
        } else if (purchase instanceof HomeViewingPackageInstance) {
            HomeViewingPackageInstance homeViewing = (HomeViewingPackageInstance) purchase;
            purchaseType = homeViewing.getMovie().getEnglishName() + " home viewing package";
            additionalInfo = String.format("\n\nHome Viewing Details:\n" +
                            "Available Date: %s\n" +
                            "Available Time: %s\n" +
                            "Price Paid: %s\n"+
                    "You will receive a link for the home viewing via email one hour before your requested viewing time.",
                    homeViewing.getActivationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                    homeViewing.getActivationDate().format(DateTimeFormatter.ofPattern("HH:mm")),
                    homeViewing.getMovie().getHomeViewingPrice());
        }
        else if (purchase instanceof MultiEntryTicket) {
            MultiEntryTicket homeViewing = (MultiEntryTicket) purchase;
            purchaseType = " Multi Entry Ticket (20 Tickets)";
        }


        String confirmation = String.format("Dear %s,\n\n" +
                        "Thank you for your recent purchase of %s. " +
                        "Your purchase has been confirmed and processed successfully. %s" +
                        "\n\nIf you have any questions or need further assistance, please contact us.\n\n" +
                        "Thank you for choosing BookMySeat.\n\nBest regards,\nBookMySeat Team",
                purchase.getOwner().getName(), purchaseType, additionalInfo);

        System.out.println(ANSI_BLUE + "Sending purchase confirmation email to " + purchase.getOwner().getEmail() + ANSI_RESET);
        // Send the email to the customer
        EmailSender.sendEmail(purchase.getOwner().getEmail(), "Confirmation of Your Purchase from BookMySeat", confirmation);

        // Optionally, email the company
        EmailSender.sendEmail("bookmyseatofficial@gmail.com", "New Purchase Confirmation from BookMySeat", confirmation);
    }

    /**
     * Schedules a welcome email to be sent to the new user.
     *
     * @param user The new user to whom the welcome email should be sent.
     */
    public void scheduleWelcomeEmail(RegisteredUser user) {
        System.out.println(ANSI_BLUE + CLASS_NAME + "Scheduling welcome email for user: " + user.getName() + ANSI_RESET);
        emailExecutor.submit(() -> sendWelcomeEmail(user));
    }

    private void sendWelcomeEmail(RegisteredUser user) {
        String welcomeMessage = String.format("Dear %s,\n\n" +
                        "Welcome to BookMySeat! We are thrilled to have you as a registered member.\n\n" +
                        "As a registered user, you can easily book movie tickets, purchase home viewing packages, " +
                        "and manage your orders directly from your account. Keep an eye on your inbox for exciting " +
                        "updates and new movie releases.\n\n" +
                        "If you have any questions or need assistance, feel free to contact our support team.\n\n" +
                        "Thank you for joining us!\n\nBest regards,\nBookMySeat Team",
                user.getName());

        System.out.println(ANSI_BLUE + "Sending welcome email to " + user.getEmail() + ANSI_RESET);
        EmailSender.sendEmail(user.getEmail(), "Welcome to BookMySeat!", welcomeMessage);

        // Optionally, email the company
        EmailSender.sendEmail("bookmyseatofficial@gmail.com", "New User Registered: " + user.getName(), welcomeMessage);
    }

    /**
     * Schedules a login notification email to be sent to the user.
     *
     * @param user The user who has logged in.
     */
    public void scheduleLoginEmail(RegisteredUser user) {
        System.out.println(ANSI_BLUE + CLASS_NAME + "Scheduling login notification email for user: " + user.getName() + ANSI_RESET);
        emailExecutor.submit(() -> sendLoginEmail(user));
    }

    private void sendLoginEmail(RegisteredUser user) {
        String loginMessage = String.format("Dear %s,\n\n" +
                        "This is a notification that your account on BookMySeat has just been accessed.\n\n" +
                        "If this was you, you can safely ignore this email. However, if you did not login recently, " +
                        "please contact our support team immediately as your account security might be compromised.\n\n" +
                        "Login Time: %s\n\n" +
                        "Best regards,\nBookMySeat Security Team",
                user.getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        System.out.println(ANSI_BLUE + "Sending login notification email to " + user.getEmail() + ANSI_RESET);
        EmailSender.sendEmail(user.getEmail(), "Security Alert: New Login to BookMySeat", loginMessage);

        // Optionally, email the company
        EmailSender.sendEmail("bookmyseatofficial@gmail.com", "User Login Notification: " + user.getName(), loginMessage);
    }

    /**
     * Sends a cancellation email to the customer for the given purchase.
     *
     * @param purchase The purchase for which the cancellation email should be sent.
     */
    private void sendCancellationEmail(Purchase purchase) {
        String purchaseType = (purchase instanceof MovieTicket)
                ? ((MovieTicket) purchase).getMovieInstance().getMovie().getEnglishName() + " ticket"
                : ((HomeViewingPackageInstance) purchase).getMovie().getEnglishName() + " home viewing package";

        String confirmation = String.format("Dear %s,\n\n" +
                        "We wanted to confirm that your recent purchase of %s has been successfully canceled. " +
                        "If you have any questions or require further assistance, please don't hesitate to reach out to us.\n\n" +
                        "Thank you for your understanding.\n\nBest regards,\nBookMySeat Team",
                purchase.getOwner().getName(), purchaseType);

        System.out.println(ANSI_BLUE + "Sending cancellation email to " + purchase.getOwner().getEmail() + ANSI_RESET);
        // Send the email to the customer
        EmailSender.sendEmail(purchase.getOwner().getEmail(), "Confirmation of Your Canceled Purchase from BookMySeat", confirmation);

        // Optionally, email the company
        EmailSender.sendEmail("bookmyseatofficial@gmail.com", "A purchase has been canceled.", confirmation);
    }
}
