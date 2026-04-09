package il.cshaifasweng.OCSFMediatorExample.client.boundaries.contentManager;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.PriceRequestController;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images.expandImage;
import static il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images.getImage;

public class DialogEditMovie implements Initializable {

    private static final Stage stage = new Stage();
    private File imageFile;
    private Movie movie;
    private String currentMode;

    private final ColorAdjust colorAdjust = new ColorAdjust();
    private final long LIMIT = 1000000;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnSave;

    @FXML
    private ComboBox<String> comboGenre;


    @FXML
    private AnchorPane containerAddProduct;

    @FXML
    private VBox imageContainer;

    @FXML
    private ImageView imageProduct;

    @FXML
    private Pane paneContainer;

    @FXML
    private TextField txtActors,txtDuration,txtEnglishName,txtHVPrice,txtHebrewName, txtTheaterPrice, txtProducer;

    @FXML
    private TextArea  txtDescription;

    @FXML
    private Label  txtAddProduct;

    @FXML
    private Label lblPathImage;

    private EditMovieListBoundary editMovieListBoundary;

    @FXML
    private Label txtTitle;

    @FXML
    private ComboBox<Movie.Availability> comboAvailable;

    @FXML
    private ComboBox<Movie.StreamingType> comboType;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Images.initializeImageHoverEffect(imageContainer, imageProduct, 300, 200);
        initializeComboBox();

