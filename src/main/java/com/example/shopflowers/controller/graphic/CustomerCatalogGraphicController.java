package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.AlertUtils;
import com.example.shopflowers.util.CartTimerManager;
import com.example.shopflowers.util.CustomBouquetSession;
import com.example.shopflowers.util.ProductFilterUtils;
import com.example.shopflowers.util.ProductTableUtils;
import com.example.shopflowers.util.SceneNavigator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
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

import com.example.shopflowers.util.ProductFilterUIUtils;

public class CustomerCatalogGraphicController {

    private static final String SELECT_PRODUCT_MESSAGE = "Seleziona prima un prodotto dal catalogo.";
    private static final String SELECT_CART_ITEM_MESSAGE = "Seleziona prima un articolo dal carrello.";
    private static final String INVALID_QUANTITY_MESSAGE = "Quantità non valida. Inserisci un numero corretto.";
    private static final String INVALID_POSITIVE_QUANTITY_MESSAGE =
            "Quantità non valida. Inserisci un valore maggiore di zero.";

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

    @FXML
    private Label cartTimerLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> colorFilterComboBox;

    @FXML
    private CheckBox availableOnlyCheckBox;

    private final BrowseCatalogController browseCatalogController = new BrowseCatalogController();
    private static final CustomerCartController customerCartController = new CustomerCartController();

    private FilteredList<FlowerProduct> filteredProducts;
    private FlowerProduct selectedProduct;
    private CartItem selectedCartItem;

    public static CustomerCartController getSharedCartController() {
        return customerCartController;
    }

    @FXML
    public void initialize() {
        configureProductTable();
        configureCartTable();
        configureFilters();
        configureSelectionListeners();
        configureCartTimer();
        loadProducts();
        refreshCart();
    }

