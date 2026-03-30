package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.StatisticsController;
import com.example.shopflowers.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class StatisticsGraphicController {

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
        try {
            Map<String, String> statistics = statisticsController.getStatistics();

            totalOrdersLabel.setText(statistics.get("Totale ordini"));
            totalRevenueLabel.setText(statistics.get("Totale ricavi"));
            deliveredOrdersLabel.setText(statistics.get("Ordini consegnati"));
            activeOrdersLabel.setText(statistics.get("Ordini attivi"));
            totalClientsLabel.setText(statistics.get("Clienti registrati"));
            totalOperatorsLabel.setText(statistics.get("Operatori registrati"));
            mostSoldProductLabel.setText(statistics.get("Prodotto più venduto"));

            loadSoldProductsPieChart();

        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento delle statistiche.");
        }
    }

    private void loadSoldProductsPieChart() throws SQLException {
        Map<String, Integer> soldProducts = statisticsController.getSoldProductsDistribution();

        statisticsPieChart.getData().clear();

        if (soldProducts.isEmpty()) {
            statisticsPieChart.setTitle("Nessun prodotto venduto");
            return;
        }

        statisticsPieChart.setTitle("Distribuzione prodotti venduti");

        for (Map.Entry<String, Integer> entry : soldProducts.entrySet()) {
            statisticsPieChart.getData().add(
                    new PieChart.Data(entry.getKey(), entry.getValue())
            );
        }
    }

    @FXML
    private void handleBackToAdmin() {
        try {
            SceneNavigator.goTo(
                    (Stage) messageLabel.getScene().getWindow(),
                    "/com/example/shopflowers/admin-product-view.fxml",
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