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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

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
                CustomBouquetSession.clear();
                bouquetInfoLabel.setText("Nessun bouquet personalizzato nel pagamento corrente.");
            }

            totalLabel.setText(String.format("Totale ordine: € %.2f", total));
        }
    }

    @FXML
    private void handleConfirmOrder() {
        if ((sharedCartController == null || sharedCartController.isCartEmpty()) && !CustomBouquetSession.hasBouquet()) {
            messageLabel.setText("Checkout non disponibile. Il carrello e il bouquet personalizzato sono vuoti.");
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
                    sharedCartController != null ? sharedCartController.getCartItems() : java.util.Collections.emptyList(),
                    deliveryMode,
                    address,
                    pickupDate,
                    pickupTime,
                    paymentMethod
            );

            boolean confirmed = checkoutController.confirmOrder(order);

            if (!confirmed) {
                messageLabel.setText("Ordine non confermato: stock insufficiente o ordine vuoto.");
                return;
            }

            if (sharedCartController != null) {
                sharedCartController.clearCart();
            }

            checkoutTable.getItems().clear();
            CustomBouquetSession.clear();
            bouquetInfoLabel.setText("Nessun bouquet personalizzato nel pagamento corrente.");
            totalLabel.setText(String.format("Totale ordine: € %.2f", 0.0));
            addressField.clear();
            pickupDatePicker.setValue(null);
            pickupTimeComboBox.setValue(null);
            paymentField.clear();
            messageLabel.setText("Ordine confermato con successo.");

        } catch (Exception e) {
            messageLabel.setText("Ordine non confermato: uno o più prodotti non sono più disponibili nella quantità richiesta.");
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
            messageLabel.setText("Si è verificato un errore durante il ritorno al catalogo.");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            SceneNavigator.logoutToLogin((Stage) checkoutTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }
    @FXML
    private void handleRemoveBouquet() {
        if (!CustomBouquetSession.hasBouquet()) {
            messageLabel.setText("Nessun bouquet presente nel pagamento corrente.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere il bouquet dal pagamento corrente?");
        alert.setContentText("Il bouquet personalizzato verrà eliminato dal checkout.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Operazione annullata.");
            return;
        }

        CustomBouquetSession.clear();
        bouquetInfoLabel.setText("Nessun bouquet personalizzato nel pagamento corrente.");

        double total = sharedCartController != null ? sharedCartController.getCartTotal() : 0.0;
        totalLabel.setText(String.format("Totale ordine: € %.2f", total));

        messageLabel.setText("Bouquet rimosso dal pagamento corrente con successo.");
    }
}