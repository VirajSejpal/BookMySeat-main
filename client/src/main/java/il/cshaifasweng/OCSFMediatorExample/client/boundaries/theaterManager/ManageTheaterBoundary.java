package il.cshaifasweng.OCSFMediatorExample.client.boundaries.theaterManager;

import il.cshaifasweng.OCSFMediatorExample.client.boundaries.user.MainBoundary;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.HallController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.SeatController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.TheaterController;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.HallMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.SeatMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.TheaterMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManageTheaterBoundary implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private ComboBox<Hall> comboHalls;

    @FXML
    private GridPane seatGrid;

    @FXML
    private Label lblTheaterName;

    private Theater theater;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getDefault().register(this);
        TheaterController.getTheaterNameByTheaterManagerID(MainBoundary.getId());
        
        comboHalls.setOnAction(event -> {
            Hall selectedHall = comboHalls.getSelectionModel().getSelectedItem();
            if (selectedHall != null) {
                HallController.requestHallByID(selectedHall.getId());
            }
        });
    }

    @Subscribe
    public void onTheaterMessage(TheaterMessage message) {
        if (message.responseType == TheaterMessage.ResponseType.RETURN_THEATER && !message.theaterList.isEmpty()) {
            this.theater = message.theaterList.get(0);
            Platform.runLater(() -> {
                lblTheaterName.setText("Managing: " + theater.getLocation());
                // Explicitly request halls for this theater to ensure they are loaded
                HallController.requestHallsByTheaterID(theater.getId());
            });
        }
    }

    @Subscribe
    public void onHallMessage(HallMessage hallMessage) {
        if (hallMessage.responseType == HallMessage.ResponseType.RETURN_HALLS_BY_ID) {
            Platform.runLater(() -> {
                comboHalls.getItems().setAll(hallMessage.halls);
            });
        } else if (hallMessage.responseType == HallMessage.ResponseType.REQUESTED_HALL) {
            if (!hallMessage.halls.isEmpty()) {
                Hall hall = hallMessage.halls.get(0);
                Platform.runLater(() -> updateSeatsGrid(hall));
            }
        }
    }

    private void updateSeatsGrid(Hall hall) {
        seatGrid.getChildren().clear();
        for (Seat seat : hall.getSeats()) {
            Button seatButton = new Button(seat.getRow() + ":" + seat.getCol());
            updateSeatButtonStyle(seat, seatButton);

            seatButton.setOnAction(event -> {
                List<Seat> seatsToToggle = new ArrayList<>();
                seatsToToggle.add(seat);
                SeatController.toggleSeatBrokenStatus(seatsToToggle);
            });
            seatGrid.add(seatButton, seat.getCol(), seat.getRow());
        }
    }

    private void updateSeatButtonStyle(Seat seat, Button seatButton) {
        if (seat.isBroken()) {
            seatButton.setStyle("-fx-background-color: #3b3b3b; -fx-text-fill: white;");
            seatButton.setText("X");
        } else {
            seatButton.setStyle("-fx-background-color: #4CAF50FF; -fx-text-fill: white;");
        }
    }

    @Subscribe
    public void onSeatMessage(SeatMessage message) {
        if (message.responseType == SeatMessage.ResponseType.SEAT_BROKEN_STATUS_CHANGED) {
            Platform.runLater(() -> {
                NotificationsBuilder.create(NotificationType.SUCCESS, "Seat status updated successfully!", stackPane);
                Hall selectedHall = comboHalls.getSelectionModel().getSelectedItem();
                if (selectedHall != null) {
                    HallController.requestHallByID(selectedHall.getId());
                }
            });
        }
    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }
}
