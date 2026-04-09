package il.cshaifasweng.OCSFMediatorExample.client.util.popUp;

import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos; // Import this for positioning

import java.util.Objects;

public class DialogTool {

    private final Stage dialogStage;
    private final StackPane root;

    public DialogTool(Region region, StackPane container) {
        dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initStyle(StageStyle.TRANSPARENT);

        // Ensure the region (VBox or any other region) is centered within the StackPane
        root = new StackPane(region);
        root.setAlignment(Pos.TOP_CENTER); // Explicitly center the content
        root.setStyle("-fx-background-color: transparent; -fx-padding: 0;"); // Transparent background and no padding

        // Create the scene with transparent fill
        Scene scene = new Scene(root);
        scene.setFill(null); // Transparent background for the scene

        // Add the stylesheet if necessary
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(ConstantsPath.CSS_LIGHT_THEME)).toExternalForm()); // Ensure this path is correct
        dialogStage.setScene(scene);
    }

    public void setOnDialogOpened(EventHandler<WindowEvent> action) {
        dialogStage.setOnShown(action);
    }

    public void setOnDialogClosed(EventHandler<WindowEvent> action) {
        dialogStage.setOnHidden(action);
    }

    public void show() {
        dialogStage.show();
    }

    public void close() {
         dialogStage.close();
    }
}
