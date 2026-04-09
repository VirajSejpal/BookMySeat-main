package il.cshaifasweng.OCSFMediatorExample.client.boundaries.reports;

import il.cshaifasweng.OCSFMediatorExample.client.boundaries.user.MainBoundary;
import il.cshaifasweng.OCSFMediatorExample.client.connect.SimpleClient;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.ReportController;
import il.cshaifasweng.OCSFMediatorExample.client.controllers.TheaterController;
import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.ReportMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.TheaterMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class is responsible for handling the user interface of the reports boundary.
 * It manages the report generation, bar chart displays, and ComboBox selections.
 * The class uses JavaFX components to handle different types of reports.
 */
public class ReportsBoundary implements Initializable {

    @FXML
    private AnchorPane rootStatistics;

    @FXML
    private VBox vbox;

    @FXML
    private TabPane tabPane;

    @FXML
    private BarChart<String, Number> ticketSalesBarChart;

    @FXML
    private BarChart<String, Number> packageSalesBarChart;

    @FXML
    private BarChart<String, Number> multiEntryTicketSalesBarChart;

    @FXML
    private BarChart<String, Number> complaintStatusBarChart;

    @FXML
    private ComboBox<Integer> ComplaintsyearComboBox, MultiSalesyearComboBox, TicketSalesyearComboBox, PackageSalesyearComboBox;

    @FXML
    private ComboBox<String> ComplaintsmonthComboBox, MultiSalesmonthComboBox, TicketSalesmonthComboBox, PackageSalesmonthComboBox;

    private String theaterLocation = "";
    private int theaterId = 0;

    /**
     * Initializes the boundary, sets up ComboBox values, event listeners, and event subscriptions.
     *
     * @param location   The location of the FXML resource.
     * @param resources  The resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBus.getDefault().register(this);
        TheaterController.getTheaterNameByTheaterManagerID(SimpleClient.user);
        // Remove tabs if the logged-in user is a theater manager
        if (MainBoundary.loggedInEmployeeId == Employee.EmployeeType.THEATER_MANAGER) {
            tabPane.getTabs().removeIf(tab -> tab.getText().equals("Package Sales") ||
                    tab.getText().equals("Multi-Entry Ticket Sales") ||
                    tab.getText().equals("Complaint Status") ||
                    tab.getText().equals("Additional Reports"));
        }
        // Initialize combo boxes for each type of report
        initializeComboBoxes(ComplaintsyearComboBox, ComplaintsmonthComboBox, "ComplaintStatus");
        initializeComboBoxes(MultiSalesyearComboBox, MultiSalesmonthComboBox, "MultiEntrySales");
        initializeComboBoxes(TicketSalesyearComboBox, TicketSalesmonthComboBox, "TicketSales");
        initializeComboBoxes(PackageSalesyearComboBox, PackageSalesmonthComboBox, "PackageSales");
    }

    /**
     * Handles the receipt of a TheaterMessage, which includes theater location data.
     * Once the message is received, the tab listeners are initialized.
     *
     * @param message The message containing theater data.
     */
    @Subscribe
    public void onTheaterMessageReceived(TheaterMessage message) {
        Platform.runLater(() -> {
            if (message.responseType == TheaterMessage.ResponseType.RETURN_THEATER) {
                if (!message.theaterList.isEmpty()) {
                    this.theaterLocation = message.theaterList.get(0).getLocation();
                    this.theaterId = message.theaterList.get(0).getId();

                    // Now that we have the theater info, initialize the tab listeners and trigger the first tab
                    initializeTabListeners();
                    Platform.runLater(() -> handleTabLeave("Ticket Sales"));
                }
            }
        });
    }

    /**
     * Initializes ComboBox values for year and month selection for a given report type.
     *
     * @param yearComboBox  The ComboBox for selecting the year.
     * @param monthComboBox The ComboBox for selecting the month.
     * @param reportType    The type of report to generate when selections change.
     */
    private void initializeComboBoxes(ComboBox<Integer> yearComboBox, ComboBox<String> monthComboBox, String reportType) {
        int currentYear = java.time.LocalDate.now().getYear();
        for (int i = currentYear; i >= currentYear - 10; i--) {
            yearComboBox.getItems().add(i);
        }
        yearComboBox.setValue(currentYear);
        for (java.time.Month month : java.time.Month.values()) {
            monthComboBox.getItems().add(month.name());
        }
        monthComboBox.setValue(java.time.LocalDate.now().getMonth().name());
        // Trigger report generation based on the current selection immediately after initialization
        handleGenerateReport(reportType, yearComboBox.getValue(), monthComboBox.getValue());
        // Add listeners for ComboBox changes
        yearComboBox.setOnAction(event -> handleGenerateReport(reportType, yearComboBox.getValue(), monthComboBox.getValue()));
        monthComboBox.setOnAction(event -> handleGenerateReport(reportType, yearComboBox.getValue(), monthComboBox.getValue()));
    }

