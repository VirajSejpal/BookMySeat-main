package il.cshaifasweng.OCSFMediatorExample.client.boundaries.registeredUser;

import il.cshaifasweng.OCSFMediatorExample.client.boundaries.user.MainBoundary;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.PurchaseController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.RegisteredUserController;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.ButtonFactory;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.*;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ComplaintMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.PurchaseMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.RegisteredUserMessage;
import il.cshaifasweng.OCSFMediatorExample.server.events.HomeViewingEvent;
import il.cshaifasweng.OCSFMediatorExample.server.events.MovieInstanceCanceledEvent;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.DialogTool;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.CustomContextMenu;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class OrdersBoundary implements Initializable {


    @FXML
    private AnchorPane OrderContainer;

    @FXML
    private TableColumn<Purchase, Integer> colId;

    @FXML
    private TableColumn<Purchase, Node> colStatus;

    @FXML
    private TableColumn<Purchase, Button> colTypePurchase;

    @FXML
    private TableColumn<Purchase, String> colPurchaseDate,colName;

    @FXML
    private Text ticketCounterT;
    @FXML
    private AnchorPane deleteUserContainer;

    @FXML
    private HBox hboxSearch;

    @FXML
    private AnchorPane rootUsers;

    @FXML
    private StackPane stckUsers;

    @FXML
    private TableView<Purchase> tblOrders;

    @FXML
    private Text txtRefund;

    @FXML
    private Button btnDelete;

    private DialogTool dialogMyOrder;
    private DialogTool dialogDelete;
    private ObservableList<Purchase> listOrders;
    private String id;
    private double refundPrice;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listOrders = FXCollections.observableArrayList();
        EventBus.getDefault().register(this);
        setId(MainBoundary.getId());
        PurchaseController.GetPurchasesByCustomerID(id);
        RegisteredUserController.getUserByID(String.valueOf(id));
        animateNodes();
        setContextMenu();
        tblOrders.setRowFactory(tv -> {
            TableRow<Purchase> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Purchase rowData = row.getItem();
                    showDialogOrder();
                }
            });
            return row;
        });
    }

    public void setId(String id) {
        this.id = id;
    }

    @Subscribe
    public void onRegisteredUserReceivedMessage(RegisteredUserMessage message)
    {
        Platform.runLater(() -> {
            ticketCounterT.setText("Multi-Entry-Ticket amount: " + message.registeredUser.getTicket_counter());
        });
    }

    @Subscribe
    public void onMovieInstanceCanceledEvent(MovieInstanceCanceledEvent message)
    {
        for (Purchase p : listOrders)
        {
            if(p instanceof MovieTicket && ((MovieTicket) p).getMovieInstance().getId() == message.movieInstance.getId()) {
                PurchaseController.GetPurchasesByCustomerID(id);
            }
        }
    }

    @Subscribe
    public void onHomeViewingEvent(HomeViewingEvent homeViewingEvent)
    {
        System.out.println("Home Viewing Event received");
       if(String.valueOf(homeViewingEvent.id).equals(id))
           PurchaseController.GetPurchasesByCustomerID(id);
    }

    @Subscribe
    public void onPurchaseMessageReceived(PurchaseMessage message)
    {
        System.out.println(message.responseType);
        Platform.runLater(() -> {
        if (message.responseType == PurchaseMessage.ResponseType.PURCHASES_LIST)
        {
            loadTableData(message.purchases) ;
        }
        else if (message.responseType == PurchaseMessage.ResponseType.PURCHASE_REMOVED)
        {
            System.out.println("Purchase removed, refreshing purchases for customer ID: " + id);
            PurchaseController.GetPurchasesByCustomerID(id);
        }

        else if (message.responseType == PurchaseMessage.ResponseType.PURCHASE_FAILED)
        {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "Cannot Cancel purchase selected!");
        }
        else {
            PurchaseController.GetPurchasesByCustomerID(id);
        }});
    }


    @Subscribe
    public void onComplainMessageReceived(ComplaintMessage message) {
        System.out.println(message.responseType);
        Platform.runLater(() -> {  if (message.responseType == ComplaintMessage.ResponseType.COMPLIANT_ADDED) {
            AlertsBuilder.create(AlertType.SUCCESS, stckUsers, rootUsers, tblOrders, "Complaint added!");
        }else if (message.responseType == ComplaintMessage.ResponseType.COMPLIANT_MESSAGE_FAILED) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "Complaint message failed!");
        }
        });
    }


    private void loadTableData(List<Purchase> purchases) {
        Platform.runLater(() -> {
            listOrders = FXCollections.observableArrayList(purchases);
            tblOrders.setItems(listOrders);
            tblOrders.setFixedCellSize(30);
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));
            colName.setCellValueFactory(param -> {
                String s ="";
                if(param.getValue() instanceof MultiEntryTicket)
                {
                    s = "20 Movie Tickets";
                }
                else if(param.getValue() instanceof MovieTicket)
                {
                    s = ((MovieTicket) param.getValue()).getMovieInstance().getMovie().getEnglishName();
                }
                else if(param.getValue() instanceof HomeViewingPackageInstance)
                {
                    s = ((HomeViewingPackageInstance) param.getValue()).getMovie().getEnglishName();
                }

                return new SimpleObjectProperty<>(s);

            });
            colPurchaseDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPurchaseDate().toLocalDate().toString()));
            colTypePurchase.setCellValueFactory(new ButtonFactory.ButtonTypePurchaseCellValueFactory());
            colStatus.setCellValueFactory(new ButtonFactory.ButtonStatusTicketCellValueFactory());
        });

    }

    public StackPane getStckUsers(){return stckUsers;}

    private void setContextMenu() {
        CustomContextMenu contextMenu = new CustomContextMenu(tblOrders,2);
        contextMenu.setActionDetails(ev -> {
            showDialogOrder();
            contextMenu.hide();
        });

        contextMenu.setActionEdit(ev -> {
            showDialogComplain();
            contextMenu.hide();
        });

        contextMenu.setActionDelete(ev -> {
            showDialogCancel();
            contextMenu.hide();
        });

        contextMenu.show();
    }

    private void animateNodes() {
        Animations.fadeInUp(tblOrders);
        Animations.fadeInUp(hboxSearch);
    }

    @FXML
    private void showDialogOrder() {
        disableTable();
        Purchase selectedPurchase = tblOrders.getSelectionModel().getSelectedItem();

        if (selectedPurchase == null) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "No purchase selected");
            tblOrders.setDisable(false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.DIALOG_TICKET_VIEW));
            AnchorPane ticketPane = loader.load();
            DialogTicket dialogTicket = loader.getController();
            dialogTicket.setOrdersController(this);
            dialogTicket.setTicketInfo(selectedPurchase);
            OrderContainer.getChildren().clear();
            OrderContainer.getChildren().add(ticketPane);
            OrderContainer.setVisible(true);
            dialogMyOrder = new DialogTool(OrderContainer, stckUsers);
            dialogMyOrder.show();
            dialogMyOrder.setOnDialogClosed(ev -> {
                tblOrders.setDisable(false);
                rootUsers.setEffect(null);
                OrderContainer.setVisible(false);
            });
            rootUsers.setEffect(ConstantsPath.BOX_BLUR_EFFECT);
        } catch (IOException e) {
            e.printStackTrace();
            tblOrders.setDisable(false);
        }
    }

    @FXML
    private void showDialogComplain() {
        disableTable();
        Purchase selectedPurchase = tblOrders.getSelectionModel().getSelectedItem();
        if (selectedPurchase == null) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "No purchase selected");
            tblOrders.setDisable(false);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.DIALOG_COMPLAINT_VIEW));
            AnchorPane ticketPane = loader.load();
            DialogComplaint dialogComplaint = loader.getController();
            dialogComplaint.setOrdersController(this);
            dialogComplaint.setPurchase(selectedPurchase);
            OrderContainer.getChildren().clear();
            OrderContainer.getChildren().add(ticketPane);
            OrderContainer.setVisible(true);
            dialogMyOrder = new DialogTool(OrderContainer, stckUsers);
            dialogMyOrder.show();
            dialogMyOrder.setOnDialogClosed(ev -> {
                tblOrders.setDisable(false);
                rootUsers.setEffect(null);
                OrderContainer.setVisible(false);
            });
            rootUsers.setEffect(ConstantsPath.BOX_BLUR_EFFECT);
        } catch (IOException e) {
            e.printStackTrace();
            tblOrders.setDisable(false);
        }
    }

    @FXML
    public void closeDialog() {
        if (dialogMyOrder != null) {
            dialogMyOrder.close();
        }
    }

    @FXML
    public void closeDialogDelete() {
        if (dialogDelete != null) {
            dialogDelete.close();
        }
    }

    private void disableTable() {
        tblOrders.setDisable(true);
    }

    @FXML
    private void showDialogCancel() {
        Purchase delete = tblOrders.getSelectionModel().getSelectedItem();
        if (delete == null) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, ConstantsPath.MESSAGE_NO_RECORD_SELECTED);
            return;
        }
        if (delete instanceof  MultiEntryTicket) {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "You cannot cancel multi-entry tickets!");
            return;
        }
        if(!delete.getIsActive())
        {
            AlertsBuilder.create(AlertType.ERROR, stckUsers, rootUsers, tblOrders, "You cannot cancel a purchase that is not active!");
            return;
        }
        txtRefund.setText(processRefund(delete));
        Platform.runLater(() -> {
            rootUsers.setEffect(ConstantsPath.BOX_BLUR_EFFECT);
            deleteUserContainer.setVisible(true);
            disableTable();
            dialogDelete = new DialogTool(deleteUserContainer, stckUsers);
            btnDelete.setOnAction(ev -> {
                handleRefund(delete);
                PurchaseController.RemovePurchase(delete);
                dialogDelete.close();
            });
            dialogDelete.show();
            dialogDelete.setOnDialogClosed(ev -> {
                tblOrders.setDisable(false);
                rootUsers.setEffect(null);
                deleteUserContainer.setVisible(false);
                txtRefund.setText("");
            });
        });
    }
    @FXML
    private void handleRefund(Purchase purchase) {
        if(refundPrice==0)
            AlertsBuilder.create(AlertType.SUCCESS, stckUsers, rootUsers, tblOrders, "You have not been refunded.");
        else
            AlertsBuilder.create(AlertType.SUCCESS, stckUsers, rootUsers, tblOrders, "You have been refunded for: " + refundPrice);

    }

    private String processRefund(Purchase purchase) {
        if (purchase instanceof MovieTicket) {
            MovieTicket movieTicket = (MovieTicket) purchase;
            refundPrice = movieTicket.getPricePaid();
            LocalDateTime screeningTime = movieTicket.getMovieInstance().getTime();
            long hoursUntilScreening = ChronoUnit.HOURS.between(LocalDateTime.now(), screeningTime.minusHours(3));
            System.out.println("hoursUntilScreening = "+ hoursUntilScreening+ "Screening time = " +screeningTime.minusHours(3));
            if (hoursUntilScreening > 3) {
                return "You will refund Full Price Ticket: " + refundPrice + "₹";
            } else if (hoursUntilScreening > 1) {
                refundPrice *= 0.5;
                return "You will refund 50% of the Price Ticket: " + refundPrice + "₹";
            } else {
                refundPrice = 0;
                return "You won't be refunded!";
            }
        } else {
            HomeViewingPackageInstance homeViewingPackageInstance = (HomeViewingPackageInstance) purchase;
            long hoursUntilScreening = ChronoUnit.HOURS.between(LocalDateTime.now(), homeViewingPackageInstance.getActivationDate().minusHours(3));
            if (hoursUntilScreening > 1) {
                refundPrice = homeViewingPackageInstance.getPricePaid() * 0.5;
                return "You will refund 50% of the Price Ticket: " + refundPrice + "₹";
            }
            else {
                refundPrice = 0;
                return "You won't be refunded!";
            }
        }
    }

    public void cleanup() {
        EventBus.getDefault().unregister(this);
    }
}