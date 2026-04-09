package il.cshaifasweng.OCSFMediatorExample.client.util.assets;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.scene.image.Image;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Images {

    public static void expandImage(Pane paneContainer, ImageView imageProduct, Movie movie, String title) {
        ColorAdjust colorAdjust = new ColorAdjust();
        paneContainer.hoverProperty().addListener((o, oldV, newV) -> {
            colorAdjust.setBrightness(newV ? 0.25 : 0);
            imageProduct.setEffect(colorAdjust);
        });

        paneContainer.setOnMouseClicked(ev -> {
            final Image image = getImage(movie);
            final ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setFitWidth(550);

            final BorderPane borderPane = new BorderPane(imageView);
            borderPane.setStyle("-fx-background-color: white");
            borderPane.setCenter(imageView);

            final ScrollPane root = new ScrollPane(borderPane);
            root.setStyle("-fx-background-color: white");
            root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            root.getStylesheets().add(ConstantsPath.LIGHT_THEME);
            root.getStyleClass().add("scroll-bar");

            root.setFitToHeight(true);
            root.setFitToWidth(true);

            Stage stage = new Stage();
            stage.getIcons().add(new Image(ConstantsPath.STAGE_ICON));
            stage.setScene(new Scene(root, 550, 550));
            stage.setTitle(title);
            stage.show();
        });
    }

    public static void initializeImageHoverEffect(VBox imageContainer, ImageView imageProduct, double fitHeight, double fitWidth) {
        ColorAdjust colorAdjust = new ColorAdjust();
        imageContainer.hoverProperty().addListener((o, oldV, newV) -> {
            colorAdjust.setBrightness(newV ? 0.25 : 0);
            imageProduct.setEffect(colorAdjust);
        });
        imageContainer.setPadding(new Insets(5));
        imageProduct.setFitHeight(fitHeight);
        imageProduct.setFitWidth(fitWidth);
    }

    public static Image getImage(Movie movie) {
        byte[] imageBytes = movie.getImageBytes();

        if (imageBytes != null && imageBytes.length > 0) {
            try {
                return new Image(new ByteArrayInputStream(imageBytes));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Image(ConstantsPath.NO_IMAGE_AVAILABLE, true);
    }


    public static File uploadMovieImage(Stage stage, ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Movie Image");

        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter(
                "Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp"
        );
        fileChooser.getExtensionFilters().add(imageFilter);

        // Show the open dialog
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }

        return file;
    }

    public static void setImageToMovie(File imageFile, Movie movie) throws IOException {
        byte[] imageBytes;
        try (FileInputStream fis = new FileInputStream(imageFile)) {
            imageBytes = fis.readAllBytes();
        }

        movie.setImage(imageFile.getName());
        movie.setImageBytes(imageBytes);

     }



}