    /**
     * Initializes the tab listeners to handle changes between report types.
     */
    private void initializeTabListeners() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (oldTab != null) {
                handleTabLeave(oldTab.getText());
            }
            String selectedTab = newTab.getText();
            switch (selectedTab) {
                case "Ticket Sales":
                    handleGenerateReport("TicketSales", TicketSalesyearComboBox.getValue(), TicketSalesmonthComboBox.getValue());
                    break;
                case "Package Sales":
                    handleGenerateReport("PackageSales", PackageSalesyearComboBox.getValue(), PackageSalesmonthComboBox.getValue());
                    break;
                case "Multi-Entry Ticket Sales":
                    handleGenerateReport("MultiEntrySales", MultiSalesyearComboBox.getValue(), MultiSalesmonthComboBox.getValue());
                    break;
                case "Complaint Status":
                    handleGenerateReport("ComplaintStatus", ComplaintsyearComboBox.getValue(), ComplaintsmonthComboBox.getValue());
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Handles the logic to clear and update bar charts when leaving a tab.
     *
     * @param tabName The name of the tab that is being left.
     */
    private void handleTabLeave(String tabName) {
        switch (tabName) {
            case "Ticket Sales":
                clearBarChart(ticketSalesBarChart);
                handleGenerateReport("TicketSales", TicketSalesyearComboBox.getValue(), TicketSalesmonthComboBox.getValue());
                break;
            case "Package Sales":
                clearBarChart(packageSalesBarChart);
                handleGenerateReport("PackageSales", PackageSalesyearComboBox.getValue(), PackageSalesmonthComboBox.getValue());
                break;
            case "Multi Entry Sales":
                clearBarChart(multiEntryTicketSalesBarChart);
                handleGenerateReport("MultiEntrySales", MultiSalesyearComboBox.getValue(), MultiSalesmonthComboBox.getValue());
                break;
            case "Complaint Status":
                clearBarChart(complaintStatusBarChart);
                handleGenerateReport("ComplaintStatus", ComplaintsyearComboBox.getValue(), ComplaintsmonthComboBox.getValue());
                break;
            default:
                break;
        }
    }

    /**
     * Clears the given bar chart by removing all data.
     *
     * @param barChart The BarChart to be cleared.
     */

    private void clearBarChart(BarChart<String, Number> barChart) {
        barChart.getData().clear();
    }

    /**
     * Handles generating a report for the specified type and time range.
     *
     * @param reportType The type of report to generate (e.g., Ticket Sales, Package Sales).
     * @param year       The selected year for the report.
     * @param month      The selected month for the report.
     */
    @FXML
    private void handleGenerateReport(String reportType, int year, String month) {
        switch (reportType) {
            case "TicketSales":
                if (theaterId != 0) {
                    ReportController.requestTheaterTicketSalesReport(year, getMonthNumber(month), theaterId);
                } else {
                    ReportController.requestTicketSalesReport(year, getMonthNumber(month));
                }
                break;
            case "PackageSales":
                ReportController.requestPackageSalesReport(year, getMonthNumber(month), theaterId);
                break;
            case "MultiEntrySales":
                ReportController.requestMultiEntrySalesReport(year, getMonthNumber(month), theaterId);
                break;
            case "ComplaintStatus":
                ReportController.requestComplaintStatusReport(year, getMonthNumber(month), theaterId);
                break;
        }
    }

    /**
     * Converts the given month name to its corresponding integer value.
     *
     * @param monthName The name of the month (e.g., "January").
     * @return The integer value of the month (e.g., 1 for January).
     */
    private int getMonthNumber(String monthName) {
        return java.time.Month.valueOf(monthName.toUpperCase()).getValue();
    }

    /**
     * Gets the current year.
     *
     * @return The current year.
     */
    private int getCurrentYear() {
        return java.time.LocalDate.now().getYear();
    }

    /**
     * Gets the current month name.
     *
     * @return The name of the current month.
     */
    private String getCurrentMonth() {
        return java.time.LocalDate.now().getMonth().name();
    }

    /**
     * Receives and processes the report message to update the corresponding bar chart.
     *
     * @param reportMessage The message containing report data.
     */
    @Subscribe
    public void onReportMessageReceived(ReportMessage reportMessage) {
        Platform.runLater(() -> {
            switch (reportMessage.getResponseType()) {
                case TICKET_SALES_DATA:
                    ReportFactory.updateCombinedBarChart(reportMessage.getPurchases(), reportMessage.getSalesData(), ticketSalesBarChart);
                    break;
                case THEATER_TICKET_SALES_DATA:
                    ReportFactory.updatePurchaseBarChart(reportMessage.getPurchases(), ticketSalesBarChart, theaterLocation);
                    break;
                case PACKAGE_SALES_DATA:
                    ReportFactory.updatePurchaseBarChart(reportMessage.getPurchases(), packageSalesBarChart, "Home Viewing Packages");
                    break;
                case MULTI_ENTRY_SALES_DATA:
                    ReportFactory.updatePurchaseBarChart(reportMessage.getPurchases(), multiEntryTicketSalesBarChart, "Multi Entry Packages");
                    break;
                case COMPLAINT_STATUS_DATA:
                    ReportFactory.updateComplaintBarChart(reportMessage.getComplaints(), complaintStatusBarChart);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * Cleans up resources and unregisters from the event bus when closing the window.
     */
    public void cleanup() {
        Platform.runLater(() -> {
            clearAllCharts();
            EventBus.getDefault().unregister(this);
        });
    }

    /**
     * Clears all bar charts in the view.
     */
    private void clearAllCharts() {
        clearBarChart(ticketSalesBarChart);
        clearBarChart(packageSalesBarChart);
        clearBarChart(multiEntryTicketSalesBarChart);
        clearBarChart(complaintStatusBarChart);
    }
}
