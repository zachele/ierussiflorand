package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.CheckoutController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.Order;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import com.example.shopflowers.ShopFlowersApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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
    @FXML
    private void handleBackToCatalog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/catalog-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 900, 650);

            Stage stage = (Stage) checkoutTable.getScene().getWindow();
            stage.setTitle("Shop Flowers - Catalogo Cliente");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore nel ritorno al catalogo.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/login-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 500, 350);

            Stage stage = (Stage) checkoutTable.getScene().getWindow();
            stage.setTitle("Shop Flowers - Login");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
            e.printStackTrace();
        }
    }
}