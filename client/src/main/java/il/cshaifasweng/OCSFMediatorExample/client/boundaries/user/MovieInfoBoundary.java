package il.cshaifasweng.OCSFMediatorExample.client.boundaries.user;

import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.client.util.ButtonFactory;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.bytebuddy.implementation.ToStringMethod;

import java.net.URL;
import java.util.ResourceBundle;

import static il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images.expandImage;
import static il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images.getImage;

public class MovieInfoBoundary implements Initializable {

    @FXML
    private Button btnGenre;
    @FXML
    private Pane paneContainer;
    @FXML
    private Button Close;
    @FXML
    private Label lblActors;
    @FXML
    private Label lblName;
    @FXML
    private Label lblInfo;
    @FXML
    private Label lblProducer;
    @FXML
    private HBox imageContainer;
    @FXML
    private ImageView image;

    @FXML
    private Label homePrice;
    @FXML
    private Label theaterPrice;

    @FXML
    private Label lblDuration;

    private HomeBoundary homeController;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
     }

    public void setHomeController(HomeBoundary homeController) {
        this.homeController = homeController;
    }

    public void setInfo(Movie movie) {
        lblInfo.setText(movie.getInfo());
        lblActors.setText(movie.getMainActors().toString().replace("[", "").replace("]", ""));
        lblName.setText(movie.getEnglishName() + "  |  " + movie.getHebrewName());
        lblProducer.setText(movie.getProducer());
        lblDuration.setText(String.valueOf(movie.getDuration()) + " minutes.");
        homePrice.setText(String.valueOf((movie.getHomeViewingPrice()))+ " ₹");
        theaterPrice.setText(String.valueOf((movie.getTheaterPrice()))+ " ₹");
        image.setImage(getImage(movie));
        image.setPreserveRatio(true);
        image.setFitWidth(250);
        image.setFitHeight(350);
        expandImage(paneContainer, image, movie, movie.getEnglishName());

        // Update genre button with appropriate styling and action
        Button genreButton = ButtonFactory.createButtonGenre(movie);
        btnGenre.setText(genreButton.getText());
        btnGenre.setGraphic(genreButton.getGraphic());
        btnGenre.getStyleClass().addAll(genreButton.getStyleClass());
    }

    @FXML
    void handleClose(ActionEvent event) {
        homeController.closeDialogAddUser();
    }




}