    @FXML
    private void handleAddToCart() {
        if (selectedProduct == null) {
            messageLabel.setText(SELECT_PRODUCT_MESSAGE);
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity <= 0) {
                messageLabel.setText(INVALID_POSITIVE_QUANTITY_MESSAGE);
                return;
            }

            if (!customerCartController.addToCart(selectedProduct, quantity)) {
                messageLabel.setText("Operazione non riuscita. Quantità richiesta non disponibile.");
                return;
            }

            completeCartOperation("Articolo aggiunto al carrello con successo.");
            quantityField.clear();

        } catch (NumberFormatException e) {
            messageLabel.setText(INVALID_QUANTITY_MESSAGE);
        }
    }

    @FXML
    private void handleGoToCheckout() {
        if (customerCartController.isCartEmpty() && !CustomBouquetSession.hasBouquet()) {
            AlertUtils.showWarning(
                    "Checkout non disponibile",
                    "Il carrello e il bouquet personalizzato sono vuoti."
            );
            return;
        }

        try {
            CartTimerManager.startOrResetTimer();
            CheckoutGraphicController.setCartController(customerCartController);
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    "/com/example/shopflowers/checkout-view.fxml",
                    "Shop Flowers - Checkout"
            );
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante l'apertura del checkout.");
        }
    }

    @FXML
    private void handleRemoveFromCart() {
        if (selectedCartItem == null) {
            messageLabel.setText(SELECT_CART_ITEM_MESSAGE);
            return;
        }
        boolean confirmed = AlertUtils.showConfirmation(
                "Conferma rimozione",
                "L'articolo selezionato verrà eliminato dal carrello corrente."
        );

        if (confirmed) {
            customerCartController.removeFromCart(selectedCartItem.getProduct().getId());
            selectedCartItem = null;
            completeCartOperation("Articolo rimosso dal carrello con successo.");
        } else {
            messageLabel.setText("Operazione annullata.");
        }
    }

    @FXML
    private void handleClearCart() {
        if (customerCartController.isCartEmpty()) {
            messageLabel.setText("Il carrello è già vuoto.");
            return;
        }

        boolean confirmed = AlertUtils.showConfirmation(
                "Conferma svuotamento",
                "Tutti gli articoli verranno rimossi dal carrello."
        );

        if (confirmed) {
            customerCartController.clearCart();
            selectedCartItem = null;
            completeCartOperation("Carrello svuotato con successo.");
        } else {
            messageLabel.setText("Operazione annullata.");
        }
    }

    @FXML
    private void handleMyOrders() {
        goToScene(
                "/com/example/shopflowers/customer-orders-view.fxml",
                "Shop Flowers - I miei ordini",
                "Si è verificato un errore durante l'apertura dello storico ordini."
        );
    }

    @FXML
    private void handleCompanyInfo() {
        goToScene(
                "/com/example/shopflowers/company-info-view.fxml",
                "Shop Flowers - Informazioni Azienda",
                "Si è verificato un errore durante l'apertura della pagina aziendale."
        );
    }

    @FXML
    private void handleRecommendationAssistant() {
        goToScene(
                "/com/example/shopflowers/recommendation-view.fxml",
                "Shop Flowers - Assistente Bouquet",
                "Si è verificato un errore durante l'apertura dell'assistente bouquet."
        );
    }

    @FXML
    private void handleCustomBouquet() {
        goToScene(
                "/com/example/shopflowers/custom-bouquet-view.fxml",
                "Shop Flowers - Bouquet Personalizzato",
                "Si è verificato un errore durante l'apertura della schermata bouquet personalizzato."
        );
    }

    @FXML
    private void handleLogout() {
        try {
            CartTimerManager.stopTimer();
            SceneNavigator.logoutToLogin((Stage) productTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }

    private void configureProductTable() {
        ProductTableUtils.configureProductTable(
                idColumn,
                nameColumn,
                priceColumn,
                colorColumn,
                varietyColumn,
                stockColumn
        );
    }

    private void configureCartTable() {
        cartNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    private void configureFilters() {
        ProductFilterUIUtils.configureColorFilter(colorFilterComboBox);
        ProductFilterUIUtils.bindFilterListeners(
                searchField,
                colorFilterComboBox,
                availableOnlyCheckBox,
                this::applyFilters
        );
    }

    private void configureSelectionListeners() {
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                selectedProduct = newSelection);

        cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                selectedCartItem = newSelection);
    }

    private void configureCartTimer() {
        CartTimerManager.setOnTickAction(seconds -> Platform.runLater(() ->
                cartTimerLabel.setText(formatRemainingTime(seconds))
        ));

        CartTimerManager.setOnTimeoutAction(() -> Platform.runLater(() -> {
            customerCartController.clearCart();
            CustomBouquetSession.clear();
            selectedCartItem = null;
            refreshCart();
            messageLabel.setText("Sessione carrello scaduta: il carrello è stato svuotato per inattività.");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Carrello svuotato");
            alert.setHeaderText("Timeout del carrello");
            alert.setContentText("Il carrello è stato svuotato automaticamente per inattività.");
            alert.showAndWait();
        }));

        cartTimerLabel.setText(formatRemainingTime(600));
    }

    private void loadProducts() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();
            filteredProducts = ProductTableUtils.loadProductsIntoTable(productTable, products);
            applyFilters();
        } catch (SQLException e) {
            messageLabel.setText("Si è verificato un errore durante il caricamento dei prodotti.");
        }
    }

    private void applyFilters() {
        if (filteredProducts == null) {
            return;
        }

        String searchText = searchField.getText();
        String selectedColor = colorFilterComboBox.getValue();
        boolean availableOnly = availableOnlyCheckBox.isSelected();

        filteredProducts.setPredicate(product ->
                ProductFilterUtils.matchesProductFilters(
                        product,
                        searchText,
                        selectedColor,
                        availableOnly
                )
        );
    }

    private void refreshCart() {
        cartTable.setItems(FXCollections.observableArrayList(customerCartController.getCartItems()));
        cartTable.refresh();
        totalLabel.setText(String.format("Totale carrello: € %.2f", customerCartController.getCartTotal()));
    }

    private void completeCartOperation(String successMessage) {
        CartTimerManager.startOrResetTimer();
        refreshCart();
        messageLabel.setText(successMessage);
    }

    private String formatRemainingTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("Timer carrello: %02d:%02d", minutes, seconds);
    }

    private void goToScene(String fxmlPath, String title, String errorMessage) {
        try {
            SceneNavigator.goTo(
                    (Stage) productTable.getScene().getWindow(),
                    fxmlPath,
                    title
            );
        } catch (IOException e) {
            messageLabel.setText(errorMessage);
        }
    }
}