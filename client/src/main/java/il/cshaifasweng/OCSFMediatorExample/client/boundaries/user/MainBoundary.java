
package il.cshaifasweng.OCSFMediatorExample.client.boundaries.user;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.InvocationTargetException;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleChatClient;
import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.LoginPageController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.RegisteredUserController;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.RegisteredUserMessage;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.DialogTool;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.EmployeeLoginMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.LoginMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainBoundary implements Initializable {
    @FXML
    private ImageView eyeIcon;
    @FXML
    private TextField txtPasswordVisible;

    @FXML
    private Button btnSpace;
    @FXML
    private VBox vboxSide;
    @FXML
    private ImageView imgLog;

    @FXML
    private AnchorPane rootTopMenu;
    @FXML
    private Label lblWelcome;
    private ObservableList<MovieInstance> listProducts;

    private ObservableList<MovieInstance> filterProducts;

    @FXML
    private Label Complaints;

    @FXML
    private Button btnAbout;

    @FXML
    private Button btnComplaints;

    @FXML
    private Button btnEditMovieList;

    @FXML
    private Button btnEditScreenings;

    @FXML
    private Button btnLog;

    @FXML
    private Button btnHome;

    @FXML
    private Button btnME;

    @FXML
    private Button btnOrders;

    @FXML
    private Button btnPriceChange;
    @FXML
    private Button btnReports;

    @FXML
    private Button btnManageTheater;

    @FXML
    private Button btnSettings;

    @FXML
    private VBox customerLogin;

    @FXML
    private VBox registerForm;

    @FXML
    private TextField txtRegName;

    @FXML
    private TextField txtRegPhone;

    @FXML
    private TextField txtRegEmail;

    @FXML
    private RadioButton customerRadioButton;

    @FXML
    private VBox employeeLogin;

    @FXML
    private RadioButton employeeRadioButton;

    @FXML
    private AnchorPane pnLogIn;

    @FXML
    private AnchorPane rootContainer;

    @FXML
    private AnchorPane rootSideMenu;

    @FXML
    private StackPane stckMain;

    @FXML
    private AnchorPane tooltipComplaints;

    @FXML
    private AnchorPane tooltipEditMovieList;

    @FXML
    private AnchorPane tooltipEditScreening;

    @FXML
    private AnchorPane tooltipOrders;

    @FXML
    private AnchorPane tooltipPrice;

    @FXML
    private AnchorPane tooltipReports;

    @FXML
    private AnchorPane tooltipManageTheater;

    @FXML
    private ToggleGroup userTypeToggleGroup;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUser;

    @FXML
    private TextField txtEmployee;

    @FXML
    private ImageView image;

    private DialogTool dialogLogIn;
    private static String loggedInUserId;

    public static void setUserAfterRegistration(String userId) {
        loggedInUserId = userId;
        Platform.runLater(() -> {
            if (SimpleChatClient.mainBoundary != null) {
                if (loggedInEmployeeId != null) {
                    SimpleChatClient.mainBoundary.updateEmployeeBasedOnRole(userId, loggedInEmployeeId);
                } else {
                    SimpleChatClient.mainBoundary.updateUserBasedOnRole(userId);
                }
                SimpleChatClient.mainBoundary.imgLog.setImage(new Image(Objects.requireNonNull(MainBoundary.class.getResourceAsStream(ConstantsPath.LOGOUT_ICON))));
            }
        });
    }
    public static Employee.EmployeeType loggedInEmployeeId;
    private boolean isPasswordVisible = false;

    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getDefault().register(this);
        txtUser.setStyle("-fx-text-fill: #cae8fb;");
        txtEmployee.setStyle("-fx-text-fill: #cae8fb;");
        txtPassword.setStyle("-fx-text-fill: #cae8fb;");
         eyeIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ConstantsPath.GENERAL_PACKAGE+"close-eye.png"))));

        // Create the ToggleGroup and add the radio buttons to it
        ToggleGroup userTypeToggleGroup = new ToggleGroup();
        customerRadioButton.setToggleGroup(userTypeToggleGroup);
        employeeRadioButton.setToggleGroup(userTypeToggleGroup);

        // Set the initial selection if needed
        customerRadioButton.setSelected(true);

        // Add a listener to clear text fields on selection change
        userTypeToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            clearTextFields();
        });
        txtUser.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtUser.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtUser.getText().length() > 10) {
                txtUser.setText(txtUser.getText().substring(0, 10));
            }
        });
        txtRegPhone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtRegPhone.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (txtRegPhone.getText().length() > 10) {
                txtRegPhone.setText(txtRegPhone.getText().substring(0, 10));
            }
        });
        lblWelcome.setText(setWelcomeMessage() + "Welcome To 'BookMySeat'!");

        SimpleChatClient.mainBoundary = this;

        homeWindowsInitialize();
        resetButtons();
        tooltips();
    }

    private void clearTextFields() {
        txtUser.clear();
        txtEmployee.clear();
        txtPassword.clear();
        txtPasswordVisible.clear();
        txtRegName.clear();
        txtRegPhone.clear();
        txtRegEmail.clear();
        imgLog.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ConstantsPath.LOGIN_ICON))));
        lblWelcome.setText(setWelcomeMessage() + "Welcome To 'BookMySeat'!");
    }

    public void homeWindowsInitialize() {
        showFXMLWindows(ConstantsPath.HOME_VIEW);
    }

    @FXML
    private void homeWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.HOME_VIEW);
    }

    @FXML
    private void EditMovieScreeningsWindows(ActionEvent event) { showFXMLWindows(ConstantsPath.CONTENT_SCREENINGS_VIEW);}

    @FXML
    private void CustomerServiceWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.CUSTOMER_SERVICE_VIEW);
    }

    @FXML
    private void settingsWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.COMPLAINT_VIEW);
    }

    @FXML
    private void statisticsWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.COMPANY_MANAGER_VIEW);
    }

    @FXML
    private void aboutWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.ABOUT_VIEW);
    }

    @FXML
    private void productsWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.CONTENT_MOVIES_VIEW);
    }

    @FXML
    private void priceChangeWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.PRICE_CHANGE_VIEW);
    }

    @FXML
    private void addUserWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.ORDERS_VIEW);
    }

    @FXML
    private void manageTheaterWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.THEATER_MANAGER_VIEW);
    }

    @FXML
    private void MEWindows(ActionEvent event) {
        showFXMLWindows(ConstantsPath.ME_PURCHASE_VIEW);
    }

    @FXML
    private void closeLoginDialog() {
        if (dialogLogIn != null) {
            dialogLogIn.close();
        }
    }

    @FXML
    private void showRegisterForm() {
        customerLogin.setVisible(false);
        customerLogin.setManaged(false);
        registerForm.setVisible(true);
        registerForm.setManaged(true);
        txtRegName.clear();
        txtRegPhone.clear();
        txtRegEmail.clear();
        if (pnLogIn.getScene() != null && pnLogIn.getScene().getWindow() != null) {
            pnLogIn.getScene().getWindow().sizeToScene();
        }
    }

    @FXML
    private void showLoginForm() {
        registerForm.setVisible(false);
        registerForm.setManaged(false);
        customerLogin.setVisible(true);
        customerLogin.setManaged(true);
        if (pnLogIn.getScene() != null && pnLogIn.getScene().getWindow() != null) {
            pnLogIn.getScene().getWindow().sizeToScene();
        }
    }

    @FXML
    private void register() {
        String name = txtRegName.getText().trim();
        String phone = txtRegPhone.getText().trim();
        String email = txtRegEmail.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            NotificationsBuilder.create(NotificationType.ERROR, "Please fill all fields.", stckMain);
            return;
        }
        if (!phone.matches("\\d{10}")) {
            NotificationsBuilder.create(NotificationType.ERROR, "Phone Number must be exactly 10 digits.", stckMain);
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            NotificationsBuilder.create(NotificationType.ERROR, "Please enter a valid email address.", stckMain);
            return;
        }
        RegisteredUserController.addNewUser(phone, name, "", email);
    }

    @Subscribe
    public void handleRegisterResponse(RegisteredUserMessage message) {
        Platform.runLater(() -> {
            if (registerForm.isVisible()) {
                if (message.responseType == RegisteredUserMessage.ResponseType.USER_ADDED) {
                    NotificationsBuilder.create(NotificationType.SUCCESS,
                            "Registration successful! You can now login with " + message.registeredUser.getId_number(), stckMain);
                    showLoginForm();
                } else if (message.responseType == RegisteredUserMessage.ResponseType.USER_DID_NOT_ADDED) {
                    NotificationsBuilder.create(NotificationType.ERROR,
                            "Phone number already registered. Please login instead.", stckMain);
                }
            }
        });
    }

    @FXML
    private void loginWindow() {
        showLoginForm(); // Always reset to login form when dialog opens
        rootContainer.setEffect(ConstantsPath.BOX_BLUR_EFFECT);
        pnLogIn.setVisible(true);
        dialogLogIn = new DialogTool(pnLogIn, stckMain);
        dialogLogIn.setOnDialogOpened(ev -> employeeLogin.requestFocus());
        dialogLogIn.show();
        handleRadioButtonAction();
        dialogLogIn.setOnDialogClosed(ev -> {
            rootContainer.setEffect(null);
            pnLogIn.setVisible(false);
        });
        pnLogIn.toFront();
    }

    @FXML
    private void handleRadioButtonAction() {
        // Always reset register form to hidden
        registerForm.setVisible(false);
        registerForm.setManaged(false);
        if (customerRadioButton.isSelected()) {
            customerLogin.setVisible(true);
            customerLogin.setManaged(true);
            employeeLogin.setVisible(false);
            employeeLogin.setManaged(false);
        } else if (employeeRadioButton.isSelected()) {
            customerLogin.setVisible(false);
            customerLogin.setManaged(false);
            employeeLogin.setVisible(true);
            employeeLogin.setManaged(true);
        }
        if (pnLogIn.getScene() != null && pnLogIn.getScene().getWindow() != null) {
            pnLogIn.getScene().getWindow().sizeToScene();
        }
    }

    @FXML
    private void closeStage() {
        ((Stage) btnHome.getScene().getWindow()).close();
    }

    private void setDisableButtons(ActionEvent event, Button button) {
        if (event.getSource().equals(button)) {
            button.setDisable(true);
        } else {
            button.setDisable(false);
        }
    }

    private void tooltips() {
        Animations.tooltip(btnEditMovieList, tooltipEditMovieList);
        Animations.tooltip(btnEditScreenings, tooltipEditScreening);
        Animations.tooltip(btnOrders, tooltipOrders);
        Animations.tooltip(btnPriceChange, tooltipPrice);
        Animations.tooltip(btnComplaints, tooltipComplaints);
        Animations.tooltip(btnReports, tooltipReports);
        Animations.tooltip(btnManageTheater, tooltipManageTheater);
    }

    private static Object currentController;

     public static void setCurrentController(Object new_currentController) {
        currentController = new_currentController;
    }

    public void executeCleanup() {
        if (currentController != null) {
            try {
                currentController.getClass().getMethod("cleanup").invoke(currentController);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            }
        }
    }

    private void showFXMLWindows(String FXMLName) {
         executeCleanup();
        rootContainer.getChildren().clear();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLName));
            Parent root = loader.load();
            currentController = loader.getController();
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            rootContainer.getChildren().setAll(root);
        } catch (IOException ex) {
            Logger.getLogger(MainBoundary.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Button getBtnStatistics() {
        return btnReports;
    }

    public Button getBtnAbout() {
        return btnAbout;
    }

    @FXML
    private void login(ActionEvent event) {
        String UserName = txtUser.getText();
        String EmployeeName = txtEmployee.getText();
        String password;
        if(isPasswordVisible)
            password = txtPasswordVisible.getText();
        else
            password = txtPassword.getText();
        if (customerRadioButton.isSelected()) {
            // Phone number validation: exactly 10 digits
            if (UserName.isEmpty()) {
                NotificationsBuilder.create(NotificationType.ERROR, "Please enter your Phone Number.", stckMain);
                return;
            }
            if (!UserName.matches("\\d{10}")) {
                NotificationsBuilder.create(NotificationType.ERROR, "Phone Number must be exactly 10 digits (numbers only).", stckMain);
                return;
            }
            LoginPageController.requestUserLogin(UserName);
            System.out.println("SEND Login");
        } else if (employeeRadioButton.isSelected()) {
            LoginPageController.requestEmployeeLogin(EmployeeName, password);
            System.out.println("SEND EMPLOYEE Login");
        }
        resetButtons();
        clearTextFields();
        homeWindowsInitialize();
    }

    @Subscribe
    public void handleLoginResponse(LoginMessage loginMessage) {
        if (loginMessage instanceof EmployeeLoginMessage) {
            Platform.runLater(() -> {
                        handleEmployeeLoginResponse((EmployeeLoginMessage) loginMessage);
                        String role = "";
                        if (((EmployeeLoginMessage) loginMessage).employeeType != null) {

                            switch (((EmployeeLoginMessage) loginMessage).employeeType) {
                                case THEATER_MANAGER:
                                    role = "Theater Manager";
                                    break;
                                case COMPANY_MANAGER:
                                    role = "Company Manager";
                                    break;
                                case CUSTOMER_SERVICE:
                                    role = "Customer Service";
                                    break;
                                case CONTENT_MANAGER:
                                    role = "Content Manager";
                                    break;
                            }
                            lblWelcome.setText(setWelcomeMessage() + role + "!");
                        }
                    }
            );
        } else {
            Platform.runLater(() -> handleCustomerLoginResponse(loginMessage));
        }
    }

    public String setWelcomeMessage() {
        LocalTime now = LocalTime.now();
        String greeting;

        if (now.isBefore(LocalTime.NOON)) {
            greeting = "Good Morning, ";
        } else if (now.isBefore(LocalTime.of(18, 0))) {
            greeting = "Good Afternoon, ";
        } else {
            greeting = "Good Evening ,";
        }
        return greeting;
    }

    public static String getId() {
        return loggedInUserId;
    }

    public static Employee.EmployeeType getEmployee() {
        return loggedInEmployeeId;
    }

    public void handleCustomerLoginResponse(LoginMessage loginMessage) {
        Platform.runLater(() -> {
            if (loginMessage.responseType == LoginMessage.ResponseType.LOGIN_SUCCESFUL) {
                lblWelcome.setText(setWelcomeMessage() + "Nice to see you again in 'BookMySeat'!");

                SimpleClient.user = loginMessage.id; // Save the logged-in user ID

                loggedInUserId = loginMessage.id; // Save the logged-in user ID
                NotificationsBuilder.create(NotificationType.SUCCESS, "Registered User " + loginMessage.id + " Logged in!",stckMain);

                // Update the UI based on the user's role
                updateUserBasedOnRole(loginMessage.id);
                imgLog.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ConstantsPath.LOGOUT_ICON))));

                // Close the login dialog or do any other post-login actions
                closeLoginDialog();
            } else if (loginMessage.responseType == LoginMessage.ResponseType.LOGIN_FAILED) {
                // Handle login failure (show error message, etc.)
                NotificationsBuilder.create(NotificationType.ERROR, "Login failed. Please check your credentials.",stckMain);

            } else if (loginMessage.responseType == LoginMessage.ResponseType.ALREADY_LOGGED) {

                NotificationsBuilder.create(NotificationType.ERROR, "Registered User is already logged in.",stckMain);
            }
        });
    }

    public void handleEmployeeLoginResponse(EmployeeLoginMessage loginMessage) {
        Platform.runLater(() -> {
            if (loginMessage.responseType == LoginMessage.ResponseType.LOGIN_SUCCESFUL) {
                SimpleClient.user = loginMessage.id; // Save the logged-in employee ID
                loggedInUserId = loginMessage.id; // Save the logged-in employee ID
                loggedInEmployeeId = loginMessage.employeeType; // Save the logged-in employee type
                imgLog.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ConstantsPath.LOGOUT_ICON))));
                updateEmployeeBasedOnRole(loginMessage.id, loginMessage.employeeType);
                NotificationsBuilder.create(NotificationType.SUCCESS, "Employee" + loginMessage.employeeType + loginMessage.employeeType.name() + "Logged in!",stckMain);
                closeLoginDialog();
            } else if (loginMessage.responseType == LoginMessage.ResponseType.LOGIN_FAILED) {

                NotificationsBuilder.create(NotificationType.ERROR, "Login failed. Please check your credentials.",stckMain);
            } else if (loginMessage.responseType == LoginMessage.ResponseType.ALREADY_LOGGED) {
                // Handle the case where the user is already logged in
                NotificationsBuilder.create(NotificationType.ERROR, "User is already logged in.",stckMain);
            }
        });
    }

    private void resetButtons() {
        lblWelcome.setText(setWelcomeMessage() + "Welcome To 'BookMySeat'!");
        vboxSide.getChildren().clear();
    }

    private void updateUserBasedOnRole(String userId) {
        VBox vboxSide = (VBox) rootSideMenu.lookup("#vboxSide");
        vboxSide.getChildren().clear();
        vboxSide.getChildren().add(btnSpace);
        vboxSide.getChildren().add(btnOrders);
    }

    private void updateEmployeeBasedOnRole(String userId, Employee.EmployeeType role) {
        VBox vboxSide = (VBox) rootSideMenu.lookup("#vboxSide");
        vboxSide.getChildren().clear();
        vboxSide.getChildren().add(btnSpace);
        vboxSide.getChildren().add(btnOrders); // Allow employees to see their personal orders
        switch (role) {
            case CONTENT_MANAGER:
                vboxSide.getChildren().add(btnEditMovieList);
                vboxSide.getChildren().add(btnEditScreenings);
                break;
            case CUSTOMER_SERVICE:
                vboxSide.getChildren().add(btnComplaints);
                break;
            case THEATER_MANAGER:
                vboxSide.getChildren().add(btnReports);
                vboxSide.getChildren().add(btnManageTheater);
                break;
            case COMPANY_MANAGER:
                vboxSide.getChildren().add(btnReports);
                vboxSide.getChildren().add(btnPriceChange);
                break;
        }
    }

    @FXML
    private void logout() {
        if (loggedInUserId != null || loggedInEmployeeId != null) {
            AlertsBuilder.create(AlertType.WARNING, stckMain, stckMain, stckMain, "Are you sure you want to log out?", "Yes", () -> {
                        sendLogoutRequest();
                        loggedInUserId = null;
                        loggedInEmployeeId = null;
                        resetButtons();
                        clearTextFields();
                        closeLoginDialog();
                        imgLog.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ConstantsPath.LOGIN_ICON))));
                        homeWindowsInitialize();
                        AlertsBuilder.create(AlertType.SUCCESS, stckMain, stckMain, stckMain, "You have successfully logged out.");
                    }, "No", () -> {
                        AlertsBuilder.create(AlertType.SUCCESS, stckMain, stckMain, stckMain, "Have A nice Stay in 'BookMySeat'!");
                    }
            );
        } else {
            loginWindow();
        }
    }

    public static void sendLogoutRequest() {
        if (loggedInUserId != null) {
            SimpleClient.user = ""; // Save the logged-in user ID
            if (loggedInEmployeeId != null)
                LoginPageController.requestEmployeeLogOut(loggedInUserId);
            else
                LoginPageController.requestUserLogOut(loggedInUserId);
        }
    }


    public void togglePasswordVisibility(ActionEvent actionEvent) {
        if (isPasswordVisible) {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setManaged(true);
            txtPassword.setVisible(true);
            txtPasswordVisible.setManaged(false);
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.clear();
            eyeIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ConstantsPath.GENERAL_PACKAGE + "close-eye.png"))));
        } else {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setManaged(true);
            txtPasswordVisible.setVisible(true);
            txtPassword.setManaged(false);
            txtPassword.setVisible(false);
            txtPassword.clear();
            eyeIcon.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(ConstantsPath.GENERAL_PACKAGE+"view.png"))));
        }
        isPasswordVisible = !isPasswordVisible;
    }
    }
