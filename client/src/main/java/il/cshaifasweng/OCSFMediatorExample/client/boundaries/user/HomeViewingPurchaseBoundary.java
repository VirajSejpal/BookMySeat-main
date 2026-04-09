package il.cshaifasweng.OCSFMediatorExample.client.boundaries.user;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleChatClient;
import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.PurchaseController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.RegisteredUserController;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import il.cshaifasweng.OCSFMediatorExample.entities.HomeViewingPackageInstance;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.RegisteredUserMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Movie;
import il.cshaifasweng.OCSFMediatorExample.entities.RegisteredUser;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.PurchaseMessage;
import il.cshaifasweng.OCSFMediatorExample.server.events.HomeViewingEvent;
import il.cshaifasweng.OCSFMediatorExample.server.events.MovieEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import static il.cshaifasweng.OCSFMediatorExample.client.util.assets.Images.getImage;

public class HomeViewingPurchaseBoundary {

    @FXML
    private StackPane stackPane;

    @FXML
    private VBox ticketSelectionPane;

    @FXML
    private VBox paymentDetailsPane;

    @FXML
    private GridPane nodetails;

    @FXML
    private VBox creditCardPane;

    @FXML
    private VBox idPhonePane;

    @FXML
    private VBox ticketConfirmationPane;

    @FXML
    private ImageView movieImage;

    @FXML
    private ImageView confirmationMovieImage;

    @FXML
    private Label movieTitle;

    @FXML
    private Label movieTime;

    @FXML
    private Label movieDate;

    @FXML
    private TextField cardNumberField;

    @FXML
    private ComboBox<String> expirationMonthCombo;

    @FXML
    private ComboBox<String> expirationYearCombo;

    @FXML
    private TextField cvvField;

    @FXML
    private TextField idField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label step2Label;

    @FXML
    private Label step2Text;

    @FXML
    private Label step3Label;

    @FXML
    private Label step3Text;

    @FXML
    private Label price;

    //the user details
    @FXML
    private TextField firstNameTF;

    @FXML
    private TextField lastNameTF;

    @FXML
    private Text movieText,movieEnglish,movieHebrew,availableOnText,totalAmountLabel,pricePaidText,validUntilText;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField confirmEmailTF;

    @FXML
    private TextField idNumberTF;

    @FXML
    private TextField confirmIdNumberTF;

    private RegisteredUser user=null;
    private String link;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private LocalDateTime dateTime;
    private double packagePrice;
    private Movie currentMovie;
    private HomeViewingPackageInstance homeViewingPackageInstance;

    @FXML
    public void initialize() {
        EventBus.getDefault().register(this);
        stackPane.getChildren().clear();
        stackPane.getChildren().add(ticketSelectionPane);
        highlightStep(1);
        user=null;
        if(!SimpleClient.user.isEmpty()) {
            RegisteredUserController.getUserByID(SimpleClient.user);
        }
        populateExpirationMonths();
        populateExpirationYears();
    }

    private void populateExpirationMonths() {
        for (int i = 1; i <= 12; i++) {
            expirationMonthCombo.getItems().add(String.format("%02d", i));
        }
    }

    private void populateExpirationYears() {
        int currentYear = Year.now().getValue();
        for (int i = 0; i < 10; i++) {
            expirationYearCombo.getItems().add(String.valueOf(currentYear + i));
        }
    }

    private void highlightStep(int step) {

        step2Label.setStyle("-fx-text-fill: white;");
        step2Text.setStyle("-fx-text-fill: white;");
        step3Label.setStyle("-fx-text-fill: white;");
        step3Text.setStyle("-fx-text-fill: white;");
        step2Label.setStyle("-fx-background-color: #1d1d48");
        step3Label.setStyle("-fx-background-color: #1d1d48");
        switch (step) {
            case 1:
                step2Label.setStyle("-fx-text-fill: #ffc500;");
                step2Text.setStyle("-fx-text-fill: #ffc500;");
                step2Label.setStyle("-fx-background-color: #ffc500");

                break;
            case 2:
                step3Label.setStyle("-fx-text-fill: #ffc500;");
                step3Text.setStyle("-fx-text-fill: #ffc500;");
                step3Label.setStyle("-fx-background-color: #ffc500");
                break;

        }
        Animations.hover(step2Label, 200, 1.2);
        Animations.hover(step3Label, 200, 1.2);
    }

    @FXML
    private void showIdPhoneFields() {
        idPhonePane.setVisible(true);
    }

    @FXML
    private void hideIdPhoneFields() {
        idPhonePane.setVisible(false);
    }

    @FXML
    private void goToPaymentDetails() {
        stackPane.getChildren().clear();
        if(user!=null)
        {
            stackPane.getChildren().add(creditCardPane);
            creditCardPane.setVisible(true);
        }
        else
            stackPane.getChildren().add(paymentDetailsPane);

        highlightStep(2);
    }

