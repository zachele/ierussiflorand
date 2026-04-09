package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InvalidQuantityException;
import com.example.shopflowers.exception.ProductNotFoundException;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.AlertUtils;
import com.example.shopflowers.util.CartSession;
import com.example.shopflowers.util.CartTimerManager;
import com.example.shopflowers.util.CustomBouquetSession;
import com.example.shopflowers.util.ProductFilterUIUtils;
import com.example.shopflowers.util.ProductFilterUtils;
import com.example.shopflowers.util.ProductTableUtils;
import com.example.shopflowers.util.SceneNavigator;
import com.example.shopflowers.util.Session;
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

public class CustomerCatalogGraphicController {

    private static final String SELECT_PRODUCT_MESSAGE = "Seleziona prima un prodotto dal catalogo.";
    private static final String SELECT_CART_ITEM_MESSAGE = "Seleziona prima un articolo dal carrello.";
    private static final String INVALID_QUANTITY_MESSAGE = "Quantità non valida. Inserisci un numero corretto.";
    private static final String INVALID_POSITIVE_QUANTITY_MESSAGE =
            "Quantità non valida. Inserisci un valore maggiore di zero.";
    private static final String GUEST_ONLY_BROWSE_MESSAGE =
            "L'ospite può solo consultare il catalogo. Effettua il login o registrati per continuare.";
    private static final String NO_BOUQUET_MESSAGE = "Nessun bouquet personalizzato nel carrello.";

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
    private Label bouquetInfoLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> colorFilterComboBox;

    @FXML
    private CheckBox availableOnlyCheckBox;

    private final BrowseCatalogController browseCatalogController = new BrowseCatalogController();

    private FilteredList<FlowerProduct> filteredProducts;
    private FlowerProduct selectedProduct;
    private CartItem selectedCartItem;

    @FXML
    public void initialize() {
        configureProductTable();
        configureCartTable();
        configureFilters();
        configureSelectionListeners();
        configureCartTimer();
        loadProducts();
        refreshCart();
        configureGuestMode();
    }

    @FXML
    private void handleAddToCart() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

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

            if (!getCartController().addToCart(selectedProduct, quantity)) {
                messageLabel.setText("Operazione non riuscita. Quantità richiesta non disponibile.");
                return;
            }

