package il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts;

import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Objects;

public class AlertsBuilder {

    private static String title;
    private static String buttonStyle;
    private static String titleStyle;
    private static String bodyStyle;
    private static String borderStyle;
    private static ImageView icon;

    public static void create(AlertType type, StackPane dialogContainer, Node nodeToBlur, Node nodeToDisable, String body) {
        create(type, dialogContainer, nodeToBlur, nodeToDisable, body, "Okay", null, null, null);
    }

    public static void create(AlertType type, StackPane dialogContainer, Node nodeToBlur, Node nodeToDisable, String body, String primaryButtonText, Runnable primaryAction, String secondaryButtonText, Runnable secondaryAction) {
        Platform.runLater(() -> {
            setFunction(type);
            Stage alertStage = new Stage();
            alertStage.initStyle(StageStyle.UNDECORATED);
            alertStage.initModality(Modality.APPLICATION_MODAL);

            VBox content = new VBox();
            content.setAlignment(Pos.TOP_CENTER);
            content.setSpacing(20);
            content.setPadding(new Insets(20));
            content.getStyleClass().add("alert-container");
            content.getStyleClass().add(borderStyle);

            HBox titleContainer = new HBox();
            titleContainer.setAlignment(Pos.CENTER); // Center title horizontally
            titleContainer.setSpacing(10);
            if (icon != null) {
                titleContainer.getChildren().add(icon);
            }
            Text textTitle = new Text(title);
            textTitle.getStyleClass().add(titleStyle);
            titleContainer.getChildren().add(textTitle);

            Text textBody = new Text(body);
            textBody.getStyleClass().add(bodyStyle);
            TextFlow textFlow = new TextFlow(textBody);
            textFlow.setTextAlignment(TextAlignment.CENTER); // Center text within the TextFlow
            textFlow.setPrefWidth(350);

            HBox buttonContainer = new HBox(20);
            buttonContainer.setAlignment(Pos.CENTER); // Center buttons horizontally

            Button primaryButton = createButton(primaryButtonText, primaryAction, alertStage);
            buttonContainer.getChildren().add(primaryButton);

            if (secondaryButtonText != null) {
                Button secondaryButton = createButton(secondaryButtonText, secondaryAction, alertStage);
                buttonContainer.getChildren().add(secondaryButton);
            }

            content.getChildren().addAll(titleContainer, textFlow, buttonContainer);

            StackPane root = new StackPane(content);
            root.getStyleClass().add("alert-container");
            Scene scene = new Scene(root);
            scene.setFill(Color.WHITE);
            scene.getStylesheets().add(Objects.requireNonNull(AlertsBuilder.class.getResource(ConstantsPath.CSS_LIGHT_THEME)).toExternalForm());
            alertStage.setScene(scene);


            Window parentWindow = dialogContainer.getScene().getWindow();
            alertStage.setOnShown(event -> {
                alertStage.setX(parentWindow.getX() + parentWindow.getWidth() / 2 - alertStage.getWidth() / 2);
                alertStage.setY(parentWindow.getY() + 200);
            });

            applyBlurEffect(nodeToBlur);
            nodeToDisable.setDisable(true);

            alertStage.show();

            alertStage.setOnHidden(e -> {
                nodeToDisable.setDisable(false);
                removeBlurEffect(nodeToBlur);
            });

            root.setOnMouseClicked(e -> alertStage.close());
            content.setOnMouseClicked(Event::consume);
        });
    }

    private static Button createButton(String text, Runnable action, Stage stage) {
        Button button = new Button(text);
        button.getStyleClass().add(buttonStyle);
        button.setPrefWidth(180);
        button.setOnAction(e -> {
            if (action != null) {
                action.run();
            }
            stage.close();
        });
        return button;
    }

    private static void applyBlurEffect(Node node) {
        node.setEffect(ConstantsPath.BOX_BLUR_EFFECT);
    }

    private static void removeBlurEffect(Node node) {
        node.setEffect(null);
    }

    private static void setFunction(AlertType type) {
        switch (type) {
            case SUCCESS:
                title = "Success";
                buttonStyle = "alert-success-button";
                titleStyle = "alert-success-title";
                bodyStyle = "alert-success-body";
                borderStyle = "alert-success-border";
                icon = new ImageView(new Image(Objects.requireNonNull(AlertsBuilder.class.getResourceAsStream(ConstantsPath.GENERAL_PACKAGE + "success.png"))));
                break;
            case ERROR:
                title = "Oops";
                buttonStyle = "alert-error-button";
                titleStyle = "alert-error-title";
                bodyStyle = "alert-error-body";
                borderStyle = "alert-error-border";
                icon = new ImageView(new Image(Objects.requireNonNull(AlertsBuilder.class.getResourceAsStream(ConstantsPath.GENERAL_PACKAGE + "error.png"))));
                break;
            case WARNING:
                title = "Warning";
                buttonStyle = "alert-warning-button";
                titleStyle = "alert-warning-title";
                bodyStyle = "alert-warning-body";
                borderStyle = "alert-warning-border";
                icon = new ImageView(new Image(Objects.requireNonNull(AlertsBuilder.class.getResourceAsStream(ConstantsPath.GENERAL_PACKAGE + "warning.png"))));
                break;
            case INFO:
                title = "Information";
                buttonStyle = "alert-information-button";
                titleStyle = "alert-information-title";
                bodyStyle = "alert-information-body";
                borderStyle = "alert-information-border";
                icon = new ImageView(new Image(Objects.requireNonNull(AlertsBuilder.class.getResourceAsStream(ConstantsPath.GENERAL_PACKAGE + "information.png"))));
                break;
            case CANCELLATION:
                title = "Update";
                buttonStyle = "alert-cancel-button";
                titleStyle = "alert-cancel-title";
                bodyStyle = "alert-cancel-body";
                borderStyle = "alert-cancel-border";
                icon = new ImageView(new Image(Objects.requireNonNull(AlertsBuilder.class.getResourceAsStream(ConstantsPath.GENERAL_PACKAGE + "update.png"))));
                break;
        }
        if (icon != null) {
            icon.getStyleClass().add("alert-icon");
        }
    }
}