        paneContainer.setOnMouseClicked(event -> {
            try {
                handleUploadImage();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initializeComboBox() {
        comboAvailable.getItems().addAll(Movie.Availability.values());
        comboType.getItems().addAll(Movie.StreamingType.values());
        comboGenre.getItems().addAll("action", "comedy", "drama", "horror", "romance", "sci-Fi", "documentary");
        
        comboType.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal == Movie.StreamingType.THEATER_VIEWING) {
                    txtTheaterPrice.setDisable(false);
                    txtHVPrice.setDisable(true);
                    txtHVPrice.setText("0");
                } else if (newVal == Movie.StreamingType.HOME_VIEWING) {
                    txtTheaterPrice.setDisable(true);
                    txtHVPrice.setDisable(false);
                    txtTheaterPrice.setText("0");
                } else {
                    txtTheaterPrice.setDisable(false);
                    txtHVPrice.setDisable(false);
                }
            }
        });
    }

    @FXML
    private void handleUploadImage() throws IOException {
        Stage stage = new Stage();
        imageFile = Images.uploadMovieImage(stage, imageProduct);

        if (imageFile != null) {
            Images.setImageToMovie(imageFile, movie);
            lblPathImage.setText(imageFile.getName());
        }
    }

    public void setEditMovieListBoundary(EditMovieListBoundary editMovieListBoundary) {
        this.editMovieListBoundary = editMovieListBoundary;
    }

    public void setDialog(String operation, Movie movie) {
        this.currentMode = operation;
        this.movie = "add".equals(operation) ? new Movie() : movie;
        switch (operation) {
            case "view":
                populateFieldsForView();
                break;
            case "add":
                prepareForNewProduct();
                break;
            case "edit":
                populateFieldsForEdit();
                break;
        }
    }


    private void prepareForNewProduct() {
        cleanControls();
        enableEditControls();
        txtAddProduct.setText("Add Movie");
        comboAvailable.getItems().remove(Movie.Availability.NOT_AVAILABLE);
        btnSave.setVisible(true);
    }

    private void populateFields(boolean isEditMode) {
        txtEnglishName.setText(movie.getEnglishName());
        txtHebrewName.setText(movie.getHebrewName());
        txtProducer.setText(movie.getProducer());
        txtDuration.setText(String.valueOf(movie.getDuration()));
        txtTheaterPrice.setText(String.valueOf(movie.getTheaterPrice()));
        txtHVPrice.setText(String.valueOf(movie.getHomeViewingPrice()));
        comboGenre.setValue(movie.getGenre());
        comboType.setValue(movie.getStreamingType());
        comboAvailable.setValue(movie.getAvailability());
        txtDescription.setText(movie.getInfo());
        txtActors.setText(String.join(", ", movie.getMainActors()));
        imageProduct.setImage(getImage(movie));
         lblPathImage.setText(movie.getImage());

        if (isEditMode) {
            txtAddProduct.setText("Update Movie");
            enableEditControls();
            btnSave.setVisible(true);
        } else {
            txtAddProduct.setText("View Movie");
            expandImage(paneContainer, imageProduct, movie, movie.getEnglishName());
            disableEditControls();
            btnSave.setVisible(false);
        }
    }

    private void populateFieldsForEdit() {
        populateFields(true);
    }

    private void populateFieldsForView() {
        populateFields(false);
    }

    private void enableEditControls() {
        Arrays.asList(txtEnglishName, txtHebrewName, txtProducer, txtDuration, txtTheaterPrice, txtHVPrice, txtDescription, txtActors)
                .forEach(field -> field.setEditable(true));
        comboGenre.setDisable(false);
        comboType.setDisable(false);
        comboAvailable.setDisable(false);
    }

    private void disableEditControls() {
        Arrays.asList(txtEnglishName, txtHebrewName, txtProducer, txtDuration, txtTheaterPrice, txtHVPrice, txtDescription, txtActors)
                .forEach(field -> field.setEditable(false));
        comboGenre.setDisable(true);
        comboType.setDisable(true);
        comboAvailable.setDisable(true);
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (!validateInputs()) return;

        String englishName = txtEnglishName.getText().trim();
        String hebrewName = txtHebrewName.getText().trim();
        String producer = txtProducer.getText().trim();
        String duration = txtDuration.getText().trim();
        String theaterPrice = txtTheaterPrice.getText().trim();
        String hvPrice = txtHVPrice.getText().trim();
        String genre = comboGenre.getSelectionModel().getSelectedItem();
        Movie.StreamingType streaming = comboType.getSelectionModel().getSelectedItem();
        Movie.Availability availability = comboAvailable.getSelectionModel().getSelectedItem();
        String description = txtDescription.getText().trim();
        List<String> actors = Arrays.asList(txtActors.getText().trim().split(", "));

        boolean imageChanged = imageFile != null;

        if (imageChanged) {
            try {

                Images.setImageToMovie(imageFile, movie);

            } catch (IOException e) {
                e.printStackTrace();
                NotificationsBuilder.create(NotificationType.ERROR, "Failed to update image.", containerAddProduct);
                return;
            }
        }

        boolean detailsChanged = !englishName.equals(movie.getEnglishName()) ||
                !hebrewName.equals(movie.getHebrewName()) ||
                !producer.equals(movie.getProducer()) ||
                !description.equals(movie.getInfo()) ||
                !genre.equals(movie.getGenre()) ||
                streaming != movie.getStreamingType() ||
                availability != movie.getAvailability() ||
                !actors.equals(movie.getMainActors()) ||
                Integer.parseInt(duration) != movie.getDuration();

        boolean priceChanged = Integer.parseInt(theaterPrice) != movie.getTheaterPrice() ||
                Integer.parseInt(hvPrice) != movie.getHomeViewingPrice();

        if (!detailsChanged && !priceChanged && !imageChanged) {
            NotificationsBuilder.create(NotificationType.INFORMATION, "No changes detected, nothing was saved.", containerAddProduct);
            return;
        }

        if ("add".equals(currentMode)) {
            MovieController.addMovie(
                    hebrewName, description, producer, englishName, String.valueOf(actors),
                    movie.getImage() != null ? movie.getImage() : "empty-image.jpg",
                    movie.getImageBytes() != null ? movie.getImageBytes() : new byte[0],
                    streaming, Integer.parseInt(duration), Integer.parseInt(theaterPrice),
                    Integer.parseInt(hvPrice), genre, availability
            );
        } else {
            if (detailsChanged || imageChanged) {
                MovieController.updateMovie(
                        movie, hebrewName, description, producer, englishName,
                        String.join(", ", actors),
                        movie.getImage() != null ? movie.getImage() : "empty-image.jpg",
                        movie.getImageBytes() != null ? movie.getImageBytes() : new byte[0],
                        streaming, Integer.parseInt(duration), genre, availability
                );
            }

            if (priceChanged) {
                if (!theaterPrice.isEmpty() && Integer.parseInt(theaterPrice) != movie.getTheaterPrice()) {
                    PriceRequestController.createNewPriceRequest(Integer.parseInt(theaterPrice), movie, Movie.StreamingType.THEATER_VIEWING);
                }

                if (!hvPrice.isEmpty() && Integer.parseInt(hvPrice) != movie.getHomeViewingPrice()) {
                    PriceRequestController.createNewPriceRequest(Integer.parseInt(hvPrice), movie, Movie.StreamingType.HOME_VIEWING);
                }
            }
        }

        cleanControls();
        closeDialog();
    }

    @FXML
    private void handleClose(ActionEvent event) {
        closeDialog();
    }

    private void closeDialog() {
        editMovieListBoundary.closeDialogAddProduct();
    }



     private boolean validateInputs() {

        // Check if the English name text field is empty
        if (txtEnglishName.getText().trim().isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR, "English name cannot be empty", containerAddProduct);
            showErrorAndFocus(txtEnglishName); // Show error and focus on the field
            return false;
        }

        // Check if only English letters are allowed
        if (!txtEnglishName.getText().trim().matches("[a-zA-Z0-9 ,.!&:-]+")) {// Allows only English letters and spaces
            NotificationsBuilder.create(NotificationType.ERROR, "Only English letters are allowed", containerAddProduct);
            showErrorAndFocus(txtEnglishName);
            return false;
        }

        // Check if the Hindi name text field is empty
        if (txtHebrewName.getText().trim().isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR, "Hindi name cannot be empty", containerAddProduct);
            showErrorAndFocus(txtHebrewName); // Show error and focus on the field
            return false;
        }

        // Check if only Hebrew letters are allowed
        if (!txtHebrewName.getText().trim().matches("[\\p{L}0-9 ,.!'&:\\-]+")) {
            NotificationsBuilder.create(NotificationType.ERROR, "Only valid letters are allowed", containerAddProduct);
            showErrorAndFocus(txtHebrewName);
            return false;
        }


        // Check if the producer text field is empty
        if (txtProducer.getText().trim().isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR, "Producer cannot be empty", containerAddProduct);
            showErrorAndFocus(txtProducer); // Show error and focus on the field
            return false;
        }

        // Check if the Duration text field is empty
        if (txtDuration.getText().trim().isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR, "Duration cannot be empty", containerAddProduct);
            showErrorAndFocus(txtDuration); // Show error and focus on the field
            return false;
        }

        // Validate if the Duration contains only numbers
         if (!(txtDuration.getText().trim().matches("\\d+"))) {
             NotificationsBuilder.create(NotificationType.ERROR, "Duration must be a valid positive number", containerAddProduct);
             showErrorAndFocus(txtDuration); // Show error and focus on the field
             return false;
         }

        // Check if the Genre combo box is empty
        if (comboGenre.getValue() == null) {
            NotificationsBuilder.create(NotificationType.ERROR, "Genre cannot be empty", containerAddProduct);
            comboGenre.requestFocus();
            Animations.shake(comboGenre);
            return false;
        }

        // Check if the Available combo box is empty
        if (comboAvailable.getValue() == null) {
            NotificationsBuilder.create(NotificationType.ERROR, "Available cannot be empty", containerAddProduct);
            comboAvailable.requestFocus();
            Animations.shake(comboAvailable);
            return false;
        }

        // Check if the Type combo box is empty
        if (comboType.getValue() == null) {
            NotificationsBuilder.create(NotificationType.ERROR, "Type of movie cannot be empty", containerAddProduct);
            comboType.requestFocus();
            Animations.shake(comboType);
            return false;
        }

        // Check if the Actors text field is empty
        if (txtActors.getText().trim().isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR, "Actors cannot be empty", containerAddProduct);
            showErrorAndFocus(txtActors); // Show error and focus on the field
            return false;
        }

        if (!txtHVPrice.isDisabled()) {
            if (txtHVPrice.getText().trim().isEmpty()) {
                NotificationsBuilder.create(NotificationType.ERROR, "Home video price cannot be empty", containerAddProduct);
                showErrorAndFocus(txtHVPrice);
                return false;
            }
            if (!(txtHVPrice.getText().trim().matches("\\d+"))) {
                NotificationsBuilder.create(NotificationType.ERROR, "Home video price must be a valid positive number", containerAddProduct);
                showErrorAndFocus(txtHVPrice);
                return false;
            }
        } else {
            txtHVPrice.setText("0");
        }

        if (!txtTheaterPrice.isDisabled()) {
            if (txtTheaterPrice.getText().trim().isEmpty()) {
                NotificationsBuilder.create(NotificationType.ERROR, "Theater price cannot be empty", containerAddProduct);
                showErrorAndFocus(txtTheaterPrice);
                return false;
            }
            if (!(txtTheaterPrice.getText().trim().matches("\\d+"))) {
                NotificationsBuilder.create(NotificationType.ERROR, "Theater price must be a valid positive number", containerAddProduct);
                showErrorAndFocus(txtTheaterPrice);
                return false;
            }
        } else {
            txtTheaterPrice.setText("0");
        }

        // Check if the description text field is empty
        if (txtDescription.getText().trim().isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR, "Description cannot be empty", containerAddProduct);
            showErrorAndFocus(txtDescription); // Show error and focus on the field
            return false;
        }


        // Check if an image file is provided and trigger an error if it exists
        if (imageFile != null && imageFile.length() > LIMIT) {
            Animations.shake(imageContainer); // Add a shake animation to indicate an error
            NotificationsBuilder.create(NotificationType.ERROR, ConstantsPath.MESSAGE_IMAGE_LARGE,containerAddProduct);
            return false;
        }

        return true;
    }


    private void showErrorAndFocus(TextInputControl field) {
        field.requestFocus();
        Animations.shake(field);
    }

    private void cleanControls() {
        imageFile = null;
        Arrays.asList(txtEnglishName, txtHebrewName, txtProducer, txtDuration, txtTheaterPrice, txtHVPrice, txtDescription, txtActors)
                .forEach(TextInputControl::clear);
        imageProduct.setImage(new Image(ConstantsPath.NO_IMAGE_AVAILABLE));
    }


}

