package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.ShopFlowersApplication;
import com.example.shopflowers.controller.application.OperatorOrdersController;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OperatorGraphicController {

    @FXML
    private TableView<OrderSummary> orderTable;

    @FXML
    private TableColumn<OrderSummary, Integer> orderIdColumn;

    @FXML
    private TableColumn<OrderSummary, String> usernameColumn;

    @FXML
    private TableColumn<OrderSummary, String> deliveryColumn;

    @FXML
    private TableColumn<OrderSummary, String> paymentColumn;

    @FXML
    private TableColumn<OrderSummary, Double> totalColumn;

    @FXML
    private TableColumn<OrderSummary, String> dateColumn;

    @FXML
    private TableView<OrderItemSummary> orderItemsTable;

    @FXML
    private TableColumn<OrderItemSummary, String> productColumn;

    @FXML
    private TableColumn<OrderItemSummary, Integer> quantityColumn;

    @FXML
    private TableColumn<OrderItemSummary, Double> unitPriceColumn;

    @FXML
    private Label messageLabel;

    @FXML
    private ComboBox<String> statusComboBox;

    private OrderSummary selectedOrder;

    private final OperatorOrdersController operatorOrdersController = new OperatorOrdersController();

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryMode"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        statusComboBox.setItems(FXCollections.observableArrayList(
                "IN_PREPARAZIONE",
                "PRONTO",
                "CONSEGNATO"
        ));

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedOrder = newSelection;
                statusComboBox.setValue(newSelection.getStatus());
                loadOrderItems(newSelection.getId());
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        try {
            List<OrderSummary> orders = showingCompletedOrders
                    ? operatorOrdersController.getCompletedOrders()
                    : operatorOrdersController.getActiveOrders();

            ObservableList<OrderSummary> observableOrders = FXCollections.observableArrayList(orders);
            orderTable.setItems(observableOrders);
            orderItemsTable.getItems().clear();
            selectedOrder = null;
        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento ordini.");
        }
    }

    private void loadOrderItems(int orderId) {
        try {
            List<OrderItemSummary> items = operatorOrdersController.getItemsByOrderId(orderId);
            ObservableList<OrderItemSummary> observableItems = FXCollections.observableArrayList(items);
            orderItemsTable.setItems(observableItems);
        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento dettagli ordine.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clearSession();

            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/login-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 500, 350);

            Stage stage = (Stage) orderTable.getScene().getWindow();
            stage.setTitle("Shop Flowers - Login");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }
    @FXML
    private void handleUpdateStatus() {
        if (selectedOrder == null) {
            messageLabel.setText("Seleziona prima un ordine.");
            return;
        }

        String newStatus = statusComboBox.getValue();
        if (newStatus == null || newStatus.isBlank()) {
            messageLabel.setText("Seleziona uno stato valido.");
            return;
        }

        try {
            operatorOrdersController.updateOrderStatus(selectedOrder.getId(), newStatus);
            messageLabel.setText("Stato ordine aggiornato.");
            loadOrders();
            orderItemsTable.getItems().clear();
            selectedOrder = null;
        } catch (SQLException e) {
            messageLabel.setText("Errore durante l'aggiornamento dello stato.");
        }
    }
    @FXML
    private void handleShowActiveOrders() {
        showingCompletedOrders = false;
        messageLabel.setText("");
        loadOrders();
    }

    @FXML
    private void handleShowCompletedOrders() {
        showingCompletedOrders = true;
        messageLabel.setText("");
        loadOrders();
    }
    private boolean showingCompletedOrders = false;
}