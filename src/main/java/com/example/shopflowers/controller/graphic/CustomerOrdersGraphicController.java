package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.CustomerOrdersController;
import com.example.shopflowers.model.dao.CustomBouquetOrderDAO;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;
import com.example.shopflowers.util.SceneNavigator;
import com.example.shopflowers.util.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    private final CustomBouquetOrderDAO customBouquetOrderDAO = new CustomBouquetOrderDAO();

    private ObservableList<OrderSummary> masterOrderList = FXCollections.observableArrayList();
    private FilteredList<OrderSummary> filteredOrders;

    private OrderSummary selectedOrder;

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryMode"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        pickupDateColumn.setCellValueFactory(new PropertyValueFactory<>("pickupDate"));
        pickupTimeColumn.setCellValueFactory(new PropertyValueFactory<>("pickupTime"));
        paymentColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        unitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        setupReadableCells();

        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tutti",
                "IN_PREPARAZIONE",
                "PRONTO",
                "CONSEGNATO"
        ));
        statusFilterComboBox.setValue("Tutti");
        statusFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        orderTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedOrder = newSelection;

            if (newSelection != null) {
                loadOrderItems(newSelection.getId());
                loadBouquetDetails(newSelection.getId());
            } else {
                orderItemsTable.getItems().clear();
                bouquetDetailsLabel.setText("Questo ordine non contiene un bouquet personalizzato.");
            }
        });

        loadOrders();
    }

    private void setupReadableCells() {
        addressColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    return;
                }

                OrderSummary order = getTableView().getItems().get(getIndex());
                if (!"CONSEGNA".equalsIgnoreCase(safe(order.getDeliveryMode()))) {
                    setText("-");
                    return;
                }

                setText(item == null || item.isBlank() ? "-" : item);
            }
        });

        pickupDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    return;
                }

                OrderSummary order = getTableView().getItems().get(getIndex());
                if (!"RITIRO".equalsIgnoreCase(safe(order.getDeliveryMode()))) {
                    setText("-");
                    return;
                }

                setText(item == null || item.isBlank() ? "-" : item);
            }
        });

        pickupTimeColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setText(null);
                    return;
                }

                OrderSummary order = getTableView().getItems().get(getIndex());
                if (!"RITIRO".equalsIgnoreCase(safe(order.getDeliveryMode()))) {
                    setText("-");
                    return;
                }

                setText(item == null || item.isBlank() ? "-" : item);
            }
        });

        totalColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("€ %.2f", item));
            }
        });
    }

    private void loadOrders() {
        try {
            List<OrderSummary> orders =
                    customerOrdersController.getOrdersByUsername(Session.getLoggedUsername());

            masterOrderList = FXCollections.observableArrayList(orders);
            filteredOrders = new FilteredList<>(masterOrderList, order -> true);

            SortedList<OrderSummary> sortedOrders = new SortedList<>(filteredOrders);
            sortedOrders.comparatorProperty().bind(orderTable.comparatorProperty());

            orderTable.setItems(sortedOrders);
            applyFilters();

            orderItemsTable.getItems().clear();
            bouquetDetailsLabel.setText("Questo ordine non contiene un bouquet personalizzato.");
            selectedOrder = null;

        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento degli ordini.");
        }
    }

    private void applyFilters() {
        if (filteredOrders == null) {
            return;
        }

        String selectedStatus = statusFilterComboBox.getValue();

        filteredOrders.setPredicate(order -> {
            if (order == null) {
                return false;
            }

            if (selectedStatus == null || selectedStatus.equalsIgnoreCase("Tutti")) {
                return true;
            }

            return order.getStatus() != null &&
                    order.getStatus().equalsIgnoreCase(selectedStatus);
        });
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void loadOrderItems(int orderId) {
        try {
            List<OrderItemSummary> items = customerOrdersController.getItemsByOrderId(orderId);
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
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (Stage) orderTable.getScene().getWindow(),
                    "/com/example/shopflowers/catalog-view.fxml",
                    "Shop Flowers - Catalogo Cliente"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il ritorno al catalogo.");
        }
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