package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.StatisticsController;
import com.example.shopflowers.util.SceneNavigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsGraphicController {

    private static final int MAX_PRODUCTS_IN_CHART = 6;
    private static final int MAX_LABEL_LENGTH = 18;

    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label deliveredOrdersLabel;

    @FXML
    private Label activeOrdersLabel;

    @FXML
    private Label totalClientsLabel;

    @FXML
    private Label totalOperatorsLabel;

    @FXML
    private Label mostSoldProductLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private PieChart statisticsPieChart;

    private final StatisticsController statisticsController = new StatisticsController();

    @FXML
    public void initialize() {
        configurePieChart();

        try {
            Map<String, String> statistics = statisticsController.getStatistics();

            totalOrdersLabel.setText(statistics.getOrDefault("Totale ordini", "0"));
            totalRevenueLabel.setText(statistics.getOrDefault("Totale ricavi", "€ 0.00"));
            deliveredOrdersLabel.setText(statistics.getOrDefault("Ordini consegnati", "0"));
            activeOrdersLabel.setText(statistics.getOrDefault("Ordini attivi", "0"));
            totalClientsLabel.setText(statistics.getOrDefault("Clienti registrati", "0"));
            totalOperatorsLabel.setText(statistics.getOrDefault("Operatori registrati", "0"));
            mostSoldProductLabel.setText(statistics.getOrDefault("Prodotto più venduto", "-"));

            loadSoldProductsPieChart();

        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento delle statistiche.");
        }
    }

    private void configurePieChart() {
        statisticsPieChart.setLegendVisible(true);
        statisticsPieChart.setLabelsVisible(true);
        statisticsPieChart.setClockwise(true);
        statisticsPieChart.setStartAngle(90);
    }

    private void loadSoldProductsPieChart() throws SQLException {
        Map<String, Integer> soldProducts = statisticsController.getSoldProductsDistribution();

        statisticsPieChart.getData().clear();

        if (soldProducts == null || soldProducts.isEmpty()) {
            statisticsPieChart.setTitle("Nessun prodotto venduto");
            return;
        }

        statisticsPieChart.setTitle("Distribuzione prodotti venduti");

        Map<String, Integer> sortedProducts = soldProducts.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() > 0)
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(MAX_PRODUCTS_IN_CHART)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (first, second) -> first,
                        java.util.LinkedHashMap::new
                ));

        if (sortedProducts.isEmpty()) {
            statisticsPieChart.setTitle("Nessun prodotto venduto");
            return;
        }

        for (Map.Entry<String, Integer> entry : sortedProducts.entrySet()) {
            String fullName = entry.getKey();
            String chartLabel = abbreviateLabel(fullName);

            PieChart.Data data = new PieChart.Data(chartLabel, entry.getValue());
            statisticsPieChart.getData().add(data);

            installTooltipWhenNodeIsReady(data, fullName, entry.getValue());
        }
    }

    private void installTooltipWhenNodeIsReady(PieChart.Data data, String fullProductName, int soldQuantity) {
        Platform.runLater(() -> {
            if (data.getNode() == null) {
                return;
            }

            Tooltip tooltip = new Tooltip(
                    "Prodotto: " + fullProductName + "\nQuantità venduta: " + soldQuantity
            );
            Tooltip.install(data.getNode(), tooltip);
        });
    }

    private String abbreviateLabel(String text) {
        if (text == null || text.isBlank()) {
            return "Prodotto";
        }

        if (text.length() <= MAX_LABEL_LENGTH) {
            return text;
        }

        return text.substring(0, MAX_LABEL_LENGTH - 3) + "...";
    }

    @FXML
    private void handleBackToAdmin() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/view/admin-product-view.fxml",
                    "Shop Flowers - Gestione Prodotti"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il ritorno all'area amministratore.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) messageLabel.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }
}