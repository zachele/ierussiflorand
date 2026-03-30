package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.controller.application.BrowseCatalogController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.util.CartTimerManager;
import com.example.shopflowers.util.CustomBouquetSession;
import com.example.shopflowers.util.SceneNavigator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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

    private ObservableList<FlowerProduct> masterProductList = FXCollections.observableArrayList();
    private FilteredList<FlowerProduct> filteredProducts;

    private FlowerProduct selectedProduct;
    private CartItem selectedCartItem;

    public static CustomerCartController getSharedCartController() {
        return customerCartController;
    }

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

        colorFilterComboBox.setItems(FXCollections.observableArrayList(
                "Tutti", "Rosso", "Bianco", "Rosa", "Giallo", "Misto"
        ));
        colorFilterComboBox.setValue("Tutti");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        colorFilterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        availableOnlyCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                selectedProduct = newSelection);

        cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                selectedCartItem = newSelection);

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

        loadProducts();
        refreshCart();
    }

    @FXML
    private void handleAddToCart() {
        if (selectedProduct == null) {
            messageLabel.setText("Seleziona prima un prodotto dal catalogo.");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity <= 0) {
                messageLabel.setText("Quantità non valida. Inserisci un valore maggiore di zero.");
                return;
            }

            boolean added = customerCartController.addToCart(selectedProduct, quantity);

            if (!added) {
                messageLabel.setText("Operazione non riuscita. Quantità richiesta non disponibile.");
                return;
            }

            CartTimerManager.startOrResetTimer();
            messageLabel.setText("Articolo aggiunto al carrello con successo.");
            quantityField.clear();
            refreshCart();

        } catch (NumberFormatException e) {
            messageLabel.setText("Quantità non valida. Inserisci un numero corretto.");
        }
    }

    private void loadProducts() {
        try {
            List<FlowerProduct> products = browseCatalogController.getAllProducts();
            masterProductList = FXCollections.observableArrayList(products);
            filteredProducts = new FilteredList<>(masterProductList, product -> true);

            SortedList<FlowerProduct> sortedProducts = new SortedList<>(filteredProducts);
            sortedProducts.comparatorProperty().bind(productTable.comparatorProperty());

            productTable.setItems(sortedProducts);
            applyFilters();

        } catch (SQLException e) {
            messageLabel.setText("Errore nel caricamento prodotti.");
        }
    }

    private void applyFilters() {
        if (filteredProducts == null) {
            return;
        }

        String searchText = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        String selectedColor = colorFilterComboBox.getValue();
        boolean availableOnly = availableOnlyCheckBox.isSelected();

        filteredProducts.setPredicate(product -> {
            if (product == null) {
                return false;
            }

            boolean matchesSearch = searchText.isBlank()
                    || safe(product.getName()).contains(searchText)
                    || safe(product.getColor()).contains(searchText)
                    || safe(product.getVariety()).contains(searchText);

            boolean matchesColor = selectedColor == null
                    || selectedColor.equalsIgnoreCase("Tutti")
                    || safe(product.getColor()).contains(selectedColor.toLowerCase());

            boolean matchesAvailability = !availableOnly || product.getStockQuantity() > 0;

            return matchesSearch && matchesColor && matchesAvailability;
        });
    }

    private String safe(String value) {
        return value == null ? "" : value.toLowerCase();
    }

    private void refreshCart() {
        ObservableList<CartItem> observableCart =
                FXCollections.observableArrayList(customerCartController.getCartItems());

        cartTable.setItems(observableCart);
        cartTable.refresh();

        totalLabel.setText(String.format("Totale carrello: € %.2f", customerCartController.getCartTotal()));
    }

    private String formatRemainingTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("Timer carrello: %02d:%02d", minutes, seconds);
    }

    @FXML
    private void handleGoToCheckout() {
        if (customerCartController.isCartEmpty() && !CustomBouquetSession.hasBouquet()) {
            messageLabel.setText("Checkout non disponibile. Il carrello e il bouquet personalizzato sono vuoti.");
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
            messageLabel.setText("Seleziona prima un articolo dal carrello.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere l'articolo selezionato dal carrello?");
        alert.setContentText("L'articolo verrà eliminato dal carrello corrente.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Operazione annullata.");
            return;
        }

        customerCartController.removeFromCart(selectedCartItem.getProduct().getId());
        selectedCartItem = null;
        CartTimerManager.startOrResetTimer();
        refreshCart();
        messageLabel.setText("Articolo rimosso dal carrello con successo.");
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
            messageLabel.setText("Operazione annullata.");
            return;
        }

        customerCartController.clearCart();
        selectedCartItem = null;
        CartTimerManager.startOrResetTimer();
        refreshCart();
        messageLabel.setText("Carrello svuotato con successo.");
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
            messageLabel.setText("Si è verificato un errore durante l'apertura dello storico ordini.");
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
            messageLabel.setText("Si è verificato un errore durante l'apertura della pagina aziendale.");
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
            messageLabel.setText("Si è verificato un errore durante l'apertura dell'assistente bouquet.");
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
            messageLabel.setText("Si è verificato un errore durante l'apertura della schermata bouquet personalizzato.");
        }
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
}