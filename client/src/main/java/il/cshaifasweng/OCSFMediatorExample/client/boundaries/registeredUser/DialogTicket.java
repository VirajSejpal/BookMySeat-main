package il.cshaifasweng.OCSFMediatorExample.client.boundaries.registeredUser;

import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.entities.HomeViewingPackageInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieTicket;
import il.cshaifasweng.OCSFMediatorExample.entities.MultiEntryTicket;
import il.cshaifasweng.OCSFMediatorExample.entities.Purchase;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DialogTicket {

    @FXML
    private HBox hboxPurchaseDay;

    @FXML
    private HBox hboxTime;

    @FXML
    private HBox hbxActiveDay;

    @FXML
    private HBox hbxTheater;

    @FXML
    private Label lblActiveDay;

    @FXML
    private Label lblActiveMonth;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblActiveYear;

    @FXML
    private Label lblCustomer;

    @FXML
    private Label lblHall;

    @FXML
    private Label lblHour;

    @FXML
    private Label lblId;

    @FXML
    private Hyperlink lblLink;

    @FXML
    private Label lblMin;

    @FXML
    private Label lblPurchaseDay,lblPurchaseMonth,lblPurchaseYear;

    @FXML
    private Label lblSeat;

    @FXML
    private Label lblTheater;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblExpiredDay, lblExpiredMonth, lblExpiredYear,lblHourExpired,lblMinExpired;
    @FXML
    private HBox hbxExpired;

    @FXML
    private Label lblActive;
    @FXML
    private VBox vbxHVP;

    private MovieTicket movieTicket;
    private MultiEntryTicket multiEntryTicket;
    private HomeViewingPackageInstance homeViewingPackage;
    private OrdersBoundary ordersController;

    public void setOrdersController(OrdersBoundary ordersController) {
        this.ordersController = ordersController;
    }
    public void setTicketInfo(Purchase purchase) {
        if (purchase != null) {
            setPurchase(purchase);
            if (purchase instanceof MovieTicket) {
                movieTicket = (MovieTicket) purchase;
                setMovieTicket();
            } else if (purchase instanceof MultiEntryTicket) {
                multiEntryTicket = (MultiEntryTicket) purchase;
                setMultiEntryTicket();
            } else if (purchase instanceof HomeViewingPackageInstance) {
                homeViewingPackage = (HomeViewingPackageInstance) purchase;
                setHomeViewingPackage();
            }
        }
    }

    public void setPurchase(Purchase purchase) {
        LocalDate purchaseDate = purchase.getPurchaseDate().toLocalDate();
        if (purchaseDate != null) {
            lblPurchaseDay.setText(String.format("%02d", purchaseDate.getDayOfMonth()));
            lblPurchaseMonth.setText(purchaseDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            lblPurchaseYear.setText(String.valueOf(purchaseDate.getYear()));
            lblCustomer.setText(purchase.getOwner().getName());
            lblId.setText(String.valueOf(purchase.getId()));
        }
    }

    public void setMovieTicket() {
        lblHour.setVisible(true);
        lblMin.setVisible(true);
        lblTitle.setText(movieTicket.getMovieInstance().getMovie().getEnglishName()+" | "+movieTicket.getMovieInstance().getMovie().getHebrewName());
        LocalDateTime activeDate = movieTicket.getMovieInstance().getTime().minusHours(3);
        lblActiveDay.setText(String.format("%02d",activeDate.getDayOfMonth()));
        lblActiveMonth.setText(activeDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        lblActiveYear.setText(String.valueOf(activeDate.getYear()));
        lblHour.setText(String.valueOf(activeDate.getHour()));
        lblMin.setText(String.format("%02d", activeDate.getMinute()));
        lblPrice.setText(String.valueOf(movieTicket.getPricePaid()+"₹"));
        lblTheater.setText(movieTicket.getSeat().getHall().getTheater().getLocation());
        String seat = "row: " + movieTicket.getSeat().getRow() + ", seat: " + movieTicket.getSeat().getCol();
        lblSeat.setText(seat);
        hboxTime.setVisible(true);
        hbxTheater.setVisible(true);
        vbxHVP.setVisible(false);
        hboxPurchaseDay.setVisible(true);
        hbxActiveDay.setVisible(true);
        lblActive.setVisible(true);
        hbxExpired.setVisible(false);

    }

    public void setMultiEntryTicket() {
        lblTitle.setText("Multi Entry Card");
        lblPrice.setText("2800₹");
        hbxTheater.setVisible(false);
        vbxHVP.setVisible(false);
        hboxPurchaseDay.setVisible(true);
        hbxActiveDay.setVisible(false);
        hboxTime.setVisible(false);
        lblActive.setVisible(false);
        hbxExpired.setVisible(false);
    }

    public void setHomeViewingPackage() {
        lblTitle.setText(homeViewingPackage.getMovie().getEnglishName()+" | " + homeViewingPackage.getMovie().getHebrewName());
        if (homeViewingPackage.isLinkActive()) {
            lblLink.setDisable(false);
            lblLink.setText(homeViewingPackage.getLink());
        } else {
            lblLink.setDisable(true);
            lblLink.setText("Unavailable");
        }
        lblLink.setOnAction(event -> {
            closeDialogAddUser();
            AlertsBuilder.create(
                    AlertType.INFO,
                    ordersController.getStckUsers(),
                    lblLink.getScene().getRoot(),
                    lblLink,
                    "Enjoy your cinematic experience!",
                    "START",
                    null,
                    null,
                    null
            );
        });

        lblPrice.setText(String.valueOf(homeViewingPackage.getPricePaid()+"₹"));
        hbxActiveDay.setVisible(true);
        hboxTime.setVisible(true);
        LocalDateTime activeDate = homeViewingPackage.getViewingDate().minusHours(3);
        lblActiveDay.setText(String.valueOf(activeDate.getDayOfMonth()));
        lblActiveMonth.setText(activeDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        lblActiveYear.setText(String.valueOf(activeDate.getYear()));
        lblHour.setText(String.valueOf(activeDate.getHour()));
        lblMin.setText(String.format("%02d", activeDate.getMinute()));
        LocalDateTime expiredDate = homeViewingPackage.getViewingDate().plusWeeks(1).minusHours(3);
        hbxExpired.setVisible(true);
        lblExpiredDay.setText(String.valueOf(expiredDate.getDayOfMonth()));
        lblExpiredMonth.setText(expiredDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        lblExpiredYear.setText(String.valueOf(expiredDate.getYear()));
        lblHourExpired.setText(String.valueOf(expiredDate.getHour()));
        lblMinExpired.setText(String.format("%02d", expiredDate.getMinute()));
        hbxTheater.setVisible(false);
        vbxHVP.setVisible(true);
        hboxPurchaseDay.setVisible(true);
        lblActive.setVisible(true);
    }

    @FXML
    private void closeDialogAddUser() {
        if (ordersController != null) {
            ordersController.closeDialog();
        }
    }
}
