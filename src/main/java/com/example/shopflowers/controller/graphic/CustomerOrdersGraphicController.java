package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.ShopFlowersApplication;
import com.example.shopflowers.controller.application.CustomerOrdersController;
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

public class CustomerOrdersGraphicController {

    @FXML
    private TableView<OrderSummary> orderTable;

    @FXML
    private TableColumn<OrderSummary, Integer> orderIdColumn;

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
    private TableColumn<OrderSummary, String> statusColumn;

    private final CustomerOrdersController customerOrdersController = new CustomerOrdersController();

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryMode"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadOrderItems(newSelection.getId());
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        try {
            List<OrderSummary> orders = customerOrdersController.getOrdersByUsername(Session.getLoggedUsername());
            ObservableList<OrderSummary> observableOrders = FXCollections.observableArrayList(orders);
            orderTable.setItems(observableOrders);
        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento ordini.");
        }
    }

    private void loadOrderItems(int orderId) {
        try {
            List<OrderItemSummary> items = customerOrdersController.getItemsByOrderId(orderId);
            ObservableList<OrderItemSummary> observableItems = FXCollections.observableArrayList(items);
            orderItemsTable.setItems(observableItems);
        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento dettagli ordine.");
        }
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/catalog-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 900, 650);

            Stage stage = (Stage) orderTable.getScene().getWindow();
            stage.setTitle("Shop Flowers - Catalogo Cliente");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno al catalogo.");
        }
    }

    @FXML
    private void handleLogout() {
        try{
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
}