package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.OperatorOrdersController;
import com.example.shopflowers.model.dao.CustomBouquetOrderDAO;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.util.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
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
import com.example.shopflowers.util.TableDataUtils;

public class OperatorGraphicController {

    private static final String NO_BOUQUET_MESSAGE =
            "Questo ordine non contiene un bouquet personalizzato.";
    private static final String LOAD_ORDERS_ERROR_MESSAGE =
            "Si è verificato un errore durante il caricamento degli ordini.";
    private static final String LOAD_ORDER_ITEMS_ERROR_MESSAGE =
            "Si è verificato un errore durante il caricamento dei dettagli dell'ordine.";
    private static final String LOAD_BOUQUET_ERROR_MESSAGE =
            "Si è verificato un errore durante il caricamento dei dettagli del bouquet.";
    private static final String UPDATE_STATUS_ERROR_MESSAGE =
            "Si è verificato un errore durante l'aggiornamento dello stato dell'ordine.";
    private static final String LOGOUT_ERROR_MESSAGE =
            "Si è verificato un errore durante il logout.";

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

    private FilteredList<OrderSummary> filteredOrders;
    private OrderSummary selectedOrder;
    private boolean showingCompletedOrders = false;

    public OperatorGraphicController() {
        this.operatorOrdersController = new OperatorOrdersController();
            this.customBouquetOrderDAO = DAOFactory.getCustomBouquetOrderDAO();
    }



    @FXML
    public void initialize() {
        configureOrderTable();
        configureOrderItemsTable();
        configureStatusBoxes();
        configureFilters();
        configureSelectionListener();
        loadOrders();
    }

    private void configureOrderTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryMode"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
    }

    private void configureOrderItemsTable() {
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    private void configureStatusBoxes() {
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
    }

    private void configureFilters() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        statusFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void configureSelectionListener() {
        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedOrder = newSelection;

            if (newSelection == null) {
                clearOrderDetails();
                return;
            }

            statusComboBox.setValue(newSelection.getStatus());
            loadOrderItems(newSelection.getId());
            loadBouquetDetails(newSelection.getId());
            messageLabel.setText("Ordine selezionato: stato attuale = " + newSelection.getStatus());
        });
    }

    private void loadOrders() {
        try {
            List<OrderSummary> orders = getOrdersForCurrentView();
            filteredOrders = TableDataUtils.bindFilteredSortedTable(orderTable, orders);
            applyFilters();
            clearOrderDetails();

        } catch (SQLException e) {
            messageLabel.setText(LOAD_ORDERS_ERROR_MESSAGE);
        }
    }

    private List<OrderSummary> getOrdersForCurrentView() throws SQLException {
        return showingCompletedOrders
                ? operatorOrdersController.getCompletedOrders()
                : operatorOrdersController.getActiveOrders();
    }

    private void applyFilters() {
        if (filteredOrders == null) {
            return;
        }

        String searchText = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase();
        String selectedStatus = statusFilterComboBox.getValue();

        filteredOrders.setPredicate(order ->
                order != null
                        && matchesSearch(order, searchText)
                        && matchesStatus(order, selectedStatus)
        );
    }

    private boolean matchesSearch(OrderSummary order, String searchText) {
        return searchText.isBlank()
                || safe(order.getName()).contains(searchText)
                || safe(order.getSurname()).contains(searchText)
                || safe(order.getUsername()).contains(searchText)
                || safe(order.getDeliveryMode()).contains(searchText)
                || safe(order.getPaymentMethod()).contains(searchText)
                || safe(order.getStatus()).contains(searchText);
    }

    private boolean matchesStatus(OrderSummary order, String selectedStatus) {
        return selectedStatus == null
                || selectedStatus.equalsIgnoreCase("Tutti")
                || safe(order.getStatus()).equals(selectedStatus.toLowerCase());
    }

    private void loadOrderItems(int orderId) {
        try {
            List<OrderItemSummary> items = operatorOrdersController.getItemsByOrderId(orderId);
            orderItemsTable.setItems(FXCollections.observableArrayList(items));
        } catch (SQLException e) {
            messageLabel.setText(LOAD_ORDER_ITEMS_ERROR_MESSAGE);
        }
    }

    private void loadBouquetDetails(int orderId) {
        try {
            CustomBouquetOrderSummary bouquet = customBouquetOrderDAO.findByOrderId(orderId);
            bouquetDetailsLabel.setText(buildBouquetDetailsText(bouquet));
        } catch (SQLException e) {
            bouquetDetailsLabel.setText(LOAD_BOUQUET_ERROR_MESSAGE);
        }
    }

    private String buildBouquetDetailsText(CustomBouquetOrderSummary bouquet) {
        if (bouquet == null) {
            return NO_BOUQUET_MESSAGE;
        }

        return String.format(
                "Bouquet personalizzato | Dimensione: %s | Confezione: %s | Biglietto: %s | Vaso: %s | Totale: € %.2f",
                bouquet.getSize(),
                bouquet.getPackaging(),
                bouquet.isCardIncluded() ? "Sì" : "No",
                bouquet.isVaseIncluded() ? "Sì" : "No",
                bouquet.getTotalPrice()
        );
    }

    private void clearOrderDetails() {
        orderItemsTable.getItems().clear();
        bouquetDetailsLabel.setText(NO_BOUQUET_MESSAGE);
        statusComboBox.setValue(null);
        selectedOrder = null;
        messageLabel.setText("");
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
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
            messageLabel.setText(UPDATE_STATUS_ERROR_MESSAGE);
        }
    }

    @FXML
    private void handleShowActiveOrders() {
        showOrders(false);
    }

    @FXML
    private void handleShowCompletedOrders() {
        showOrders(true);
    }

    private void showOrders(boolean completed) {
        showingCompletedOrders = completed;
        messageLabel.setText("");
        loadOrders();
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) orderTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText(LOGOUT_ERROR_MESSAGE);
        }
    }
}