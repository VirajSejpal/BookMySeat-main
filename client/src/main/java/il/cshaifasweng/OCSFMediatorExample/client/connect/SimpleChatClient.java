package il.cshaifasweng.OCSFMediatorExample.client.connect;

import il.cshaifasweng.OCSFMediatorExample.client.boundaries.user.MainBoundary;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ConnectionMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.Message;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * JavaFX App
 */
public class SimpleChatClient extends Application {

    public static Scene scene;
    public static SimpleClient client;
    private static Stage primaryStage;
    public static MainBoundary mainBoundary;


    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        EventBus.getDefault().register(this);

        // Load initial screen
        scene = new Scene(loadFXML("StartView"));
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimpleChatClient.class.getResource(ConstantsPath.USER_PACKAGE + fxml + ".fxml"));
        Parent root = fxmlLoader.load();
        scene.setRoot(root);

        // Register the appropriate controller
        switch (fxml) {
            case "MainView":
                mainBoundary = fxmlLoader.getController();
                EventBus.getDefault().register(mainBoundary);
                break;

            default:
                break;
        }
    }

    public static Parent loadFXML(String fxml) throws IOException {
        String fxmlPath = ConstantsPath.USER_PACKAGE + fxml + ".fxml";
        URL fxmlUrl = SimpleChatClient.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            System.err.println("FXML file not found at: " + fxmlPath);
            throw new IOException("FXML file not found: " + fxmlPath);
        }
        System.out.println("Loading FXML from path: " + fxmlUrl);
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        return fxmlLoader.load();
    }

    @Override
    public void stop() throws Exception {
        // Unregister from EventBus
        EventBus.getDefault().unregister(this);
        if (mainBoundary != null) {
            EventBus.getDefault().unregister(mainBoundary);
        }

        //// Log out the user if logged in
        //if (SimpleClient.user != null && !SimpleClient.user.isEmpty()) {
        //    LoginPageController.requestUserLogOut(SimpleClient.user);
        //    LoginPageController.requestEmployeeLogOut(SimpleClient.user); // Consider improving this logic
        //}

        // Close the client connection
        if (SimpleChatClient.client != null) {

            // Send a request to delete the connection
            ConnectionMessage message = new ConnectionMessage(Message.MessageType.REQUEST, ConnectionMessage.RequestType.DELETE_CONNECTION);
            SimpleClient.getClient().sendRequest(message);

            try {
                SimpleChatClient.client.closeConnection();
                System.out.println("Client connection closed successfully.");
            } catch (IOException e) {
                System.err.println("Failed to close client connection: " + e.getMessage());
            }
        }

        System.out.println("Application stopped successfully.");

        super.stop();
    }


    @Subscribe
    public void onMessageEvent(Object event) {
        System.out.println("Received Message in Client");

    }

    public void sendRequest(String action) {

    }

    public static SimpleClient getClient() {
        return client;
    }





    public static void main(String[] args) {
        launch();
    }


}