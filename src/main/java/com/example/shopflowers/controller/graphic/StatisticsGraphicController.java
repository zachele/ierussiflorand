package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.StatisticsController;
import com.example.shopflowers.util.SceneNavigator;
import javafx.fxml.FXML;
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

        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento statistiche.");
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
            messageLabel.setText("Errore nel ritorno all'area admin.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) messageLabel.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }
}