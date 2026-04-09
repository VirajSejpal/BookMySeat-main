package il.cshaifasweng.OCSFMediatorExample.client.boundaries.user;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieInstanceController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.TheaterController;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.DialogTool;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieInstanceMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.TheaterMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.Theater;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.server.events.Event;
import il.cshaifasweng.OCSFMediatorExample.server.events.HomeViewingEvent;
import il.cshaifasweng.OCSFMediatorExample.server.events.MovieEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.scene.control.ComboBox;


import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class HomeBoundary implements Initializable {

    private static final int ITEMS_PER_ROW = 4;
    @FXML
    private HBox TheaterFilters;

    private List<Movie> items;
    private DialogTool dialogInfo;

    @FXML
    private AnchorPane InfoContainer;


    @FXML
    private Button TheaterButton;

    @FXML
    private StackPane stckHome;

    @FXML
    private ComboBox<String> cmbGenre;

    @FXML
    private GridPane grid;

    static String currentScreeningFilter="Theater";        // sets in what type of screening the user wants to see
    private String Genre="all";



    @FXML
    private ComboBox<String> cmbTheater;

    @FXML
    private DatePicker beforeDate;

    @FXML
    private DatePicker afterDate;
    private Button lastSelectedScreeningButton;
    private Button lastSelectedGenreButton;
    private Movie movieInfo;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Register this controller to listen for MovieMessage events
        EventBus.getDefault().register(this);
        FilterByScreeningType(null);
        SetTheaterCombo();
        setListeners();
        TheaterController.getAllTheaters();
        animateNodes();
    }

    private void animateNodes() {
        Animations.fadeInUp(stckHome);
        Animations.fadeInUp(grid);
    }

    private void toggleButtonState(Button clickedButton, Button lastSelectedButton) {
        if (lastSelectedButton != null && lastSelectedButton != clickedButton) {
            lastSelectedButton.getStyleClass().remove("selected");
        }
        if (lastSelectedButton != clickedButton) {
            clickedButton.getStyleClass().add("selected");
        }
    }

    @Subscribe
    public void onMovieMessageReceived(MovieMessage message) {
        Platform.runLater(() ->
        {
            try {
                System.out.println("Movie list recieved");
                setItems(message.movies);
                if(cmbGenre.getItems().isEmpty()) {
                    System.out.println("Filling Genres");
                    Set<String> genres = message.movies.stream()
                            .map(Movie::getGenre)
                            .collect(Collectors.toSet());
                    // Set genres in ComboBox
                    cmbGenre.getItems().setAll(new ArrayList<>(genres));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Subscribe
    public void onMovieMessageReceived(TheaterMessage message) {
        Platform.runLater(() ->
        {
            populateTheatersComboBox(message.theaterList);
        });
    }

    @Subscribe
    public void onMovieMessageReceived(MovieInstanceMessage message) {
        if (message.requestType == MovieInstanceMessage.RequestType.GET_ALL_MOVIE_INSTANCES_BY_THEATER_NAME ||
                message.requestType == MovieInstanceMessage.RequestType.GET_MOVIE_INSTANCES_BETWEEN_DATES)
        {
            Platform.runLater(() -> {
                try {
                    GetAndSetMoviesFromInstances(message.movies);
                } catch (IOException e) {
                    e.printStackTrace(); // Log the exception
                }
            });
        }
    }


    @Subscribe
    public void onEventReceived(Event event) {
        Platform.runLater(() -> {
            if (event instanceof MovieEvent && currentScreeningFilter.equals("Theater")
                    && ((MovieEvent) event).movie.getAvailability() == Movie.Availability.AVAILABLE) {

              //  AlertsBuilder.create(AlertType.INFO, stckHome, stckHome, stckHome, "Screening list has been updated!");
                cmbGenre.setValue(null);
                cmbTheater.setValue(null);
                afterDate.setValue(null);
                beforeDate.setValue(null);
                Genre = "all";
                MovieController.getMoviesPresentedInTheater();
                if(!InfoContainer.getChildren().isEmpty()&& movieInfo.getId()==((MovieEvent) event).movie.getId()) {
                    AlertsBuilder.create(AlertType.INFO, stckHome, stckHome, stckHome, "Movie Info Had Been Updated");
                    dialogInfo.close();
                    showInfo(((MovieEvent) event).movie);
                }

            } else if (event instanceof HomeViewingEvent && currentScreeningFilter.equals("Home Viewing")) {

               // AlertsBuilder.create(AlertType.INFO, stckHome, stckHome, stckHome, "Home Viewing package list has been updated!");
                cmbGenre.setValue(null);
                Genre = "all";
                MovieController.getMoviesPresentedInHomeViewing();
                if(!InfoContainer.getChildren().isEmpty()&& movieInfo.getId()==((HomeViewingEvent) event).movie.getId()) {
                   AlertsBuilder.create(AlertType.INFO, stckHome, stckHome, stckHome, "Movie Info Had Been Updated");
                    dialogInfo.close();
                    showInfo(((HomeViewingEvent) event).movie);
                }


            } else if (currentScreeningFilter.equals("View Upcoming Movies")) {
                MovieController.getUpcomingMovies();
            }
        });
    }


    public void setListeners()
    {
        beforeDate.valueProperty().addListener((observable, oldDate, newDate) -> {
            if (newDate != null) {
                beforeDate.setValue(newDate);
                cmbTheater.setValue(null);
                cmbGenre.setValue(null);
                if(beforeDate.getValue()!=null&&beforeDate.getValue().isBefore(ChronoLocalDate.from(LocalDate.now())))
                {
                    AlertsBuilder.create(AlertType.ERROR, stckHome, stckHome, stckHome, "Can't choose a Date that passed");
                    beforeDate.setValue(null);
                }
                else if(afterDate.getValue()!=null&&beforeDate.getValue().isBefore(afterDate.getValue()))
                {
                    AlertsBuilder.create(AlertType.ERROR, stckHome, stckHome, stckHome, "Start date can't be bigger than end date");
                    afterDate.setValue(null);
                }
                else
                    MovieInstanceController.requestMovieInstancesBetweenDates(beforeDate.getValue(),afterDate.getValue());
            }
        });

        afterDate.valueProperty().addListener((observable, oldDate, newDate) -> {
            if (newDate != null) {
                cmbTheater.setValue(null);
                cmbGenre.setValue(null);
                afterDate.setValue(newDate);

                if(afterDate.getValue()!=null&&afterDate.getValue().isBefore(ChronoLocalDate.from(LocalDate.now())))
                {
                    AlertsBuilder.create(AlertType.ERROR, stckHome, stckHome, stckHome, "Can't choose a Date that passed");
                    afterDate.setValue(null);
                }
                else if(beforeDate.getValue()!=null&&beforeDate.getValue().isBefore(afterDate.getValue())) {
                    AlertsBuilder.create(AlertType.ERROR, stckHome, stckHome, stckHome, "Start date can't be bigger than end date");
                    afterDate.setValue(null);
                }
                else {
                    System.out.println("Sending Request, before date " + beforeDate.getValue() + " after date " +afterDate.getValue());
                    MovieInstanceController.requestMovieInstancesBetweenDates(beforeDate.getValue(), afterDate.getValue());
                }

            }
        });

        cmbGenre.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                // Do something with the selected genre
                System.out.println("Selected genre: " + newValue);
                cmbTheater.setValue(null);
                beforeDate.setValue(null);
                afterDate.setValue(null);
                Genre = newValue;
                Genre = Genre.toLowerCase();
                FilterByScreeningTypeAndGenre(null);
            }
        });

        cmbGenre.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String location, boolean empty) {
                super.updateItem(location, empty);
                setText(empty || location == null ? "Choose Genre" : location);
            }
        });

    }

    public void setItems(List<Movie> movies) throws IOException {
        this.items = movies;
        Platform.runLater(() -> {
            try {
                updateGrid(); // Update the grid with the movie items
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void updateGrid() throws IOException {
        grid.getChildren().clear();

        // Clear existing row constraints
        grid.getRowConstraints().clear();

        int row = 0;
        int col = 0;
        for (Movie item : items) {
            Node normalItem = createItem(item);
            grid.add(normalItem, col, row);
            col++;
            if (col == ITEMS_PER_ROW) {
                col = 0;
                row++;

                // Add a new row constraint
                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setMinHeight(316.0);
                rowConstraints.setMaxHeight(316.0);
                rowConstraints.setValignment(VPos.CENTER);
                grid.getRowConstraints().add(rowConstraints);
            }
        }

        // If the last row is not full, add the final row constraint
        if (col != 0) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setMinHeight(316.0);
            rowConstraints.setMaxHeight(316.0);
            rowConstraints.setValignment(VPos.CENTER);
            grid.getRowConstraints().add(rowConstraints);
        }
    }

    private Node createItem(Movie item) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.MOVIE_SMALL_VIEW));
        StackPane itemBox = loader.load();
        MovieSmallBoundary controller = loader.getController();
        if (controller != null) {
            controller.setMovieShort(item);
            controller.setHomeController(this);
        } else {
            System.err.println("Controller is null");
        }
        return itemBox;
    }

    public void setRoot(Parent root) {
        if (root != null) {
            stckHome.getChildren().setAll(root);
        } else {
            System.err.println("Root is null, cannot set.");
        }
    }

    public void showInfo(Movie movie) {
        try {
            movieInfo = movie;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.MOVIE_INFO_VIEW));
            Parent pane = loader.load();

            MovieInfoBoundary movieInfoController = loader.getController();
            movieInfoController.setHomeController(this);
            movieInfoController.setInfo(movie);

            InfoContainer.getChildren().clear();
            InfoContainer.getChildren().add(pane);
            InfoContainer.setVisible(true);

            dialogInfo = new DialogTool(InfoContainer, stckHome);
            dialogInfo.show();

            dialogInfo.setOnDialogClosed(ev -> {
                stckHome.setEffect(null);
                InfoContainer.setVisible(false);
                InfoContainer.getChildren().clear();
            });

            stckHome.setEffect(ConstantsPath.BOX_BLUR_EFFECT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void closeDialogAddUser() {
        if (dialogInfo != null) {
            dialogInfo.close();
        }
    }

    @FXML
    void FilterByScreeningType(ActionEvent event)           //THEATER / HOME VIEWING
    {
        if(event==null) {
            currentScreeningFilter= "Theater";
            toggleButtonState(TheaterButton, lastSelectedScreeningButton);
            lastSelectedScreeningButton = TheaterButton;
        }
        else {
            Button clickedButton = (Button) event.getSource();
            toggleButtonState(clickedButton, lastSelectedScreeningButton);
            if(lastSelectedScreeningButton.equals(clickedButton))
                return;
            lastSelectedScreeningButton = clickedButton;
            currentScreeningFilter = clickedButton.getText();
        }
        System.out.println("currentScreeningFilter = " + currentScreeningFilter);
        if (currentScreeningFilter.equals("Theater")) {
            TheaterFilters.setDisable(false);
            TheaterFilters.setVisible(true);
            cmbTheater.setDisable(false);
            cmbTheater.setVisible(true);
            afterDate.setDisable(false);
            afterDate.setVisible(true);
            beforeDate.setDisable(false);
            beforeDate.setVisible(true);
            cmbGenre.setValue(null);
            afterDate.setValue(null);
            beforeDate.setValue(null);
            cmbTheater.setValue(null);
            Genre="all";
        }
        else if (currentScreeningFilter.equals("Home Viewing"))
        {
            TheaterFilters.setDisable(false);
            TheaterFilters.setVisible(true);
            cmbTheater.setDisable(true);
            cmbTheater.setVisible(false);
            afterDate.setDisable(true);
            afterDate.setVisible(false);
            beforeDate.setDisable(true);
            beforeDate.setVisible(false);
            cmbGenre.setValue(null);
            Genre="all";
        }
        else
        {
            TheaterFilters.setDisable(true);
            TheaterFilters.setVisible(false);
        }
        if(currentScreeningFilter.equals("View Upcoming Movies"))
            MovieController.getUpcomingMovies();
        else
            FilterByScreeningTypeAndGenre(event);
    }


    @FXML
    void FilterByScreeningTypeAndGenre(ActionEvent event) {
        MovieController.getMoviesFilteredByScreeningTypeAndGenre(currentScreeningFilter,Genre);

    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }

    @FXML
    void SetTheaterCombo() {

        cmbTheater.setCellFactory(comboBox -> new ListCell<String>() {
            @Override
            protected void updateItem(String location, boolean empty) {
                super.updateItem(location, empty);
                setText(empty || location == null ? null : location);
            }
        });

        cmbTheater.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String location, boolean empty) {
                super.updateItem(location, empty);
                setText(empty || location == null ? "Choose Theater" : location);
            }
        });
        cmbTheater.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            if (newValue != null) {
                MovieInstanceController.requestMovieInstancesByTheaterName(newValue);
                beforeDate.setValue(null);
                afterDate.setValue(null);
                cmbGenre.setValue(null);
            }
        });
    }

    @FXML
    void Reset(ActionEvent event) {
        if(currentScreeningFilter.equals("Theater"))
            MovieController.getMoviesPresentedInTheater();
        else
            MovieController.getMoviesPresentedInHomeViewing();
        cmbTheater.setValue(null);
        cmbGenre.setValue(null);
        afterDate.setValue(null);
        beforeDate.setValue(null);
    }

    private void populateTheatersComboBox(List<Theater> theatersList) {
        Set<String> theaterLocations = theatersList.stream()
                .map(Theater::getLocation)
                .collect(Collectors.toSet());
        List<String> locationsList = new ArrayList<>(theaterLocations);
        cmbTheater.getItems().setAll(locationsList);
    }
    private void GetAndSetMoviesFromInstances(List<MovieInstance> movieInstances) throws IOException {
        Set<Movie> uniqueMovies = movieInstances.stream()
                .map(MovieInstance::getMovie)
                .collect(Collectors.toSet());
        setItems(new ArrayList<>(uniqueMovies));
    }

    private void enableComboBox(ComboBox<String> comboBox) {
        comboBox.setDisable(false);
        comboBox.setStyle("-fx-background-color: #cae8fb;");
    }
}
