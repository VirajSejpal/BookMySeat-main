package il.cshaifasweng.OCSFMediatorExample.client.boundaries.registeredUser;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.ComplaintController;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class DialogComplaint implements Initializable  {

    @FXML
    private TextArea additionalCommentsArea;


    @FXML
    private AnchorPane pnCustomerArea;

    @FXML
    private Button btnClose,btnSend;

    @FXML
    private TextField purchaseTypeField;

    private Purchase myPurchase;


    private OrdersBoundary ordersBoundary;

    public void initialize(URL location, ResourceBundle resources){
    }

    @FXML
    public void handleSubmitComplaint() {
        // Collect and process the complaint data
        String additionalComments = additionalCommentsArea.getText();
        if (additionalComments.length() > 0) {
            // Code to submit the complaint to the system goes here
            LocalDateTime creationDate = LocalDateTime.now();
            ComplaintController.addComplaintRegister( additionalComments,  creationDate,  myPurchase,  false,  myPurchase.getOwner());
            additionalCommentsArea.clear();
            closeDialog();

        }
        else showErrorAndFocus(additionalCommentsArea);

    }

    public void closeCustomerArea(ActionEvent actionEvent) {
        closeDialog();
    }

    private void closeDialog() {
        ordersBoundary.closeDialog();


    }
    private void showErrorAndFocus(Control field) {
        field.requestFocus();
        Animations.shake(field);
    }


    public void setOrdersController(OrdersBoundary ordersBoundary) {
        this.ordersBoundary = ordersBoundary;
    }

    public void setPurchase(Purchase selectedPurchase) {

        myPurchase = selectedPurchase;
        if (myPurchase instanceof MovieTicket)
            purchaseTypeField.setText(((MovieTicket) myPurchase).getMovieInstance().getMovie().getEnglishName());
        else if (myPurchase instanceof HomeViewingPackageInstance)
            purchaseTypeField.setText(((HomeViewingPackageInstance) myPurchase).getMovie().getEnglishName());
        else if (myPurchase instanceof MultiEntryTicket)
            purchaseTypeField.setText("Multi Entry");
    }
}
