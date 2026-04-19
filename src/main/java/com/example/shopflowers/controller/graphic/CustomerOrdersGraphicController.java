package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.config.OrderStatusFilters;
import com.example.shopflowers.config.UiTitles;
import com.example.shopflowers.config.ViewPaths;
import com.example.shopflowers.controller.application.CustomerOrdersController;
import com.example.shopflowers.model.dao.CustomBouquetOrderDAO;
import com.example.shopflowers.model.dao.CustomBouquetOrderDBDAO;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.util.SceneNavigator;
import com.example.shopflowers.util.Session;
import com.example.shopflowers.util.TableDataUtils;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CustomerOrdersGraphicController {

    private static final String DELIVERY_MODE_HOME = "CONSEGNA";
    private static final String DELIVERY_MODE_PICKUP = "RITIRO";

    private static final String NO_BOUQUET_MESSAGE =
            "Questo ordine non contiene un bouquet personalizzato.";
    private static final String LOAD_ORDERS_ERROR_MESSAGE =
            "Si è verificato un errore durante il caricamento degli ordini.";
    private static final String LOAD_ORDER_ITEMS_ERROR_MESSAGE =
            "Si è verificato un errore durante il caricamento dei dettagli dell'ordine.";
    private static final String LOAD_BOUQUET_ERROR_MESSAGE =
            "Si è verificato un errore durante il caricamento dei dettagli del bouquet.";
    private static final String BACK_TO_CATALOG_ERROR_MESSAGE =
            "Si è verificato un errore durante il ritorno al catalogo.";
    private static final String LOGOUT_ERROR_MESSAGE =
            "Si è verificato un errore durante il logout.";

    @FXML
    private TableView<OrderSummary> orderTable;

    @FXML
    private TableColumn<OrderSummary, Integer> orderIdColumn;

    @FXML
    private TableColumn<OrderSummary, String> deliveryColumn;

    @FXML
    private TableColumn<OrderSummary, String> addressColumn;

    @FXML
    private TableColumn<OrderSummary, String> pickupDateColumn;

    @FXML
    private TableColumn<OrderSummary, String> pickupTimeColumn;

    @FXML
    private TableColumn<OrderSummary, String> paymentColumn;

    @FXML
    private TableColumn<OrderSummary, Double> totalColumn;

    @FXML
    private TableColumn<OrderSummary, String> dateColumn;

    @FXML
    private TableColumn<OrderSummary, String> statusColumn;

    @FXML
    private TableView<OrderItemSummary> orderItemsTable;

    @FXML
    private TableColumn<OrderItemSummary, String> productColumn;

    @FXML
    private TableColumn<OrderItemSummary, Integer> quantityColumn;

    @FXML
    private TableColumn<OrderItemSummary, Double> unitPriceColumn;

    @FXML
    private ComboBox<String> statusFilterComboBox;

    @FXML
    private Label bouquetDetailsLabel;

    @FXML
    private Label messageLabel;

    private final CustomerOrdersController customerOrdersController = new CustomerOrdersController();
    private final CustomBouquetOrderDAO customBouquetOrderDAO;

    private FilteredList<OrderSummary> filteredOrders;

    public CustomerOrdersGraphicController() {
        this.customBouquetOrderDAO = new CustomBouquetOrderDBDAO();
    }

    @FXML
    public void initialize() {
        configureOrderTable();
        configureOrderItemsTable();
        configureReadableCells();
        configureStatusFilter();
        configureOrderSelection();
        loadOrders();
    }

    private void configureOrderTable() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryMode"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        pickupDateColumn.setCellValueFactory(new PropertyValueFactory<>("pickupDate"));
        pickupTimeColumn.setCellValueFactory(new PropertyValueFactory<>("pickupTime"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void configureOrderItemsTable() {
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
    }

    private void configureReadableCells() {
        addressColumn.setCellFactory(column -> createDeliveryDependentTextCell(DELIVERY_MODE_HOME));
        pickupDateColumn.setCellFactory(column -> createDeliveryDependentTextCell(DELIVERY_MODE_PICKUP));
        pickupTimeColumn.setCellFactory(column -> createDeliveryDependentTextCell(DELIVERY_MODE_PICKUP));

        totalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("€ %.2f", item));
            }
        });
    }

    private TableCell<OrderSummary, String> createDeliveryDependentTextCell(String requiredMode) {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    return;
                }

                OrderSummary order = getCurrentOrder();
                if (order == null || !requiredMode.equalsIgnoreCase(safe(order.getDeliveryMode()))) {
                    setText("-");
                    return;
                }

                setText(item == null || item.isBlank() ? "-" : item);
            }

            private OrderSummary getCurrentOrder() {
                if (getTableRow() == null) {
                    return null;
                }

                Object rowItem = getTableRow().getItem();
                return rowItem instanceof OrderSummary order ? order : null;
            }
        };
    }

    private void configureStatusFilter() {
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                OrderStatusFilters.ALL,
                OrderStatusFilters.IN_PREPARATION,
                OrderStatusFilters.READY
        ));
        statusFilterComboBox.setValue(OrderStatusFilters.ALL);
        statusFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
    }

    private void configureOrderSelection() {
        orderTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> handleOrderSelection(newSelection)
        );
    }

    private void handleOrderSelection(OrderSummary order) {
        if (order == null) {
            clearOrderDetails();
            return;
        }

        loadOrderItems(order.getId());
        loadBouquetDetails(order.getId());
    }

    private void loadOrders() {
        try {
            List<OrderSummary> orders =
                    customerOrdersController.getOrdersByUsername(Session.getLoggedUsername());

            filteredOrders = TableDataUtils.bindFilteredSortedTable(orderTable, orders);
            applyFilters();
            clearOrderDetails();

        } catch (SQLException e) {
            messageLabel.setText(LOAD_ORDERS_ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        if (filteredOrders == null) {
            return;
        }

        String selectedStatus = statusFilterComboBox.getValue();

        filteredOrders.setPredicate(order ->
                order != null && matchesSelectedStatus(order, selectedStatus)
        );
    }

    private boolean matchesSelectedStatus(OrderSummary order, String selectedStatus) {
        if (selectedStatus == null || selectedStatus.equalsIgnoreCase(OrderStatusFilters.ALL)) {
            return true;
        }

        return order.getStatus() != null
                && order.getStatus().equalsIgnoreCase(selectedStatus);
    }

    private void loadOrderItems(int orderId) {
        try {
            List<OrderItemSummary> items = customerOrdersController.getItemsByOrderId(orderId);
            orderItemsTable.setItems(FXCollections.observableArrayList(items));
        } catch (SQLException e) {
            messageLabel.setText(LOAD_ORDER_ITEMS_ERROR_MESSAGE);
        }
    }

    private void loadBouquetDetails(int orderId) {
        try {
            CustomBouquetOrderSummary bouquet = customBouquetOrderDAO.findByOrderId(orderId);

            if (bouquet == null) {
                bouquetDetailsLabel.setText(NO_BOUQUET_MESSAGE);
                return;
            }

            bouquetDetailsLabel.setText(buildBouquetDetailsText(bouquet));

        } catch (SQLException e) {
            bouquetDetailsLabel.setText(LOAD_BOUQUET_ERROR_MESSAGE);
        }
    }

    private String buildBouquetDetailsText(CustomBouquetOrderSummary bouquet) {
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
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (Stage) orderTable.getScene().getWindow(),
                    ViewPaths.CATALOG_VIEW,
                    UiTitles.CATALOG_CUSTOMER
            );
        } catch (IOException e) {
            messageLabel.setText(BACK_TO_CATALOG_ERROR_MESSAGE);
        }
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