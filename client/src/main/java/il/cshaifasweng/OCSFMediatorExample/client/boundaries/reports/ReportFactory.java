package il.cshaifasweng.OCSFMediatorExample.client.boundaries.reports;

import il.cshaifasweng.OCSFMediatorExample.client.util.ConstantsPath;
import il.cshaifasweng.OCSFMediatorExample.entities.Complaint;
import il.cshaifasweng.OCSFMediatorExample.entities.Purchase;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for updating different types of reports in bar charts.
 * This class handles updating bar charts based on sales, purchases, and complaints data.
 */
public class ReportFactory {

    /**
     * Creates a series for the provided data and adds it to the given BarChart.
     *
     * @param data      The map of data where the key is the x-axis label, and the value is the count.
     * @param barChart  The BarChart to update.
     * @param seriesName The name of the series.
     */
    private static void createSeriesForBarChart(Map<String, Long> data, BarChart<String, Number> barChart, String seriesName) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(seriesName);

        data.forEach((key, value) -> {
            series.getData().add(new XYChart.Data<>(key, value));
        });

        barChart.getData().add(series);
    }

    /**
     * Updates the sales histogram BarChart with sales data grouped by cinema and day.
     *
     * @param salesData The sales data to be displayed.
     * @param barChart  The BarChart to update.
     */
    public static void updateSalesHistogram(Map<String, Map<LocalDateTime, Long>> salesData, BarChart<String, Number> barChart) {
        // Prepare a map to collect sales by cinema and day
        Map<String, Map<String, Long>> salesByCinemaAndDay = new HashMap<>();

        // Populate salesByCinemaAndDay with the provided salesData
        salesData.forEach((theaterLocation, dateSalesMap) -> {
            salesByCinemaAndDay.putIfAbsent(theaterLocation, new HashMap<>());
            dateSalesMap.forEach((date, salesCount) -> {
                String day = date.format(DateTimeFormatter.ofPattern("dd"));
                salesByCinemaAndDay.get(theaterLocation).merge(day, salesCount, Long::sum);
            });
        });

        // Collect and sort all unique days across all cinemas
        List<String> sortedDays = salesByCinemaAndDay.values().stream()
                .flatMap(salesByDay -> salesByDay.keySet().stream())
                .distinct()
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());

        // Clear the BarChart before adding new data
        barChart.getData().clear();

        // Create a series for each cinema with sorted days
        salesByCinemaAndDay.forEach((cinemaName, salesByDay) -> {
            Map<String, Long> sortedSales = new LinkedHashMap<>();
            for (String day : sortedDays) {
                if (salesByDay.containsKey(day)) {
                    sortedSales.put(day, salesByDay.get(day));
                }
            }
            createSeriesForBarChart(sortedSales, barChart, cinemaName);
        });
    }

    /**
     * Updates the BarChart with both purchase data and sales data.
     *
     * @param purchases The list of purchases to be displayed.
     * @param salesData The sales data to be displayed.
     * @param barChart  The BarChart to update.
     */
    public static void updateCombinedBarChart(List<Purchase> purchases, Map<String, Map<LocalDateTime, Long>> salesData, BarChart<String, Number> barChart) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(ConstantsPath.DATE_FORMAT);

        // Grouping purchases by date and time
        Map<String, Long> salesByDayTime = purchases.stream()
                .collect(Collectors.groupingBy(p -> p.getPurchaseDate().format(formatter), Collectors.counting()));

        // Clear the chart before adding data
        barChart.getData().clear();

        // Add purchases to the BarChart
        createSeriesForBarChart(salesByDayTime, barChart, "Purchases");

        // Update the sales histogram
        updateSalesHistogram(salesData, barChart);
    }

    /**
     * Updates the BarChart based on the list of purchases.
     *
     * @param purchases The list of purchases to be displayed.
     * @param barChart  The BarChart to update.
     * @param title     The title of the series.
     */
    /**
     * Updates the BarChart based on the list of purchases.
     *
     * @param purchases The list of purchases to be displayed.
     * @param barChart  The BarChart to update.
     * @param title     The title of the series.
     */
    public static void updatePurchaseBarChart(List<Purchase> purchases, BarChart<String, Number> barChart, String title) {
        barChart.getData().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");

        // Grouping purchases by day of the month
        Map<String, Long> salesByDayTime = purchases.stream()
                .collect(Collectors.groupingBy(p -> p.getPurchaseDate().format(formatter), Collectors.counting()));

        // Sort by day number
        Map<String, Long> sortedSales = salesByDayTime.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Add purchases to the BarChart
        createSeriesForBarChart(sortedSales, barChart, title);
    }

    /**
     * Updates the BarChart based on the list of complaints.
     *
     * @param complaints The list of complaints to be displayed.
     * @param barChart   The BarChart to update.
     */
    public static void updateComplaintBarChart(List<Complaint> complaints, BarChart<String, Number> barChart) {
        barChart.getData().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");

        // Grouping complaints by day of the month
        Map<String, Long> complaintsByDayTime = complaints.stream()
                .collect(Collectors.groupingBy(c -> c.getCreationDate().format(formatter), Collectors.counting()));

        // Sort by day number
        Map<String, Long> sortedComplaints = complaintsByDayTime.entrySet().stream()
                .sorted(Comparator.comparingInt(e -> Integer.parseInt(e.getKey())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Add complaints to the BarChart
        createSeriesForBarChart(sortedComplaints, barChart, "Complaints");
    }
}
