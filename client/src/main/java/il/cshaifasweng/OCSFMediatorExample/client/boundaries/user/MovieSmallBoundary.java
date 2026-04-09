package il.cshaifasweng.OCSFMediatorExample.client.boundaries.user;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieInstanceController;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieInstanceMessage;
import il.cshaifasweng.OCSFMediatorExample.server.events.MovieInstanceCanceledEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import static il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images.getImage;

public class MovieSmallBoundary {

    private HomeBoundary homeController;
    public Movie movie;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnBook;

    @FXML
    private Button btnCloseHV;

    @FXML
    private Button btnCloseTheater;

    @FXML
    private Button btnInfo;

    @FXML
    private Button btnPayHV;

    @FXML
    private Button btnPayTheater;

    @FXML
    private ComboBox<String> cmbCinema;

    @FXML
    private ComboBox<String> cmbDate;

    @FXML
    private DatePicker cmbDateHv;

    @FXML
    private ComboBox<String> cmbHourHv;

    @FXML
    private ComboBox<String> cmbHour;

    @FXML
    private ComboBox<String> cmbMovie;

    @FXML
    private Pane contentPanel;

    @FXML
    private ImageView image;

    @FXML
    private Pane imagePanel;

    @FXML
    private Label info;

    @FXML
    private Pane selectHvPanel;

    @FXML
    private Pane selectTheaterPane;

    @FXML
    private Text title;

    @FXML
    private Text txtMovieHV;

    @FXML
    private Text txtMovieTheater;

    @FXML
    private StackPane stkpanel;

    private String theaterName;
    private LocalDate date;
    private LocalTime time;
    private static MovieSmallBoundary registeredInstance = null; // Static reference to keep track of registered instance

    @FXML
    public void initialize() {
        if (imagePanel != null) {
            imagePanel.setOnMouseEntered(event -> handleMouseEnter());
            imagePanel.setOnMouseExited(event -> handleMouseExit());
        }

        if (contentPanel != null) {
            contentPanel.setOnMouseEntered(event -> handleMouseEnter());
            contentPanel.setOnMouseExited(event -> handleMouseExit());
        }

        if (btnPayTheater != null) {
            btnPayTheater.setOnAction(event -> handlePayButton());
            btnPayTheater.setDisable(true); // Disable by default until selections are made
        }
        if (btnPayHV != null) {
            btnPayHV.setOnAction(event -> handlePayButton());
            btnPayHV.setDisable(true); // Disable by default until selections are made
        }

        if (btnCloseTheater != null) {
            btnCloseTheater.setOnAction(event -> handleCloseButton());
        }
        if (btnCloseHV != null) {
            btnCloseHV.setOnAction(event -> handleCloseButton());
        }
        if (cmbCinema != null && cmbDate != null && cmbHour != null) {
            setupComboBoxes();
        } else {
            System.err.println("One or more ComboBox elements are not initialized.");
        }
    }

    public Movie getMovie() {
        return movie;
    }

    public void setHomeController(HomeBoundary homeController) {
        this.homeController = homeController;
    }

