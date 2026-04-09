package il.cshaifasweng.OCSFMediatorExample.client.util.popUp;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class CustomContextMenu {

    private final ContextMenu contextMenu;
    private final Node node;
    private MenuItem edit;
    private MenuItem delete;
    private MenuItem details;


    public CustomContextMenu(Node node,int num) {
        this.node = node;
        contextMenu = new ContextMenu();
        contextMenu.getItems().addAll(getContent(num));

        // Load the CSS file
        contextMenu.getStyleClass().add("custom-context-menu");

    }

    public void setActionEdit(EventHandler<ActionEvent> action) {
        edit.setOnAction(action);
    }

    public void setActionDelete(EventHandler<ActionEvent> action) {
        delete.setOnAction(action);
    }

    public void setActionDetails(EventHandler<ActionEvent> action) {
        details.setOnAction(action);
    }

    public void show() {
        node.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            if (ev.getButton().equals(MouseButton.SECONDARY)) {
                contextMenu.show(node, ev.getScreenX(), ev.getScreenY());
            }
        });
    }

    public void hide() {
        contextMenu.hide();
    }

    public MenuItem getEditButton() {
        return edit;
    }

    public MenuItem getDeleteButton() {
        return delete;
    }

    public MenuItem getDetailsButton() {
        return details;
    }

    public void setEditText(String text) {
        edit.setText(text);
    }

    public void setDeleteText(String text) {
        delete.setText(text);
    }

    public void setDetailsText(String text) {
        details.setText(text);
    }

    private MenuItem[] getContent(int num) {
        switch (num)
        {
            case 1:
                edit = new MenuItem("Edit Movie");
                delete = new MenuItem("Cancel Movie");
                style(edit);
                style(delete);
                return new MenuItem[]{edit, delete};
            case 2:
                edit = new MenuItem("Submit a complaint");
                delete = new MenuItem("Cancel Purchase");
                details = new MenuItem("Details");
                style(edit);
                style(delete);
                style(details);
                return new MenuItem[]{edit, delete, details};
            case 3:
                edit = new MenuItem("Edit Screening");
                delete = new MenuItem("Cancel Screening");
                style(edit);
                style(delete);
                return new MenuItem[]{edit, delete};
        }
        return null;
    }

    private void style(MenuItem menuItem) {
        menuItem.getStyleClass().add("custom-context-item");
    }
}