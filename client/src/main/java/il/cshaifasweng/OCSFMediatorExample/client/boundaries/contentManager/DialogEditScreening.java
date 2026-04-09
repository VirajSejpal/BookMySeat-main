package il.cshaifasweng.OCSFMediatorExample.client.boundaries.contentManager;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.HallController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieInstanceController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.TheaterController;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import il.cshaifasweng.OCSFMediatorExample.entities.Hall;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.HallMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.TheaterMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.Theater;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;

public class DialogEditScreening implements Initializable {

    private int screeningId = -1;
    private String currentMode;

    private Map<String, Integer> movieMap = new HashMap<>();

    @FXML
    private AnchorPane containerAddProduct;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<LocalTime> cmbHour;

    @FXML
    private ComboBox<Hall> cmbHall;

    @FXML
    private ComboBox<Theater> cmbTheater;

    @FXML
    private ComboBox<Movie> cmbMovies;

    @FXML
    private Text txtHall;

    @FXML
    private Text txtTheater;

    @FXML
    private Button btnSaveProduct;

    @FXML
    private Button btnCancelAddProduct;

    @FXML
    private Button btnClose;

    @FXML
    private Label txtAddProduct;

    private EditMovieScreeningsBoundary editMovieScreeningsBoundary;

    private MovieInstance movieInstance;

    private Theater theater;

    private Movie movie;

    private Hall hall;