    private void setupComboBoxes() {
        disableAndResetComboBox(cmbDate);
        disableAndResetComboBox(cmbHour);

        cmbCinema.setCellFactory(comboBox -> new ListCell<String>() {
            @Override
            protected void updateItem(String location, boolean empty) {
                super.updateItem(location, empty);
                setText(empty || location == null ? null : location);
            }
        });

        cmbCinema.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String location, boolean empty) {
                super.updateItem(location, empty);
                setText(empty || location == null ? null : location);
            }
        });

        cmbCinema.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null) {
                disableAndResetComboBox(cmbDate);
                disableAndResetComboBox(cmbHour);
            } else {
                enableComboBox(cmbDate);
                MovieInstanceController.requestMovieInstancesByMovieIdAndTheaterName(movie.getId(), newValue);
                theaterName=newValue;
            }
        });

        cmbDate.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null) {
                disableAndResetComboBox(cmbHour);
            } else {
                enableComboBox(cmbHour);
                date = LocalDate.from(LocalDate.parse(newValue, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay());
                MovieInstanceController.requestMovieInstancesByMovieIdTheaterNameDate(movie.getId(), theaterName,LocalDate.parse(newValue, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay());
            }
        });


        cmbHour.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (btnPayTheater != null) {
                btnPayTheater.setDisable(newValue == null);
            }
            if (btnPayHV != null) {
                btnPayHV.setDisable(newValue == null);
            }
            if(newValue!=null)
             time = LocalTime.from(LocalTime.parse(newValue, DateTimeFormatter.ofPattern("HH:mm")));
        });
    }

    private void enableComboBox(ComboBox<String> comboBox) {
        comboBox.setDisable(false);
        comboBox.setStyle("-fx-background-color: #cae8fb;");
    }

    private void disableAndResetComboBox(ComboBox<String> comboBox) {
        comboBox.setDisable(true);
        comboBox.getItems().clear();
        comboBox.setStyle("");
    }

    private void handleMouseEnter() {
        if (!contentPanel.isVisible()) {
            contentPanel.setVisible(true);
            if (HomeBoundary.currentScreeningFilter.equals("View Upcoming Movies")){
                btnBook.setVisible(false);
            }
        }
    }

    private void handleMouseExit() {
        if (contentPanel.isVisible()) {
            if (HomeBoundary.currentScreeningFilter.equals("View Upcoming Movies")){
                btnBook.setVisible(true);
            }
            contentPanel.setVisible(false);
        }
    }

    public void setMovieShort(Movie movie) {
        this.movie = movie;
        info.setText(movie.getInfo());
        title.setText(movie.getEnglishName());
        txtMovieTheater.setText(movie.getEnglishName());
        image.setImage(getImage(movie));

    }

    @Subscribe
    public void onMovieInstanceMessageReceived(MovieInstanceMessage message) {
        Platform.runLater(() -> {
            System.out.println(message.requestType);
            switch (message.requestType) {
                case GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID:
                    if(HomeBoundary.currentScreeningFilter.equals("Theater") && message.movies.isEmpty()) {
                        AlertsBuilder.create(AlertType.ERROR, stkpanel, selectTheaterPane, selectTheaterPane, "No active screenings found for this movie. Please try another one.");
                        return;
                    }
                    for(MovieInstance movieInstance : message.movies)
                        System.out.println(movieInstance.getId());
                    populateCinemasComboBox(message.movies);
                    break;
                case GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID_AND_THEATER_NAME:
                    populateDatesComboBox(message.movies);
                    break;
                case GET_ALL_MOVIE_INSTANCES_BY_MOVIE_ID_THEATER_ID_DATE:
                    populateHoursComboBox(message.movies);
                    break;
                case GET_MOVIE_INSTANCE_AFTER_SELECTION:
                    if (message.movies != null && !message.movies.isEmpty()) {
                        loadSeatSelectionPage(message.movies.get(0));
                    } else {
                        AlertsBuilder.create(AlertType.ERROR, stkpanel, selectTheaterPane, selectTheaterPane, "Could not find the selected screening. Please try again.");
                    }
                    break;
                default:
                    break;
            }
        });
    }

    @Subscribe
    public void onMovieInstanceEvent(MovieInstanceCanceledEvent movieInstanceCanceledEvent)
    {
        Platform.runLater(() -> {
            if(movie.getId()==movieInstanceCanceledEvent.movieInstance.getMovie().getId()) {
                MovieInstanceController.requestMovieInstancesByMovieId(movie.getId());
            }});
    }

    private void populateCinemasComboBox(List<MovieInstance> movieInstances) {
        cmbCinema.setDisable(false);
        Set<String> cinemas = movieInstances.stream()
                .filter(MovieInstance::getIsActive) // Filter by isActive attribute
                .map(instance -> instance.getHall().getTheater().getLocation())
                .collect(Collectors.toSet());

        cmbCinema.getItems().setAll(cinemas);
        disableAndResetComboBox(cmbDate);
        disableAndResetComboBox(cmbHour);
    }

    private void populateDatesComboBox(List<MovieInstance> movieInstances) {
        Set<String> dates = movieInstances.stream()
                .filter(MovieInstance::getIsActive) // Filter by isActive attribute
                .map(instance -> instance.getTime().minusHours(3).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .collect(Collectors.toSet());

        cmbDate.getItems().setAll(dates);
        disableAndResetComboBox(cmbHour);
    }

    private void populateHoursComboBox(List<MovieInstance> movieInstances) {
        Set<String> hours = movieInstances.stream()
                .filter(MovieInstance::getIsActive) // Filter by isActive attribute
                .map(instance -> instance.getTime().toLocalTime().minusHours(3).format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toSet());

            cmbHour.getItems().setAll(hours);
    }

    private void handlePayButton() {
        System.out.println("handlePayButton triggered for movie: " + (movie != null ? movie.getEnglishName() : "unknown"));
        
        if (HomeBoundary.currentScreeningFilter.equals("Theater")) {
            String selectedCinema = cmbCinema.getValue();
            String selectedDateStr = cmbDate.getValue();
            String selectedHourStr = cmbHour.getValue();
            
            System.out.println("Theater Selection - Cinema: " + selectedCinema + ", Date: " + selectedDateStr + ", Hour: " + selectedHourStr);

            if (selectedCinema == null || selectedDateStr == null || selectedHourStr == null || date == null || time == null) {
                AlertsBuilder.create(AlertType.ERROR, stkpanel, selectTheaterPane, selectTheaterPane, "You must fill all boxes!");
            } else {
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                System.out.println("Requesting movie instance for: " + movie.getId() + " at " + selectedCinema + " time: " + dateTime);
                MovieInstanceController.requestMovieInstanceAfterSelection(movie.getId(), selectedCinema, dateTime);
            }
        } else if (HomeBoundary.currentScreeningFilter.equals("Home Viewing")) {
            String selectedDateStr = "";
            String selectedHourStr = "";
            
            if (cmbDateHv.getValue() != null) {
                selectedDateStr = cmbDateHv.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                date = LocalDate.from(LocalDate.parse(selectedDateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay());
            }
            if (cmbHourHv.getValue() != null) {
                selectedHourStr = cmbHourHv.getSelectionModel().getSelectedItem();
                time = LocalTime.from(LocalTime.parse(selectedHourStr, DateTimeFormatter.ofPattern("HH:mm")));
            }
            
            System.out.println("Home Viewing Selection - Date: " + selectedDateStr + ", Hour: " + selectedHourStr);

            if (selectedDateStr.isEmpty() || selectedHourStr.isEmpty()) {
                AlertsBuilder.create(AlertType.ERROR, stkpanel, imagePanel, imagePanel, "You must fill all boxes!");
            } else if (date.isBefore(ChronoLocalDate.from(LocalDate.now()))) {
                AlertsBuilder.create(AlertType.ERROR, stkpanel, imagePanel, imagePanel, "Can't choose Day that passed");
                cmbDateHv.setValue(null);
                cmbHourHv.setValue(null);
            } else if (date.equals(ChronoLocalDate.from(LocalDate.now())) && time.isBefore(LocalTime.now())) {
                AlertsBuilder.create(AlertType.ERROR, stkpanel, imagePanel, imagePanel, "Can't choose Time that passed");
                cmbHourHv.setValue(null);
            } else {
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                System.out.println("Loading Home Viewing Purchase for: " + movie.getEnglishName() + " at " + dateTime);
                loadHomeViewingPurchasePage();
            }
        }
    }

    private void loadSeatSelectionPage(MovieInstance movieInstance)
    {
        EventBus.getDefault().unregister(this);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.THEATER_PURCHASE_VIEW));
            Parent root = loader.load();
            TheaterPurchaseBoundary purchaseController = loader.getController();
            purchaseController.setMovieInstance(movieInstance);
            homeController.setRoot(root);
            MainBoundary.setCurrentController(purchaseController); //set the last controller for cleanup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHomeViewingPurchasePage()
    {
        try {
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.HOME_VIEWING_PURCHASE_VIEW));
            Parent root = loader.load();
            HomeViewingPurchaseBoundary purchaseController = loader.getController();
            purchaseController.setCurrentMovie(movie);
            purchaseController.setCurrentDateTime(dateTime);
            homeController.setRoot(root);
            MainBoundary.setCurrentController(purchaseController);  //set the last controller for cleanup
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    private void onMovieInstanceReceived(MovieInstance movieInstance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.THEATER_PURCHASE_VIEW));
            Parent root = loader.load();
            TheaterPurchaseBoundary purchaseController = loader.getController();
            purchaseController.setMovieInstance(movieInstance);
            homeController.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void setHVDate(ActionEvent event) {
        cmbHourHv.setDisable(false);
        for (int i = 12; i <= 24; i++) {
            cmbHourHv.getItems().add(LocalTime.of(i % 24, 0).format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    private void handleCloseButton() {
        EventBus.getDefault().unregister(this);
        imagePanel.setVisible(true);
        if (HomeBoundary.currentScreeningFilter.equals("Theater")){
            selectTheaterPane.setVisible(false);
        }
        else if (HomeBoundary.currentScreeningFilter.equals("Home Viewing")){
            selectHvPanel.setVisible(false);
        }
    }

    public void goToSelect(ActionEvent actionEvent) {
        // Unregister the previous instance if there is one
        if (registeredInstance != null && registeredInstance != this) {
            registeredInstance.cleanup();
        }
        // Register the current instance and update the static reference
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
            registeredInstance = this; // Set the current instance as the registered instance
        }
        MovieInstanceController.requestMovieInstancesByMovieId(movie.getId());
        if (HomeBoundary.currentScreeningFilter.equals("Theater")) {
            selectTheaterPane.setVisible(true);
        } else if (HomeBoundary.currentScreeningFilter.equals("Home Viewing")) {
            txtMovieHV.setText(movie.getEnglishName());
            selectHvPanel.setVisible(true);
        }
    }

    public void goToInfo(ActionEvent actionEvent) {
        homeController.showInfo(getMovie());
    }

    public void cleanup() {
        // Call the method that handles the close button action to return to the main screen
        handleClose();
        // Unregister the instance from EventBus and reset the static reference
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
            if (registeredInstance == this) {
                registeredInstance = null; // Reset the static reference if this is the registered instance
            }
        }
    }

    private void handleClose() {
        // Unregister from EventBus and reset the UI elements to show the main screen
        EventBus.getDefault().unregister(this);
        imagePanel.setVisible(true);
        if (HomeBoundary.currentScreeningFilter.equals("Theater")) {
            selectTheaterPane.setVisible(false);
        } else if (HomeBoundary.currentScreeningFilter.equals("Home Viewing")) {
            selectHvPanel.setVisible(false);
        }
    }
}