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
import javafx.stage.Stage;
import java.io.IOException;
import com.example.shopflowers.util.SceneNavigator;

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

            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/checkout-view.fxml",
                    "Shop Flowers - Checkout",
                    800,
                    550
            );

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
            SceneNavigator.logoutToLogin((Stage) productTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Errore durante il logout.");
        }
    }
    @FXML
    private void handleMyOrders() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/customer-orders-view.fxml",
                    "Shop Flowers - I miei ordini",
                    1000,
                    650
            );

        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura dello storico ordini.");
        }
    }
    @FXML
    private void handleCompanyInfo() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/company-info-view.fxml",
                    "Shop Flowers - Informazioni Azienda",
                    900,
                    650
            );

        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura della pagina azienda.");
        }
    }
    private CartItem selectedCartItem;
}