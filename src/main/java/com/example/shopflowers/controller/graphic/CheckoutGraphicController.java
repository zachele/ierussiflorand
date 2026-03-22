package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.CheckoutController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.Order;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    private Label messageLabel;

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

        if (sharedCartController != null) {
            checkoutTable.setItems(FXCollections.observableArrayList(sharedCartController.getCartItems()));
            totalLabel.setText("Totale ordine: € " + sharedCartController.getCartTotal());
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

        if (deliveryMode == null || paymentMethod == null || paymentMethod.isBlank()) {
            messageLabel.setText("Compila tutti i campi del checkout.");
            return;
        }

        try {
            Order order = checkoutController.createOrder(
                    sharedCartController.getCartItems(),
                    deliveryMode,
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
            messageLabel.setText("Ordine confermato con successo.");

        } catch (Exception e) {
            messageLabel.setText("Errore durante la conferma dell'ordine.");
            e.printStackTrace();
        }
    }
}