    @FXML
    private void goToTicketSelection() {
        stackPane.getChildren().clear();
        stackPane.getChildren().add(ticketSelectionPane);
        highlightStep(1);
    }

    @FXML
    private void showCreditCardFields()
    {
        if(checkDetails()) {
            creditCardPane.setVisible(true);
        }
    }

    @FXML
    private void submitPayment()
    {
        // check if card info is in the right format
        if(isValidCreditCardInfo())
        {
            System.out.println("Payment submitted with card number: " + cardNumberField.getText());
            creditCardPane.setVisible(false);

            if (user == null)
                RegisteredUserController.addNewUser(idNumberTF.getText(), firstNameTF.getText(), lastNameTF.getText(), emailTF.getText());
            else {
                RegisteredUserController.addNewUser(user.getId_number(), "", "", "");
            }
        }
    }

    @Subscribe
    public void onRegisteredUserReceivedMessage(RegisteredUserMessage message)
    {
        if(message.requestType==RegisteredUserMessage.RequestType.GET_USER_BY_ID)
            user= message.registeredUser;
        else
        {
            String purchaseValidation = cardNumberField.getText() + " " + expirationMonthCombo.getValue()+"/"+expirationYearCombo.getValue() + " " + cvvField.getText();
            link ="https://bookmyseat.com/"+ generateRandomString(10);
            PurchaseController.AddHomeViewing(LocalDateTime.now(), message.registeredUser, purchaseValidation, currentMovie, dateTime.plusHours(3), link);
        }
    }
    public static String generateRandomString(int length) {
        // Define the characters that can be included in the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        // Generate random characters from the 'characters' string
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            result.append(characters.charAt(index));
        }

