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


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;

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
    public static CustomerCartController getSharedCartController() {
        return customerCartController;
    }
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

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
            selectedProduct = newSelection);
        cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
            selectedCartItem = newSelection);

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
                    "Shop Flowers - Checkout"
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere l'articolo selezionato dal carrello?");
        alert.setContentText("L'articolo verrà eliminato dal carrello corrente.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Rimozione annullata.");
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

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma svuotamento");
        alert.setHeaderText("Svuotare tutto il carrello?");
        alert.setContentText("Tutti gli articoli verranno rimossi dal carrello.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Svuotamento annullato.");
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
                    "Shop Flowers - I miei ordini"
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
                    "Shop Flowers - Informazioni Azienda"
            );

        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura della pagina azienda.");
        }
    }
    @FXML
    private void handleRecommendationAssistant() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/recommendation-view.fxml",
                    "Shop Flowers - Assistente Bouquet"
            );
        } catch (IOException e) {
            messageLabel.setText("Errore nell'apertura dell'assistente bouquet.");
        }
    }
    @FXML
    private void handleCustomBouquet() {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/custom-bouquet-view.fxml",
                    "Shop Flowers - Bouquet Personalizzato"
            );
        } catch (Exception e) {
            messageLabel.setText("Errore nell'apertura del bouquet personalizzato.");
        }
    }

    private CartItem selectedCartItem;
}