    private LocalTime time;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getDefault().register(this);
        MovieController.getMoviesPresentedInTheaterContentManager();
        TheaterController.getAllTheaters();
        setupComboBoxes();
    }
    private void setupComboBoxes() {
        disableAndResetDatePicker(datePicker);
        disableAndResetComboBox(cmbHour);
        disableAndResetComboBox(cmbHall);

        cmbMovies.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null) {
                disableAndResetDatePicker(datePicker);
                disableAndResetComboBox(cmbHour);
                disableAndResetComboBox(cmbHall);
            } else {
                movie=newValue;

            }
        });

        cmbTheater.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null) {
                disableAndResetDatePicker(datePicker);
                disableAndResetComboBox(cmbHour);
            } else {
                cmbHall.setValue(null);
                datePicker.setValue(null);
                cmbHour.setValue(null);
                theater = newValue;
                HallController.requestHallsByTheaterID(theater.getId());

            }
        });


        cmbHall.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue == null) {
                disableAndResetDatePicker(datePicker);
                disableAndResetComboBox(cmbHour);
            } else {
                hall=newValue;
                datePicker.setDisable(false);
                datePicker.setValue(null);
                cmbHour.setValue(null);
            }
        });

        datePicker.valueProperty().addListener((observable, oldDate, newDate) -> {
            if (newDate == null) {
                disableAndResetComboBox(cmbHour);
            }
            else{
                datePicker.setValue(newDate);
                if(datePicker.getValue().isBefore(ChronoLocalDate.from(LocalDate.now()))) {
                    NotificationsBuilder.create(NotificationType.ERROR, "Can't choose a Date that passed", containerAddProduct);
                    datePicker.setValue(null);

                }
                else {
                    HallController.requestAvailableTimes(hall, datePicker.getValue());
                    cmbHour.setValue(null);
                }
            }
        });

        cmbHour.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if(newValue!=null) {
                if(datePicker.getValue().equals(ChronoLocalDate.from(LocalDate.now()))&&cmbHour.getValue().isBefore(LocalTime.now())) {
                    Platform.runLater(() -> {
                        NotificationsBuilder.create(NotificationType.ERROR, "Can't choose an Hour that passed", containerAddProduct);
                        cmbHour.setValue(null);
                    });
                }
                else time = cmbHour.getValue();
            }
        });
    }
    private void populateTheatersComboBox(List<Theater> theaters) {
        cmbTheater.getItems().setAll(theaters);

        // Custom cell factory to display the location in the ComboBox
        cmbTheater.setCellFactory(lv -> new ListCell<Theater>() {
            @Override
            protected void updateItem(Theater theater, boolean empty) {
                super.updateItem(theater, empty);
                setText(empty ? "" : theater.getLocation());
            }
        });

        // Custom button cell to display the selected item's location
        cmbTheater.setButtonCell(new ListCell<Theater>() {
            @Override
            protected void updateItem(Theater theater, boolean empty) {
                super.updateItem(theater, empty);
                setText(empty || theater == null ? "" : theater.getLocation());
            }
        });
    }

    private void populateMoviesComboBox(List<Movie> movies) {
        cmbMovies.getItems().setAll(movies);

        // Custom cell factory to display the EnglishName in the ComboBox
        cmbMovies.setCellFactory(lv -> new ListCell<Movie>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                setText(empty ? "" : movie.getEnglishName());
            }
        });

        // Custom button cell to display the selected item's EnglishName
        cmbMovies.setButtonCell(new ListCell<Movie>() {
            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                setText(empty || movie == null ? "" : movie.getEnglishName());
            }
        });
    }

    private void populateHallsComboBox(List<Hall> halls) {
        cmbHall.setDisable(false);
        cmbHall.getItems().setAll(halls);

        // Custom cell factory to display the Hall name in the ComboBox
        cmbHall.setCellFactory(lv -> new ListCell<Hall>() {
            @Override
            protected void updateItem(Hall hall, boolean empty) {
                super.updateItem(hall, empty);
                setText(empty ? "" : hall.getName());
            }
        });

        // Custom button cell to display the selected item's Hall name
        cmbHall.setButtonCell(new ListCell<Hall>() {
            @Override
            protected void updateItem(Hall hall, boolean empty) {
                super.updateItem(hall, empty);
                setText(empty || hall == null ? "" : hall.getName());
            }
        });
    }


    @Subscribe
    public void onHallMessageReceived(HallMessage message) {
        Platform.runLater(() -> {
            if (message.responseType == HallMessage.ResponseType.ALL_AVAILABLE_TIMES) {
                cmbHour.getItems().clear();
                List<LocalTime> availableDates = message.availableTimes;

                if (availableDates != null && !availableDates.isEmpty()) {
                    cmbHour.setDisable(false);
                    cmbHour.getItems().addAll(availableDates);
                }
            } else if (message.requestType == HallMessage.RequestType.GET_ALL_HALLS_BY_THEATER_ID) {
                populateHallsComboBox(message.halls);
            }
        });
    }

    @Subscribe
    public void onTheaterMessageReceived(TheaterMessage message) {
        Platform.runLater(() -> {
            if (message.requestType == TheaterMessage.RequestType.GET_ALL_THEATERS)
                populateTheatersComboBox(message.theaterList);
        });
    }

    @Subscribe
    public void onMoviesMessageReceived(MovieMessage message) {
        Platform.runLater(() -> {
            if (message.responseType == MovieMessage.ResponseType.RETURN_MOVIES)
                populateMoviesComboBox(message.movies);
        });
    }


    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }

    public void setEditScreeningListBoundary(EditMovieScreeningsBoundary editMovieScreeningsBoundary) {
        this.editMovieScreeningsBoundary = editMovieScreeningsBoundary;
    }

    public void setDialog(String operation, MovieInstance movieInstance) {
        this.currentMode = operation;
        if ("add".equals(operation))
            prepareForNewScreening();
        else if ("edit".equals(operation)) {
            this.movieInstance = movieInstance;
            populateFieldsForEdit();
        }
    }

    private void prepareForNewScreening() {
        cleanControls();
        txtAddProduct.setText("Add Screening");
        btnSaveProduct.setVisible(true);
    }

    private void populateFieldsForEdit() {
        txtAddProduct.setText("Update Screening");
        cmbTheater.setDisable(false);
        cmbHall.setDisable(false);
        Platform.runLater(() -> {
            cmbTheater.getSelectionModel().select(movieInstance.getHall().getTheater());
            cmbHall.getSelectionModel().select(movieInstance.getHall());
            cmbMovies.getSelectionModel().select(movieInstance.getMovie());
        });
    }


    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) return;
        if(currentMode.equals("add"))
        {
            MovieInstanceController.addMovieInstance(cmbMovies.getValue(), LocalDateTime.of(datePicker.getValue(), cmbHour.getValue().plusHours(3)),cmbHall.getValue());
        }
        else if(currentMode.equals("edit")) {
            movieInstance.setHall(cmbHall.getValue());
            movieInstance.setMovie(cmbMovies.getValue());
            movieInstance.setTime( LocalDateTime.of(datePicker.getValue(), cmbHour.getValue().plusHours(3)));
            MovieInstanceController.updateMovieInstance(movieInstance);
        }
        cleanControls();
        closeDialog();

    }
    private void disableAndResetComboBox(ComboBox<?> comboBox) {
        comboBox.setDisable(true);
        comboBox.getItems().clear();
        comboBox.setStyle("");
    }

    private void disableAndResetDatePicker(DatePicker datePicker) {
        datePicker.setDisable(true);
        datePicker.setValue(null);
        datePicker.getEditor().clear();
        datePicker.setStyle("");
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeDialog();
    }

    private void closeDialog() {
        cleanup();
        editMovieScreeningsBoundary.closeDialog();
    }

    private boolean validateInputs()
    {
        boolean valid = true;
        if (datePicker.getValue() == null) {
            showErrorAndFocus(datePicker);
            valid = false;
        }
        if (cmbHour.getValue() == null ) {
            showErrorAndFocus(cmbHour);
            valid = false;
        }
        if (cmbTheater.getValue() == null ) {
            showErrorAndFocus(cmbTheater);
            valid = false;
        }
        if (cmbMovies.getValue() == null ) {
            showErrorAndFocus(cmbMovies);
            valid = false;
        }

        return valid;
    }


    private void showErrorAndFocus(Control field) {
        field.requestFocus();
        Animations.shake(field);
    }

    private void cleanControls() {
        datePicker.setValue(null);
        cmbHour.setValue(null);
    }
}