        return result.toString();
    }
    @Subscribe
    public void onPurchaseReceivedMessage(PurchaseMessage message)
    {
        if (message.responseType == PurchaseMessage.ResponseType.PURCHASE_ADDED)
        {
            homeViewingPackageInstance = (HomeViewingPackageInstance)message.purchases.get(0);
            showConfirmation();
        }
    }

    @Subscribe
    public void onHomeMovieEventRecieved(HomeViewingEvent event)
    {
        if (event.action.equals("delete")&&event.movie.getId()==currentMovie.getId())
        {
            AlertsBuilder.create(AlertType.CANCELLATION, stackPane, stackPane, stackPane, "The selected Movie has been canceled, Please choose a new Movie");
            Platform.runLater(() -> {
                SimpleChatClient.mainBoundary.homeWindowsInitialize();
            });
        }
    }

    @FXML
    private void cancelPayment()
    {
        cardNumberField.clear();
        cvvField.clear();

        if(user == null) {
            // Hide the credit card fields
            creditCardPane.setVisible(false);
        }
        else
            goToTicketSelection();
    }

    @FXML
    private void goToNextStep() {
        if (stackPane.getChildren().contains(ticketSelectionPane)) {
            goToPaymentDetails();
        }
    }

    @FXML
    private void goToPreviousStep() {
        if (stackPane.getChildren().contains(paymentDetailsPane)) {
            goToTicketSelection();
        }
    }

    private void showConfirmation() {
        Platform.runLater(() -> {
            System.out.println("Package purchased successfully.");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDateTime viewingDateTime = homeViewingPackageInstance.getViewingDate().minusHours(3);
            String formattedDate = viewingDateTime.toLocalDate().format(dateFormatter);
            String formattedTime = viewingDateTime.toLocalTime().format(timeFormatter);
            LocalDateTime validUntilDateTime = viewingDateTime.plusWeeks(1);
            String validUntilDate = validUntilDateTime.toLocalDate().format(dateFormatter);
            String validUntilTime = validUntilDateTime.toLocalTime().format(timeFormatter);
            movieEnglish.setText(currentMovie.getEnglishName());
            movieHebrew.setText(currentMovie.getHebrewName());
            availableOnText.setText("Available On: "+formattedDate + " AT " + formattedTime);
            pricePaidText.setText("₹" + homeViewingPackageInstance.getMovie().getHomeViewingPrice());
            validUntilText.setText("Valid Until: "+validUntilDate + " AT " + validUntilTime);
            totalAmountLabel.setText("₹" + homeViewingPackageInstance.getMovie().getHomeViewingPrice());
            stackPane.getChildren().clear();
            stackPane.getChildren().add(ticketConfirmationPane);
        });
    }

    public void cleanup() {
        System.out.println("cleanup");
        EventBus.getDefault().unregister(this);
    }

    public void setCurrentMovie(Movie movie) {
        this.currentMovie = movie;
        price.setText("Package Price: " + movie.getHomeViewingPrice()+"₹");
        movieTitle.setText(currentMovie.getHebrewName() + " | " + currentMovie.getEnglishName());
        movieImage.setImage(getImage(movie));
    }

    public void setCurrentDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
        movieTime.setText("Time: " + dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        movieDate.setText("Date: " + dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

    }

    // a method that checks if the details that the user given is valid , returns true if the details are valid
    private boolean checkDetails()
    {
        if(firstNameTF.getText().isEmpty() || lastNameTF.getText().isEmpty() || emailTF.getText().isEmpty()
                || confirmEmailTF.getText().isEmpty() || idNumberTF.getText().isEmpty() || confirmIdNumberTF.getText().isEmpty())
        {
            NotificationsBuilder.create(NotificationType.ERROR,"One or more fields are missing.",stackPane);
            return false;
        }

        String textFirstName = firstNameTF.getText();
        for (char c : textFirstName.toCharArray()) {
            if (Character.isDigit(c))
            {
                NotificationsBuilder.create(NotificationType.ERROR,"First name contains digits.",stackPane);
                return false;
            }
        }

        if(!textFirstName.matches("^[a-zA-Z]+$"))
        {
            NotificationsBuilder.create(NotificationType.ERROR,"First name contains non-letters.",stackPane);
            return false;
        }

        String textLastName = lastNameTF.getText();
        for (char c : textLastName.toCharArray()) {
            if (Character.isDigit(c))
            {
                NotificationsBuilder.create(NotificationType.ERROR,"Last name contains digits.",stackPane);
                return false;
            }
        }

        if(!textLastName.matches("^[a-zA-Z]+$"))
        {
            NotificationsBuilder.create(NotificationType.ERROR,"Last name contains non-letters.",stackPane);
            return false;
        }

        String textID = idNumberTF.getText();
        for (char c : textID.toCharArray()) {
            if (!Character.isDigit(c))
            {
                NotificationsBuilder.create(NotificationType.ERROR,"ID number contains digits.",stackPane);
                return false;
            }
        }

        if(!idNumberTF.getText().equals(confirmIdNumberTF.getText()))
        {
            NotificationsBuilder.create(NotificationType.ERROR,"ID Number and confirm ID Number do not match!",stackPane);
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        if (!pattern.matcher(emailTF.getText()).matches())
        {
            NotificationsBuilder.create(NotificationType.ERROR,"Email address is invalid.",stackPane);
            return false;
        }


        if(!emailTF.getText().equals(confirmEmailTF.getText()))
        {
            NotificationsBuilder.create(NotificationType.ERROR,"Email and confirm email do not match!",stackPane);
            return false;
        }
        return true;
    }

    // a method that checks if the details that the user given is valid , returns true if the details are valid
    public boolean isValidCreditCardInfo() {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", "");
        String expirationMonth = expirationMonthCombo.getValue();
        String expirationYear = expirationYearCombo.getValue();
        String cvv = cvvField.getText();

        // Check if all fields are filled
        if (cardNumber.isEmpty() || expirationMonth == null || expirationYear == null || cvv.isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR,"All fields must be filled",stackPane);
            return false;
        }

        // Validate card number (using Luhn algorithm)
        if (!isValidCardNumber(cardNumber)) {
            NotificationsBuilder.create(NotificationType.ERROR,"Invalid card number",stackPane);
            return false;
        }

        // Validate expiration date
        if (!isValidExpirationDate(expirationMonth, expirationYear)) {
            NotificationsBuilder.create(NotificationType.ERROR,"Invalid expiration date",stackPane);
            return false;
        }

        // Validate CVV
        if (!isValidCVV(cvv)) {
            NotificationsBuilder.create(NotificationType.ERROR,"Invalid CVV",stackPane);
            return false;
        }

        return true;
    }
    private boolean isValidCardNumber(String cardNumber) {
        // Remove any spaces from the card number
        cardNumber = cardNumber.replaceAll("\\s", "");

        // Check if the card number contains only digits and hyphens
        if (!cardNumber.matches("[0-9-]+")) {
            return false;
        }

        // Remove hyphens for further processing
        String digitsOnly = cardNumber.replaceAll("-", "");

        // Check if there are exactly 16 digits
        if (digitsOnly.length() != 16) {
            return false;
        }

        // Check if the digits are in groups of 4, separated by hyphens (if hyphens are present)
        if (cardNumber.contains("-")) {
            String pattern = "\\d{4}-\\d{4}-\\d{4}-\\d{4}";
            return cardNumber.matches(pattern);
        }

        // If no hyphens, it's valid as long as it's 16 digits
        return true;
    }

    private boolean isValidExpirationDate(String month, String year) {
        YearMonth expirationDate = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        YearMonth currentDate = YearMonth.now();
        return expirationDate.isAfter(currentDate);
    }

    private boolean isValidCVV(String cvv) {
        // Check if the cvv number contains only digits
        if (!cvv.matches("[0-9]+")) {
            return false;
        }
        return cvv.matches("\\d{3,4}");
    }
}
