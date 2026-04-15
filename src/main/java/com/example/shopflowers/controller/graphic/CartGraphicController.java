package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.config.UiTitles;
import com.example.shopflowers.config.ViewPaths;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.ProductNotFoundException;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.util.AlertUtils;
import com.example.shopflowers.util.CartSession;
import com.example.shopflowers.util.CartTimerManager;
import com.example.shopflowers.util.CustomBouquetSession;
import com.example.shopflowers.util.ProductTableImageCellFactory;
import com.example.shopflowers.util.SceneNavigator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class CartGraphicController {

    private static final String NO_BOUQUET_MESSAGE = "Nessun bouquet personalizzato nel carrello.";
    private static final String CHECKOUT_ERROR_MESSAGE =
            "Si è verificato un errore durante l'apertura del checkout.";
    private static final String BACK_TO_CATALOG_ERROR_MESSAGE =
            "Si è verificato un errore durante il ritorno al catalogo.";

    @FXML
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, String> cartImageColumn;

    @FXML
    private TableColumn<CartItem, String> cartNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> cartQuantityColumn;

    @FXML
    private TableColumn<CartItem, Double> cartTotalColumn;

    @FXML
    private Label totalLabel;

    @FXML
    private Label bouquetInfoLabel;

    @FXML
    private Label cartTimerLabel;

    @FXML
    private Label messageLabel;

    private CartItem selectedCartItem;

    @FXML
    public void initialize() {
        configureCartTable();
        configureSelectionListener();
        configureTimerLabel();
        refreshCart();
    }

    @FXML
    private void handleRemoveSelected() {
        if (selectedCartItem == null) {
            messageLabel.setText("Seleziona prima un articolo dal carrello.");
            return;
        }

        boolean confirmed = AlertUtils.showConfirmation(
                "Conferma rimozione",
                "L'articolo selezionato verrà eliminato dal carrello corrente."
        );

        if (!confirmed) {
            messageLabel.setText("Operazione annullata.");
            return;
        }

        try {
            getCartController().removeFromCart(selectedCartItem.getProduct().getId());
            selectedCartItem = null;
            refreshCart();
            messageLabel.setText("Articolo rimosso dal carrello con successo.");
        } catch (ProductNotFoundException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleClearCart() {
        if (getCartController().isCartEmpty() && !CustomBouquetSession.hasBouquet()) {
            messageLabel.setText("Il carrello è già vuoto.");
            return;
        }

        boolean confirmed = AlertUtils.showConfirmation(
                "Conferma svuotamento",
                "Tutti gli articoli e l'eventuale bouquet verranno rimossi dal carrello."
        );

        if (!confirmed) {
            messageLabel.setText("Operazione annullata.");
            return;
        }

        try {
            clearCurrentCartState();
            refreshCart();
            messageLabel.setText("Carrello svuotato con successo.");
        } catch (EmptyCartException e) {
            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleGoToCheckout() {
        if (getCartController().isCartEmpty() && !CustomBouquetSession.hasBouquet()) {
            AlertUtils.showWarning(
                    "Checkout non disponibile",
                    "Il carrello e il bouquet personalizzato sono vuoti."
            );
            return;
        }

        CartTimerManager.startOrResetTimer();

        try {
            SceneNavigator.goTo(
                    (javafx.stage.Stage) cartTable.getScene().getWindow(),
                    ViewPaths.CHECKOUT_VIEW,
                    UiTitles.CHECKOUT
            );
        } catch (IOException e) {
            messageLabel.setText(CHECKOUT_ERROR_MESSAGE);
        }
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (javafx.stage.Stage) cartTable.getScene().getWindow(),
                    ViewPaths.CATALOG_VIEW,
                    UiTitles.CATALOG_CUSTOMER
            );
        } catch (IOException e) {
            messageLabel.setText(BACK_TO_CATALOG_ERROR_MESSAGE);
        }
    }

    private CustomerCartController getCartController() {
        return CartSession.getCartController();
    }

    private void configureCartTable() {
        cartImageColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProduct().getImageName())
        );
        cartImageColumn.setCellFactory(ProductTableImageCellFactory.create());

        cartNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    private void configureSelectionListener() {
        cartTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->
                selectedCartItem = newSelection);
    }

    private void configureTimerLabel() {
        cartTimerLabel.setText("Vai al checkout per attivare o proseguire il timer carrello.");
    }

    private void refreshCart() {
        cartTable.setItems(FXCollections.observableArrayList(getCartController().getCartItems()));
        cartTable.refresh();

        double total = getSafeCartTotal() + updateBouquetInfoAndGetTotal();
        totalLabel.setText(String.format("Totale carrello: € %.2f", total));
    }

    private double getSafeCartTotal() {
        try {
            return getCartController().getCartTotal();
        } catch (EmptyCartException e) {
            return 0.0;
        }
    }

    private double updateBouquetInfoAndGetTotal() {
        if (!CustomBouquetSession.hasBouquet()) {
            bouquetInfoLabel.setText(NO_BOUQUET_MESSAGE);
            return 0.0;
        }

        CustomBouquet bouquet = CustomBouquetSession.getCurrentBouquet();
        bouquetInfoLabel.setText(
                String.format(
                        "Bouquet personalizzato: %s | Totale bouquet: € %.2f",
                        bouquet.getDescription(),
                        bouquet.getTotalPrice()
                )
        );

        return bouquet.getTotalPrice();
    }

    private void clearCurrentCartState() throws EmptyCartException {
        if (!getCartController().isCartEmpty()) {
            getCartController().clearCart();
        }

        CustomBouquetSession.clear();
        selectedCartItem = null;
        CartTimerManager.stopTimer();
    }
    @FXML
    @SuppressWarnings("unused")
    private void handleLogout() {
        try {
            CartTimerManager.stopTimer();
            CartSession.resetCart();
            CustomBouquetSession.clear();
            SceneNavigator.logoutToLogin((Stage) cartTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText("Si è verificato un errore durante il logout.");
        }
    }
}