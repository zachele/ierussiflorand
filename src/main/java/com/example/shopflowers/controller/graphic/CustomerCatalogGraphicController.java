package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.config.UiTitles;
import com.example.shopflowers.config.ViewPaths;
import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InvalidQuantityException;
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
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CustomerCatalogGraphicController {

    private static final String SELECT_PRODUCT_MESSAGE = "Seleziona prima un prodotto dal catalogo.";
    private static final String INVALID_QUANTITY_MESSAGE = "Quantità non valida. Inserisci un numero corretto.";
    private static final String INVALID_POSITIVE_QUANTITY_MESSAGE =
            "Quantità non valida. Inserisci un valore maggiore di zero.";
    private static final String GUEST_ONLY_BROWSE_MESSAGE =
            "L'ospite può solo consultare il catalogo. Effettua il login o registrati per continuare.";

    private static final String CHECKOUT_ERROR_MESSAGE =
            "Si è verificato un errore durante l'apertura del checkout.";
    private static final String MY_ORDERS_ERROR_MESSAGE =
            "Si è verificato un errore durante l'apertura dello storico ordini.";
    private static final String COMPANY_INFO_ERROR_MESSAGE =
            "Si è verificato un errore durante l'apertura della pagina aziendale.";
    private static final String RECOMMENDATION_ERROR_MESSAGE =
            "Si è verificato un errore durante l'apertura dell'assistente bouquet.";
    private static final String CUSTOM_BOUQUET_ERROR_MESSAGE =
            "Si è verificato un errore durante l'apertura della schermata bouquet personalizzato.";
    private static final String CART_ERROR_MESSAGE =
            "Si è verificato un errore durante l'apertura del carrello.";
    private static final String LOGOUT_ERROR_MESSAGE =
            "Si è verificato un errore durante il logout.";

    @FXML
    private TableView<FlowerProduct> productTable;

    @FXML
    private TableColumn<FlowerProduct, String> imageColumn;

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
    private Label cartTimerLabel;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> colorFilterComboBox;

    @FXML
    private CheckBox availableOnlyCheckBox;

    private final BrowseCatalogController browseCatalogController = new BrowseCatalogController();

    private FilteredList<FlowerProduct> filteredProducts;
    private FlowerProduct selectedProduct;

    @FXML
    public void initialize() {
        configureProductTable();
        configureFilters();
        configureSelectionListeners();
        configureCartTimer();
        loadProducts();
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

            completeCartOperation();
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
                    ViewPaths.CHECKOUT_VIEW,
                    UiTitles.CHECKOUT
            );
        } catch (IOException e) {
            messageLabel.setText(CHECKOUT_ERROR_MESSAGE);
        }
    }

    @FXML
    private void handleMyOrders() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        goToScene(
                ViewPaths.CUSTOMER_ORDERS_VIEW,
                UiTitles.CUSTOMER_ORDERS,
                MY_ORDERS_ERROR_MESSAGE
        );
    }

    @FXML
    private void handleCompanyInfo() {
        goToScene(
                ViewPaths.COMPANY_INFO_VIEW,
                UiTitles.COMPANY_INFO,
                COMPANY_INFO_ERROR_MESSAGE
        );
    }

    @FXML
    private void handleRecommendationAssistant() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        goToScene(
                ViewPaths.RECOMMENDATION_VIEW,
                UiTitles.RECOMMENDATION_ASSISTANT,
                RECOMMENDATION_ERROR_MESSAGE
        );
    }

    @FXML
    private void handleCustomBouquet() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        goToScene(
                ViewPaths.CUSTOM_BOUQUET_VIEW,
                UiTitles.CUSTOM_BOUQUET,
                CUSTOM_BOUQUET_ERROR_MESSAGE
        );
    }

    @FXML
    private void handleGoToCart() {
        if (isGuestUser()) {
            messageLabel.setText(GUEST_ONLY_BROWSE_MESSAGE);
            return;
        }

        goToScene(
                ViewPaths.CART_VIEW,
                UiTitles.CART,
                CART_ERROR_MESSAGE
        );
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleLogout() {
        try {
            CartTimerManager.stopTimer();
            resetCartTimerDisplay();
            CartSession.resetCart();
            CustomBouquetSession.clear();
            SceneNavigator.logoutToLogin((Stage) productTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText(LOGOUT_ERROR_MESSAGE);
        }
    }

    private CustomerCartController getCartController() {
        return CartSession.getCartController();
    }

    private void configureProductTable() {
        ProductTableUtils.configureProductTableWithImage(
                imageColumn,
                idColumn,
                nameColumn,
                priceColumn,
                colorColumn,
                varietyColumn,
                stockColumn
        );
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
            CartSession.resetCart();
            stopAndResetCartTimer();
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
        cartTimerLabel.setText("Modalità ospite");
        messageLabel.setText("Accesso come ospite attivo: puoi solo consultare il catalogo.");
    }

    private boolean isGuestUser() {

        String role =
                Session.getInstance().getLoggedRole();

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

    private void completeCartOperation() {
        CartTimerManager.startOrResetTimer();
        messageLabel.setText("Articolo aggiunto al carrello con successo.");
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