package il.cshaifasweng.OCSFMediatorExample.client.boundaries.customerService;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.ComplaintController;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.notifications.NotificationsBuilder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DialogCustomerService implements Initializable {

    @FXML
    private Label numTicketsLabel;

    @FXML
    private Label complaintDetailsLabel;

    @FXML
    private Label movieNameLabel;

    @FXML
    private Label viewingDateLabel;

    @FXML
    private Label complainantNameLabel;

    @FXML
    private ComboBox<String> actionComboBox;

    @FXML
    private VBox compensateFinanciallyBox;

    @FXML
    private ComboBox<String> employeeResponseComboBox;

    @FXML
    private TextFlow finalResponsePreviewArea;

    @FXML
    private ScrollPane finalResponseScrollPane;

    @FXML
    private AnchorPane pnEmployeeArea;

    @FXML
    private Label purchaseTypeField;

    @FXML
    private TextField refundAmountField;

    @FXML
    private Button btnSend;

    private CustomerServiceBoundary customerServiceController;

    private MovieTicket selectedMovieTicket;
    private MovieInstance selectedMovieInstance;
    private HomeViewingPackageInstance homeViewingPackageInstance;
    private Complaint myComplaint;
    private int price;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        compensateFinanciallyBox.setVisible(false);
        compensateFinanciallyBox.setManaged(false);

        employeeResponseComboBox.setDisable(true);

        actionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> handleActionSelection());
        employeeResponseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> checkIfCanSend());

        refundAmountField.textProperty().addListener((observable, oldValue, newValue) -> updateFinalResponsePreview());

        btnSend.setDisable(true);
    }
    public void setCustomerServiceController(CustomerServiceBoundary customerServiceController) {
        this.customerServiceController = customerServiceController;
    }

    public void setComplaint(Complaint selectedComplaint) {
        this.myComplaint = selectedComplaint;

        if (selectedComplaint != null) {
            if(selectedComplaint.getRegisteredUser() != null)
            {   complaintDetailsLabel.setText(selectedComplaint.getInfo() == null ? "" : selectedComplaint.getInfo());
                complainantNameLabel.setText(selectedComplaint.getRegisteredUser().getName() == null ? "" : selectedComplaint.getRegisteredUser().getName());

                if (selectedComplaint.getPurchase() instanceof MovieTicket) {
                    setMovieTicket(selectedComplaint);
                } else if (selectedComplaint.getPurchase() instanceof MultiEntryTicket) {
                    setMultiEntryTicket(selectedComplaint);
                } else if (selectedComplaint.getPurchase() instanceof HomeViewingPackageInstance) {
                    setHomeViewing(selectedComplaint);
                }
            }
            else  {complaintDetailsLabel.setText(selectedComplaint.getInfo() == null ? "" : selectedComplaint.getInfo());
                complainantNameLabel.setText(""); }
        }}


    private void setMovieTicket(Complaint selectedComplaint) {
        this.selectedMovieInstance = ((MovieTicket) selectedComplaint.getPurchase()).getMovieInstance();
        this.selectedMovieTicket = (MovieTicket) selectedComplaint.getPurchase();
        this.price = selectedMovieTicket.getMovieInstance().getMovie().getTheaterPrice();

        movieNameLabel.setText(selectedMovieInstance.getMovie().getEnglishName());
        viewingDateLabel.setText(selectedMovieTicket.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        numTicketsLabel.setText("1");
        purchaseTypeField.setText("Movie Ticket");
    }

    private void setHomeViewing(Complaint selectedComplaint) {
        this.homeViewingPackageInstance = (HomeViewingPackageInstance) selectedComplaint.getPurchase();
        this.price = homeViewingPackageInstance.getMovie().getHomeViewingPrice();

        movieNameLabel.setText(homeViewingPackageInstance.getMovie().getEnglishName());
        viewingDateLabel.setText(homeViewingPackageInstance.getPurchaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        numTicketsLabel.setText("1");
        purchaseTypeField.setText("Home Viewing Ticket");
    }

    private void setMultiEntryTicket(Complaint selectedComplaint) {
        purchaseTypeField.setText("Multi-Entry Ticket");
        numTicketsLabel.setText(String.valueOf(selectedComplaint.getPurchase().getOwner().getTicket_counter()));
    }

    @FXML
    public void handleActionSelection() {
        String selectedAction = actionComboBox.getValue();

        compensateFinanciallyBox.setVisible(false);
        compensateFinanciallyBox.setManaged(false);

        if (selectedAction != null && (selectedAction.equals("Compensate Financially"))) {
            compensateFinanciallyBox.setVisible(true);
            compensateFinanciallyBox.setManaged(true);
        }


        employeeResponseComboBox.setDisable(selectedAction == null);

        updateFinalResponsePreview();

        checkIfCanSend();
    }

    private void checkIfCanSend() {
        boolean canSend = actionComboBox.getValue() != null && employeeResponseComboBox.getValue() != null;
        btnSend.setDisable(!canSend);

        updateFinalResponsePreview();
    }

    private void updateFinalResponsePreview() {
        finalResponsePreviewArea.getChildren().clear();

        if (employeeResponseComboBox.getValue() != null) {
            finalResponsePreviewArea.getChildren().add(new javafx.scene.text.Text(  employeeResponseComboBox.getValue() + "\n"));
        }

        String selectedAction = actionComboBox.getValue();
        if (selectedAction != null && selectedAction.equals("Compensate Financially")) {
            finalResponsePreviewArea.getChildren().add(new javafx.scene.text.Text("\nAmount: " + refundAmountField.getText() + "₹\n"));
        }

    }

    @FXML
    public void handleSubmitFinalResponse() {
        if (btnSend.isDisable()) {
            NotificationsBuilder.create(NotificationType.ERROR, "You must select an action and a response before sending.", pnEmployeeArea);
            return;
        }

        String selectedAction = actionComboBox.getValue();
        if (selectedAction != null && selectedAction.equals("Compensate Financially")) {
            String compensationAmount = refundAmountField.getText().trim();
            if (compensationAmount.isEmpty()) {
                NotificationsBuilder.create(NotificationType.ERROR, "Compensation amount cannot be empty.", pnEmployeeArea);
                return;
            }
            try {
                double amount = Double.parseDouble(compensationAmount);
                if (amount <= 0) {
                    NotificationsBuilder.create(NotificationType.ERROR, "Compensation amount must be a positive number.", pnEmployeeArea);
                    return;
                }
            } catch (NumberFormatException e) {
                NotificationsBuilder.create(NotificationType.ERROR, "Invalid compensation amount. Please enter a valid number.", pnEmployeeArea);
                return;
            }
        }

        String finalResponseText = getFinalResponseText();

        customerServiceController.customerServiceAnswer = finalResponseText;
        myComplaint.setInfo(myComplaint.getInfo() + "\nCustomer Service answer: " + finalResponseText);
        customerServiceController.complaintId = (myComplaint.getId());
        customerServiceController.complaint = myComplaint;
        ComplaintController.answerComplaint(myComplaint);
        customerServiceController.closeDialogAddQuotes();
    }

    private String getFinalResponseText() {
        StringBuilder finalResponse = new StringBuilder();
        finalResponsePreviewArea.getChildren().forEach(node -> {
            if (node instanceof javafx.scene.text.Text) {
                finalResponse.append(((javafx.scene.text.Text) node).getText());
            }
        });
        return finalResponse.toString();
    }

    @FXML
    public void closeEmployeeArea(ActionEvent actionEvent) {
        customerServiceController.closeDialogAddQuotes();
    }
}