            completeCartOperation("Articolo aggiunto al carrello con successo.");
            quantityField.clear();

        } catch (NumberFormatException e) {
            messageLabel.setText(INVALID_QUANTITY_MESSAGE);
        } catch (InvalidQuantityException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleGoToCheckout() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        if (getCartController().isCartEmpty() && !CustomBouquetSession.hasBouquet()) {
            AlertUtils.showWarning(
                    "Checkout non disponibile",
                    "Il carrello e il bouquet personalizzato sono vuoti."
            );
            return;
        }

        try {
            CartTimerManager.startOrResetTimer();
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
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        if (selectedCartItem == null) {
            messageLabel.setText(SELECT_CART_ITEM_MESSAGE);
            return;
        }

        boolean confirmed = AlertUtils.showConfirmation(
                "Conferma rimozione",
                "L'articolo selezionato verrà eliminato dal carrello corrente."
        );

        if (confirmed) {
            try {
                getCartController().removeFromCart(selectedCartItem.getProduct().getId());
                selectedCartItem = null;
                completeCartOperation("Articolo rimosso dal carrello con successo.");
            } catch (ProductNotFoundException e) {
                messageLabel.setText(e.getMessage());
            }
        } else {
            messageLabel.setText("Operazione annullata.");
        }
    }

    @FXML
    private void handleClearCart() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        if (getCartController().isCartEmpty() && !CustomBouquetSession.hasBouquet()) {
            messageLabel.setText("Il carrello è già vuoto.");
            resetCartTimerDisplay();
            return;
        }

        boolean confirmed = AlertUtils.showConfirmation(
                "Conferma svuotamento",
                "Tutti gli articoli e l'eventuale bouquet verranno rimossi dal carrello."
        );

        if (confirmed) {
            try {
                if (!getCartController().isCartEmpty()) {
                    getCartController().clearCart();
                }
                CustomBouquetSession.clear();
                selectedCartItem = null;
                stopAndResetCartTimer();
                refreshCart();
                messageLabel.setText("Carrello svuotato con successo.");
            } catch (EmptyCartException e) {
                messageLabel.setText(e.getMessage());
            }
        } else {
            messageLabel.setText("Operazione annullata.");
        }
    }

    @FXML
    private void handleMyOrders() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

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
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        goToScene(
                "/com/example/shopflowers/recommendation-view.fxml",
                "Shop Flowers - Assistente Bouquet",
                "Si è verificato un errore durante l'apertura dell'assistente bouquet."
        );
    }

    @FXML
    private void handleCustomBouquet() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

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
            resetCartTimerDisplay();
            CartSession.resetCart();
            CustomBouquetSession.clear();
            SceneNavigator.logoutToLogin((Stage) productTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }

    private CustomerCartController getCartController() {
        return CartSession.getCartController();
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
            try {
                getCartController().clearCart();
            } catch (EmptyCartException ignored) {
                // nessuna azione necessaria
            }

            CustomBouquetSession.clear();
            selectedCartItem = null;
            CartSession.resetCart();
            stopAndResetCartTimer();
            refreshCart();
            messageLabel.setText("Sessione carrello scaduta: il carrello è stato svuotato per inattività.");

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Carrello svuotato");
            alert.setHeaderText("Timeout del carrello");
            alert.setContentText("Il carrello è stato svuotato automaticamente per inattività.");
            alert.showAndWait();
        }));

        resetCartTimerDisplay();
    }

    private void configureGuestMode() {
        if (!isGuestUser()) {
            return;
        }

        quantityField.setDisable(true);
        cartTable.setDisable(true);
        totalLabel.setText("Modalità ospite: acquisto non disponibile.");
        cartTimerLabel.setText("Modalità ospite");
        bouquetInfoLabel.setText(NO_BOUQUET_MESSAGE);
        messageLabel.setText("Accesso come ospite attivo: puoi solo consultare il catalogo.");
    }

    private boolean isGuestUser() {
        String role = Session.getInstance().getLoggedRole();
        return "GUEST".equalsIgnoreCase(role);
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
        ProductFilterUtils.applyProductFilters(
                filteredProducts,
                searchField.getText(),
                colorFilterComboBox.getValue(),
                availableOnlyCheckBox.isSelected()
        );
    }

    private void refreshCart() {
        cartTable.setItems(FXCollections.observableArrayList(getCartController().getCartItems()));
        cartTable.refresh();

        double total = 0.0;

        try {
            total = getCartController().getCartTotal();
        } catch (EmptyCartException ignored) {
            total = 0.0;
        }

        if (CustomBouquetSession.hasBouquet()) {
            CustomBouquet bouquet = CustomBouquetSession.getCurrentBouquet();
            bouquetInfoLabel.setText(
                    String.format(
                            "Bouquet personalizzato: %s | Totale bouquet: € %.2f",
                            bouquet.getDescription(),
                            bouquet.getTotalPrice()
                    )
            );
            total += bouquet.getTotalPrice();
        } else {
            bouquetInfoLabel.setText(NO_BOUQUET_MESSAGE);
        }

        totalLabel.setText(String.format("Totale carrello: € %.2f", total));

        if (getCartController().isCartEmpty() && !CustomBouquetSession.hasBouquet()) {
            stopAndResetCartTimer();
        }
    }

    private void completeCartOperation(String successMessage) {
        CartTimerManager.startOrResetTimer();
        refreshCart();
        messageLabel.setText(successMessage);
    }

    private void stopAndResetCartTimer() {
        CartTimerManager.stopTimer();
        resetCartTimerDisplay();
    }

    private void resetCartTimerDisplay() {
        cartTimerLabel.setText(formatRemainingTime(600));
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