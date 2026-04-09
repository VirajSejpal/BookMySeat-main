package il.cshaifasweng.OCSFMediatorExample.client.boundaries.user;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleChatClient;
import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ConnectionMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleChatClient.loadFXML;
import static il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleChatClient.scene;


public class StartBoundary {

    @FXML
    private   AnchorPane anchorPane;
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField messageField;

    @FXML
    private Button connectButton;

    @FXML
    private TextField ipField;

    @FXML
    private TextField portField;

    @FXML
    private Button btnAutoConnect;


    @FXML
    private void connect() {
        try {
            SimpleClient.port = Integer.parseInt(portField.getText());
            SimpleClient.host = ipField.getText();
            SimpleChatClient.client = SimpleClient.getClient();
            SimpleChatClient.client.openConnection();
            messageField.setText("Client created, host: " + SimpleClient.host + ", port: " + SimpleClient.port);
            NotificationsBuilder.create(NotificationType.SUCCESS, "Welcome to the system!",anchorPane);
            SimpleClient.getClient().sendRequest(new ConnectionMessage(Message.MessageType.REQUEST, ConnectionMessage.RequestType.FIRST_CONNECTION));
            scene = new Scene(loadFXML("MainView"));
            Stage stage = new Stage(); // Create a new Stage instance
            stage.initStyle(StageStyle.DECORATED);
            stage.setMinHeight(ConstantsPath.MIN_HEIGHT);
            stage.setMinWidth(ConstantsPath.MIN_WIDTH);
            stage.getIcons().add(new Image(ConstantsPath.STAGE_ICON));
            stage.setTitle(ConstantsPath.TITLE);
            stage.setScene(scene);
            stage.show();

            // Add this to handle closing the application when the stage is closed
            stage.setOnCloseRequest(event -> {
                MainBoundary.sendLogoutRequest();
                SimpleChatClient.mainBoundary.executeCleanup();
                Platform.exit();
            });

            // Close the current window
            Stage currentStage = (Stage) connectButton.getScene().getWindow();
            currentStage.close();
            ipField.clear();
            portField.clear();
        } catch (NumberFormatException e) {
            messageField.setText("Invalid port number: " + portField.getText());
            System.err.println("Invalid port number: " + portField.getText());
        } catch (UnknownHostException e) {
            messageField.setText("Failed to connect: Invalid hostname or IP address: " + SimpleClient.host);
            System.err.println("Failed to connect: Invalid hostname or IP address: " + SimpleClient.host);
        } catch (IOException e) {
            messageField.setText("Failed to connect: " + e.getMessage());
            System.err.println("Failed to connect: " + e.getMessage());
        }
    }

    @FXML
    public void AutoConnect(ActionEvent actionEvent) {
        ipField.setText("localhost");
        portField.setText("3000");
        connect();
    }
}