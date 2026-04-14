package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.OperatorOrdersController;
import com.example.shopflowers.model.dao.CustomBouquetOrderDAO;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private TableColumn<OrderSummary, String> nameColumn;

    @FXML
    private TableColumn<OrderSummary, String> surnameColumn;

    @FXML
    private TableColumn<OrderSummary, String> usernameColumn;

    @FXML
    private TableColumn<OrderSummary, String> deliveryColumn;

    @FXML
    private TableColumn<OrderSummary, String> paymentColumn;

    @FXML
    private TableColumn<OrderSummary, String> statusColumn;

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
    private ComboBox<String> statusComboBox;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private TextField searchField;

    @FXML
    private Label bouquetDetailsLabel;

    @FXML
    private Label messageLabel;

    private final OperatorOrdersController operatorOrdersController;
    private final CustomBouquetOrderDAO customBouquetOrderDAO;

    private ObservableList<OrderSummary> masterOrderList = FXCollections.observableArrayList();
    private FilteredList<OrderSummary> filteredOrders;

    private OrderSummary selectedOrder;
    private boolean showingCompletedOrders = false;

    public OperatorGraphicController() {
        this.operatorOrdersController = new OperatorOrdersController();

        try {
            this.customBouquetOrderDAO = DAOFactory.getCustomBouquetOrderDAO();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare la DAO dei bouquet ordine.", e);
        }
    }

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryMode"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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

        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tutti",
                "IN_PREPARAZIONE",
                "PRONTO"
        ));
        statusFilterComboBox.setValue("Tutti");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        statusFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedOrder = newSelection;

            if (newSelection != null) {
                statusComboBox.setValue(newSelection.getStatus());
                loadOrderItems(newSelection.getId());
                loadBouquetDetails(newSelection.getId());
                messageLabel.setText("Ordine selezionato: stato attuale = " + newSelection.getStatus());
            } else {
                statusComboBox.setValue(null);
                orderItemsTable.getItems().clear();
                bouquetDetailsLabel.setText("Questo ordine non contiene un bouquet personalizzato.");
                messageLabel.setText("");
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        try {
            List<OrderSummary> orders = showingCompletedOrders
                    ? operatorOrdersController.getCompletedOrders()
                    : operatorOrdersController.getActiveOrders();

            masterOrderList = FXCollections.observableArrayList(orders);
            filteredOrders = new FilteredList<>(masterOrderList, order -> true);

            SortedList<OrderSummary> sortedOrders = new SortedList<>(filteredOrders);
            sortedOrders.comparatorProperty().bind(orderTable.comparatorProperty());

            orderTable.setItems(sortedOrders);
            applyFilters();

            orderItemsTable.getItems().clear();
            bouquetDetailsLabel.setText("Questo ordine non contiene un bouquet personalizzato.");
            statusComboBox.setValue(null);
            selectedOrder = null;

        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento degli ordini.");
        }
    }

    private void applyFilters() {
        if (filteredOrders == null) {
            return;
        }

        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedStatus = statusFilterComboBox.getValue();

        filteredOrders.setPredicate(order -> {
            if (order == null) {
                return false;
            }

            boolean matchesSearch = searchText.isBlank()
                    || safe(order.getName()).contains(searchText)
                    || safe(order.getSurname()).contains(searchText)
                    || safe(order.getUsername()).contains(searchText)
                    || safe(order.getDeliveryMode()).contains(searchText)
                    || safe(order.getPaymentMethod()).contains(searchText)
                    || safe(order.getStatus()).contains(searchText);

            boolean matchesStatus = selectedStatus == null
                    || selectedStatus.equalsIgnoreCase("Tutti")
                    || safe(order.getStatus()).equals(selectedStatus.toLowerCase());

            return matchesSearch && matchesStatus;
        });
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void loadOrderItems(int orderId) {
        try {
            List<OrderItemSummary> items = operatorOrdersController.getItemsByOrderId(orderId);
            ObservableList<OrderItemSummary> observableItems = FXCollections.observableArrayList(items);
            orderItemsTable.setItems(observableItems);
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento dei dettagli dell'ordine.");
        }
    }

    private void loadBouquetDetails(int orderId) {
        try {
            CustomBouquetOrderSummary bouquet = customBouquetOrderDAO.findByOrderId(orderId);

            if (bouquet == null) {
                bouquetDetailsLabel.setText("Questo ordine non contiene un bouquet personalizzato.");
                return;
            }

            String details = String.format(
                    "Bouquet personalizzato | Dimensione: %s | Confezione: %s | Biglietto: %s | Vaso: %s | Totale: € %.2f",
                    bouquet.getSize(),
                    bouquet.getPackaging(),
                    bouquet.isCardIncluded() ? "Sì" : "No",
                    bouquet.isVaseIncluded() ? "Sì" : "No",
                    bouquet.getTotalPrice()
            );

            bouquetDetailsLabel.setText(details);

        } catch (SQLException e) {
            bouquetDetailsLabel.setText("Si è verificato un errore durante il caricamento dei dettagli del bouquet.");
        }
    }

    @FXML
    private void handleUpdateStatus() {
        if (selectedOrder == null) {
            messageLabel.setText("Seleziona prima un ordine dalla tabella.");
            return;
        }

        String newStatus = statusComboBox.getValue();
        if (newStatus == null || newStatus.isBlank()) {
            messageLabel.setText("Seleziona uno stato ordine valido.");
            return;
        }

        if (newStatus.equalsIgnoreCase(selectedOrder.getStatus())) {
            messageLabel.setText("L'ordine è già nello stato selezionato.");
            return;
        }

        try {
            operatorOrdersController.updateOrderStatus(selectedOrder.getId(), newStatus);
            messageLabel.setText("Lo stato dell’ordine è stato aggiornato con successo.");
            loadOrders();
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante l'aggiornamento dello stato dell'ordine.");
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

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) orderTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }
}