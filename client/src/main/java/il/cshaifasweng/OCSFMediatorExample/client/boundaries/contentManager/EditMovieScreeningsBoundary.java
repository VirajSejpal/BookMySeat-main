package il.cshaifasweng.OCSFMediatorExample.client.boundaries.contentManager;

import il.cshaifasweng.OCSFMediatorExample.client.controllers.MovieInstanceController;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertType;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.alerts.AlertsBuilder;
import il.cshaifasweng.OCSFMediatorExample.client.util.assets.Animations;
import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.CustomContextMenu;
import il.cshaifasweng.OCSFMediatorExample.client.util.popUp.DialogTool;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.MovieInstanceMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.MovieInstance;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class EditMovieScreeningsBoundary implements Initializable {

    private final ColorAdjust colorAdjust = new ColorAdjust();
    private ObservableList<MovieInstance> listTheater;
    private ObservableList<MovieInstance> filterProducts;
    private DialogTool dialogEdit;
    private DialogTool dialogDelete;
    private static final Stage stage = new Stage();
    private CustomContextMenu contextMenu;

    @FXML
    private TableColumn<MovieInstance, String> colDate, colEnglish, colHall, colHebrew, colHour, colTheater, colActive;

    @FXML
    private TableColumn<MovieInstance, Integer> colId;

    @FXML
    private AnchorPane containerEdit, containerDelete, rootProducts;

    @FXML
    private HBox hBoxSearch;

    @FXML
    private Button btnNewScrenning;

    @FXML
    private Button deleteButton;

    @FXML
    private StackPane stckProducts;

    @FXML
    private TableView<MovieInstance> tblProducts;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Register this controller to listen for MovieInstanceMessage events
        EventBus.getDefault().register(this);
        // Request the list of movie instances from the server
        MovieInstanceController.requestAllMovieInstances();

        listTheater = FXCollections.observableArrayList();
        filterProducts = FXCollections.observableArrayList();
        animateNodes();
        setContextMenu();

        tblProducts.setRowFactory(tv -> {
            TableRow<MovieInstance> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showDialogEdit();
                }
            });
            return row;
        });

    }


    @Subscribe
    public void loadData(MovieInstanceMessage movieInstanceMessage) {
        Platform.runLater(() -> {
            if (movieInstanceMessage.responseType != null) {
                System.out.println(movieInstanceMessage.responseType);
                switch (movieInstanceMessage.responseType) {
                    case MOVIE_INSTANCE_UPDATED:
                    case MOVIE_INSTANCE_REMOVED:
                        MovieInstanceController.requestAllMovieInstances();
                        showAlert("You have updated the screening!", AlertType.SUCCESS);
                        break;
                    case FILLTERD_LIST:
                        loadTableData(movieInstanceMessage.movies);
                        break;
                    case MOVIE_INSTANCE_ADDED:
                        showAlert("You have added the screening!", AlertType.SUCCESS);
                        MovieInstanceController.requestAllMovieInstances();
                        break;
                    default:
                        showAlert("Failed to process the screening .", AlertType.ERROR);
                        break;
                }
            } else {
                System.out.println("ResponseType is null");
            }
        });
    }



    public void showAlert(String messageText, AlertType alertType) {
        AlertsBuilder.create(
                alertType,
                stckProducts,
                stckProducts,
                stckProducts,
                messageText
        );
    }


    private void loadTableData(List<MovieInstance> movies) {
        listTheater.setAll(movies);
        tblProducts.setItems(listTheater);
        tblProducts.setFixedCellSize(30);

        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colEnglish.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMovie().getEnglishName()));
        colHebrew.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMovie().getHebrewName()));
        colDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime().minusHours(3).toLocalDate().toString()));
        colHour.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime().minusHours(3).format(DateTimeFormatter.ofPattern("HH:mm"))));
        colTheater.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHall().getTheater().getLocation()));
        colHall.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getHall().getId())));
    }

    @FXML
    public void createNewScreening() throws IOException {
        showDialog("add");
    }

    private void setContextMenu() {
        contextMenu = new CustomContextMenu(tblProducts,3);

        contextMenu.setActionEdit(ev -> {
            showDialogEdit();
            contextMenu.hide();
        });
        contextMenu.setActionDelete(ev -> {
            showDialogDelete();
            contextMenu.hide();
        });
        contextMenu.show();
    }

    @FXML
    private void showDialog(String operation) {
        tblProducts.setDisable(true);
        MovieInstance selectedMovieInstance = tblProducts.getSelectionModel().getSelectedItem();


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConstantsPath.DIALOG_SCREENING_VIEW));
            AnchorPane moviePane = loader.load();
            DialogEditScreening dialogEditScreening = loader.getController();
            dialogEditScreening.setEditScreeningListBoundary(this);
            dialogEditScreening.setDialog(operation, selectedMovieInstance);

            containerEdit.getChildren().clear();
            containerEdit.getChildren().add(moviePane);
            containerEdit.setVisible(true);

            dialogEdit = new DialogTool(containerEdit, stckProducts);
            dialogEdit.show();

            dialogEdit.setOnDialogClosed(ev -> {
                tblProducts.setDisable(false);
                rootProducts.setEffect(null);
                containerEdit.setVisible(false);
            });

            rootProducts.setEffect(ConstantsPath.BOX_BLUR_EFFECT);

        } catch (IOException e) {
            e.printStackTrace();
            tblProducts.setDisable(false);
        }
    }

    @FXML
    private void showDialogEdit() {
        if (tblProducts.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckProducts, rootProducts, tblProducts, ConstantsPath.MESSAGE_NO_RECORD_SELECTED);
            return;
        }
        showDialog("edit");
    }

    private void showDialogDelete(){
        if (tblProducts.getSelectionModel().getSelectedItems().isEmpty()) {
            AlertsBuilder.create(AlertType.ERROR, stckProducts, rootProducts, tblProducts, ConstantsPath.MESSAGE_NO_RECORD_SELECTED);
            return;
        }

        Platform.runLater(() -> {
            rootProducts.setEffect(ConstantsPath.BOX_BLUR_EFFECT);
            containerDelete.setVisible(true);
            disableTable();

            dialogDelete = new DialogTool(containerDelete, stckProducts);

            deleteButton.setOnAction(ev -> {
                MovieInstance selectedMovieInstance = tblProducts.getSelectionModel().getSelectedItem();
                MovieInstanceController.deleteMovieInstance(selectedMovieInstance.getId());   //working!
                dialogDelete.close();
            });

            dialogDelete.show();

            dialogDelete.setOnDialogClosed(ev -> {
                tblProducts.setDisable(false);
                rootProducts.setEffect(null);
                containerDelete.setVisible(false);
            });
        });
    }

    private void disableTable() {
        tblProducts.setDisable(true);
    }

    @FXML
    public void closeDialog() {
        if (dialogEdit != null) {
            dialogEdit.close();
        }
    }

    @FXML
    private void hideDialogDelete() {
        if (dialogDelete != null) {
            dialogDelete.close();
        }
    }

    private void animateNodes() {
        Animations.fadeInUp(btnNewScrenning);
        Animations.fadeInUp(tblProducts);
        Animations.fadeInUp(hBoxSearch);
    }

    public static void closeStage() {
        if (stage != null) {
            stage.hide();
        }
    }

    public void cleanup()
    {
        EventBus.getDefault().unregister(this);
    }


}
