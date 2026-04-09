package il.cshaifasweng.OCSFMediatorExample.client.boundaries.customerService;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.ComplaintController;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.DialogTool;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.client.util.ButtonFactory;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ComplaintMessage;
import il.cshaifasweng.OCSFMediatorExample.server.events.ComplaintEvent;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CustomerServiceBoundary implements Initializable {

    private ObservableList<Complaint> listComplaints;
    private DialogTool dialogAddProduct;

    @FXML
    private TableColumn<Complaint, Integer> colId;
    @FXML
    private TableColumn<Complaint, String> colCustomerName;
    @FXML
    private TableColumn<Complaint, Button> colPurchase;
    @FXML
    private TableColumn<Complaint, String> colDate;
    @FXML
    private TableColumn<Complaint, String> colDescription;
    @FXML
    private TableColumn<Complaint, Button> colStatus;
    @FXML
    private AnchorPane containerDeleteComplaint;
    @FXML
    private AnchorPane containerHandleComplaint;
    @FXML
    private AnchorPane rootCustomerService;
    @FXML
    private HBox rootSearch;
    @FXML
    private StackPane stckCustomerService;
    @FXML
    private TableView<Complaint> tblComplaints;

    public String customerServiceAnswer;
    public int complaintId;
    public Complaint complaint;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getDefault().register(this);
        ComplaintController.getOpenComplaints();

        animateNodes();
        listComplaints = FXCollections.observableArrayList();
        tblComplaints.setRowFactory(tv -> {
            TableRow<Complaint> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Complaint rowData = row.getItem();
                    showDialog();
                }
            });
            return row;
        });
    }

    private void animateNodes() {
        Animations.fadeInUp(rootSearch);
        Animations.fadeInUp(tblComplaints);
    }

    @Subscribe
    public void onComplaintMessageReceived(ComplaintMessage message) {
        Platform.runLater(() -> {
            switch (message.responseType) {
                case FILTERED_COMPLAINTS_LIST:
                    loadTableData(message.compliants);
                    break; // Add break to avoid fall-through
                case COMPLIANT_ADDED:
                    ComplaintController.getOpenComplaints();
                    break;
                case COMPLIANT_WAS_ANSWERED:
                    AlertsBuilder.create(AlertType.SUCCESS, stckCustomerService, rootCustomerService, rootCustomerService, "Complaint Handled!");
                    ComplaintController.getOpenComplaints();
                    break;
                case COMPLIANT_MESSAGE_FAILED:
                    AlertsBuilder.create(AlertType.ERROR, stckCustomerService, rootCustomerService, rootCustomerService, "Complaint wasn't Handled!");
                    break;
            }
        });
    }


    @Subscribe
    public void onComplaintEventReceived(ComplaintEvent complaintEvent) {
        Platform.runLater(() -> {
            AlertsBuilder.create(AlertType.SUCCESS, stckCustomerService, rootCustomerService, rootCustomerService, "New Complaint Added!");
            ComplaintController.getOpenComplaints();
        });
    }

    private void loadTableData(List<Complaint> complaints) {
        if (complaints == null || complaints.isEmpty()) {
            listComplaints.clear();
        }

        complaints.sort((c1, c2) -> {
            long hoursLeft1 = 24 - java.time.Duration.between(c1.getCreationDate(), java.time.LocalDateTime.now()).toHours();
            long hoursLeft2 = 24 - java.time.Duration.between(c2.getCreationDate(), java.time.LocalDateTime.now()).toHours();
            return Long.compare(hoursLeft1, hoursLeft2);
        });

        listComplaints = FXCollections.observableArrayList(complaints);
        tblComplaints.setItems(listComplaints);
        tblComplaints.setFixedCellSize(30);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("info"));
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getCreationDate() != null) {
                return new SimpleObjectProperty<>(
                        cellData.getValue().getCreationDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            } else {
                return new SimpleObjectProperty<>("");
            }
        });

        colCustomerName.setCellValueFactory(cellData -> {
            String customerName = (cellData.getValue().getRegisteredUser() != null) ?
                    cellData.getValue().getRegisteredUser().getName() : "General Complaint";
            return new SimpleObjectProperty<>(customerName);
        });

        colPurchase.setCellValueFactory(new ButtonFactory.ButtonTypeOrderCellValueFactory());
        colStatus.setCellFactory(new ButtonFactory.ButtonUrgencyCellFactory());
    }

    @FXML
    private void showDialog() {
        Platform.runLater(() -> {
            tblComplaints.setDisable(true);
            Complaint selectedComplaint = tblComplaints.getSelectionModel().getSelectedItem();
            if (selectedComplaint == null) {
                AlertsBuilder.create(AlertType.ERROR, stckCustomerService, rootCustomerService, tblComplaints, "No complaint selected");
                tblComplaints.setDisable(false);
                return;
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.DIALOG_CUSTOMER_SERVICE_VIEW));
                AnchorPane ticketPane = loader.load();

                DialogCustomerService dialogCustomerService = loader.getController();
                dialogCustomerService.setCustomerServiceController(this);
                this.complaint = selectedComplaint;
                this.complaintId = complaint.getId();
                dialogCustomerService.setComplaint(selectedComplaint);

                containerHandleComplaint.getChildren().clear();
                containerHandleComplaint.getChildren().add(ticketPane);
                containerHandleComplaint.setVisible(true);

                dialogAddProduct = new DialogTool(containerHandleComplaint, stckCustomerService);
                dialogAddProduct.show();

                dialogAddProduct.setOnDialogClosed(ev -> {
                    tblComplaints.setDisable(false);
                    rootCustomerService.setEffect(null);
                    containerHandleComplaint.setVisible(false);
                });

                rootCustomerService.setEffect(ConstantsPath.BOX_BLUR_EFFECT);

            } catch (IOException e) {
                e.printStackTrace();
                tblComplaints.setDisable(false);
            }
        });
    }

    @FXML
    public void closeDialogAddQuotes() {
        if (dialogAddProduct != null) {
            dialogAddProduct.close();
        }
    }
}
