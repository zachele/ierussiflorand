package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.CheckoutController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.Order;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.shopflowers.util.Session;
import com.example.shopflowers.util.SceneNavigator;

import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.util.CustomBouquetSession;

public class CheckoutGraphicController {

    @FXML
    private TableView<CartItem> checkoutTable;

    @FXML
    private TableColumn<CartItem, String> productColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, Double> totalColumn;

    @FXML
    private Label totalLabel;

    @FXML
    private ComboBox<String> deliveryModeComboBox;

    @FXML
    private TextField paymentField;

    @FXML
    private TextField addressField;

    @FXML
    private Label messageLabel;

    @FXML
    private DatePicker pickupDatePicker;

    @FXML
    private ComboBox<String> pickupTimeComboBox;

    @FXML
    private Label bouquetInfoLabel;

    private static CustomerCartController sharedCartController;

    private final CheckoutController checkoutController = new CheckoutController();

    public static void setCartController(CustomerCartController cartController) {
        sharedCartController = cartController;
    }

    @FXML
    public void initialize() {
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        deliveryModeComboBox.setItems(FXCollections.observableArrayList("CONSEGNA", "RITIRO"));

        pickupTimeComboBox.setItems(FXCollections.observableArrayList(
                "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "12:00", "12:30",
                "16:00", "16:30", "17:00", "17:30",
                "18:00", "18:30", "19:00"
        ));

        addressField.setDisable(true);
        pickupDatePicker.setDisable(true);
        pickupTimeComboBox.setDisable(true);

        deliveryModeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if ("CONSEGNA".equals(newValue)) {
                addressField.setDisable(false);
                pickupDatePicker.setDisable(true);
                pickupTimeComboBox.setDisable(true);
                pickupDatePicker.setValue(null);
                pickupTimeComboBox.setValue(null);
            } else if ("RITIRO".equals(newValue)) {
                addressField.setDisable(true);
                addressField.clear();
                pickupDatePicker.setDisable(false);
                pickupTimeComboBox.setDisable(false);
            }
        });

        if (sharedCartController != null) {
            checkoutTable.setItems(FXCollections.observableArrayList(sharedCartController.getCartItems()));

            double total = sharedCartController.getCartTotal();

            if (CustomBouquetSession.hasBouquet()) {
                CustomBouquet bouquet = CustomBouquetSession.getCurrentBouquet();
                bouquetInfoLabel.setText(
                        String.format("Bouquet personalizzato: %s | Totale bouquet: € %.2f",
                                bouquet.getDescription(),
                                bouquet.getTotalPrice())
                );
                total += bouquet.getTotalPrice();
            } else {
                bouquetInfoLabel.setText("Nessun bouquet personalizzato selezionato.");
            }

            totalLabel.setText(String.format("Totale ordine: € %.2f", total));
        }
    }

    @FXML
    private void handleConfirmOrder() {
        if (sharedCartController == null || sharedCartController.isCartEmpty()) {
            messageLabel.setText("Il carrello è vuoto.");
            return;
        }

        String deliveryMode = deliveryModeComboBox.getValue();
        String paymentMethod = paymentField.getText();
        String address = addressField.getText();
        String pickupDate = null;
        String pickupTime = null;

        if (deliveryMode == null || paymentMethod == null || paymentMethod.isBlank()) {
            messageLabel.setText("Compila tutti i campi del checkout.");
            return;
        }

        if ("CONSEGNA".equals(deliveryMode)) {
            if (address == null || address.isBlank()) {
                messageLabel.setText("Inserisci l'indirizzo di consegna.");
                return;
            }
        }

        if ("RITIRO".equals(deliveryMode)) {
            if (pickupDatePicker.getValue() == null || pickupTimeComboBox.getValue() == null) {
                messageLabel.setText("Seleziona giorno e ora del ritiro.");
                return;
            }

            pickupDate = pickupDatePicker.getValue().toString();
            pickupTime = pickupTimeComboBox.getValue();
            address = null;
        }

        try {
            Order order = checkoutController.createOrder(
                    Session.getLoggedUsername(),
                    sharedCartController.getCartItems(),
                    deliveryMode,
                    address,
                    pickupDate,
                    pickupTime,
                    paymentMethod
            );

            boolean confirmed = checkoutController.confirmOrder(order);

            if (!confirmed) {
                messageLabel.setText("Ordine non confermato: stock insufficiente.");
                return;
            }

            sharedCartController.clearCart();
            checkoutTable.getItems().clear();
            totalLabel.setText("Totale ordine: € 0.0");
            addressField.clear();
            pickupDatePicker.setValue(null);
            pickupTimeComboBox.setValue(null);
            paymentField.clear();
            messageLabel.setText("Ordine confermato con successo.");

        } catch (Exception e) {
            messageLabel.setText("Errore durante la conferma dell'ordine.");
        }
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (Stage) checkoutTable.getScene().getWindow(),
                    "/com/example/shopflowers/catalog-view.fxml",
                    "Shop Flowers - Catalogo Cliente"
            );

        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno al catalogo.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) checkoutTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }

}