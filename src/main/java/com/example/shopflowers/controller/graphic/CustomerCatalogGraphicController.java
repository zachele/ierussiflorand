package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.FlowerProduct;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

import com.example.shopflowers.ShopFlowersApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.shopflowers.util.Session;

public class CustomerCatalogGraphicController {

    @FXML
    private TableView<FlowerProduct> productTable;

    @FXML
    private TableColumn<FlowerProduct, Integer> idColumn;

    @FXML
    private TableColumn<FlowerProduct, String> nameColumn;

    @FXML
    private TableColumn<FlowerProduct, Double> priceColumn;

    @FXML
    private TableColumn<FlowerProduct, String> colorColumn;

    @FXML
    private TableColumn<FlowerProduct, String> varietyColumn;

    @FXML
    private TableColumn<FlowerProduct, Integer> stockColumn;

    @FXML
    private TextField quantityField;

    @FXML
    private Label messageLabel;

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> cartNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> cartQuantityColumn;

    @FXML
    private TableColumn<CartItem, Double> cartTotalColumn;

    @FXML
    private Label totalLabel;

    private final BrowseCatalogController browseCatalogController = new BrowseCatalogController();

    private static final CustomerCartController customerCartController = new CustomerCartController();

    private FlowerProduct selectedProduct;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        varietyColumn.setCellValueFactory(new PropertyValueFactory<>("variety"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQuantity"));

        cartNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedProduct = newSelection;
        });
        cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedCartItem = newSelection;
        });

        loadProducts();
        refreshCart();
    }

    @FXML
    private void handleAddToCart() {
        if (selectedProduct == null) {
            messageLabel.setText("Seleziona prima un prodotto.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity <= 0) {
                messageLabel.setText("La quantità deve essere maggiore di zero.");
                return;
            }

            boolean added = customerCartController.addToCart(selectedProduct, quantity);

            if (!added) {
                messageLabel.setText("Quantità richiesta superiore allo stock disponibile.");
                return;
            }

            messageLabel.setText("Prodotto aggiunto al carrello.");
            quantityField.clear();
            refreshCart();

        } catch (NumberFormatException e) {
            messageLabel.setText("Inserisci una quantità valida.");
        }
    }

    private void loadProducts() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();
            ObservableList<FlowerProduct> observableProducts = FXCollections.observableArrayList(products);
            productTable.setItems(observableProducts);
        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento prodotti.");
        }
    }

    private void refreshCart() {
        ObservableList<CartItem> observableCart =
                FXCollections.observableArrayList(customerCartController.getCartItems());

        cartTable.setItems(null);
        cartTable.setItems(observableCart);
        cartTable.refresh();

        totalLabel.setText("Totale carrello: € " + customerCartController.getCartTotal());
    }
    @FXML
    private void handleGoToCheckout() {
        if (customerCartController.isCartEmpty()) {
            messageLabel.setText("Il carrello è vuoto.");
            return;
        }

        try {
            CheckoutGraphicController.setCartController(customerCartController);

            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/checkout-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 800, 550);

            Stage stage = (Stage) productTable.getScene().getWindow();
            stage.setTitle("Shop Flowers - Checkout");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura del checkout.");
        }
    }
    @FXML
    private void handleRemoveFromCart() {
        if (selectedCartItem == null) {
            messageLabel.setText("Seleziona prima un articolo del carrello.");
            return;
        }

        customerCartController.removeFromCart(selectedCartItem.getProduct().getId());
        selectedCartItem = null;
        refreshCart();
        messageLabel.setText("Articolo rimosso dal carrello.");
    }

    @FXML
    private void handleClearCart() {
        if (customerCartController.isCartEmpty()) {
            messageLabel.setText("Il carrello è già vuoto.");
            return;
        }

        customerCartController.clearCart();
        selectedCartItem = null;
        refreshCart();
        messageLabel.setText("Carrello svuotato.");
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clearSession();

            FXMLLoader loader = new FXMLLoader(
                    ShopFlowersApplication.class.getResource("/com/example/shopflowers/login-view.fxml")
            );

            Scene scene = new Scene(loader.load(), 500, 350);

            Stage stage = (Stage) productTable.getScene().getWindow();
            stage.setTitle("Shop Flowers - Login");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }
    private CartItem selectedCartItem;
}