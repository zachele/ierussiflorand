package com.example.shopflowers.controller.graphic;

import com.example.shopflowers.config.DeliveryModes;
import com.example.shopflowers.config.UiTitles;
import com.example.shopflowers.config.ViewPaths;
import com.example.shopflowers.controller.application.CheckoutController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InsufficientStockException;
import com.example.shopflowers.model.bean.CheckoutBean;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.util.AlertUtils;
import com.example.shopflowers.util.CartSession;
import com.example.shopflowers.util.CartTimerManager;
import com.example.shopflowers.util.CustomBouquetSession;
import com.example.shopflowers.util.SceneNavigator;
import com.example.shopflowers.util.Session;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import com.example.shopflowers.exception.InvalidDeliveryAddressException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import com.example.shopflowers.config.UiMessages;
import com.example.shopflowers.exception.PaymentFailedException;

public class CheckoutGraphicController {

    private static final String NO_BOUQUET_MESSAGE = "Nessun bouquet personalizzato nel pagamento corrente.";
    private static final String CHECKOUT_ERROR_MESSAGE = "Si è verificato un errore durante la conferma dell'ordine.";
    private static final String BACK_TO_CATALOG_ERROR_MESSAGE =
            "Si è verificato un errore durante il ritorno al catalogo.";
    private static final String LOGOUT_ERROR_MESSAGE =
            "Si è verificato un errore durante il logout.";

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
    private TextField addressField;

    @FXML
    private Label messageLabel;

    @FXML
    private DatePicker pickupDatePicker;

    @FXML
    private ComboBox<String> pickupTimeComboBox;

    @FXML
    private Label bouquetInfoLabel;

    private final CheckoutController checkoutController = new CheckoutController();

    @FXML
    public void initialize() {
        configureCheckoutTable();
        configureDeliveryOptions();
        loadCheckoutData();
    }

    @FXML
    private void handleConfirmOrder() {
        if (!isCheckoutAvailable()) {
            return;
        }

        CheckoutBean checkoutBean = buildCheckoutBean();
        if (!validateCheckoutBean(checkoutBean)) {
            return;
        }

        try {
            Order order = checkoutController.createOrder(
                    checkoutBean,
                    getCartController() != null ? getCartController().getCartItems() : Collections.emptyList()
            );

            checkoutController.confirmOrder(order);
            clearCheckoutAfterSuccess();

            AlertUtils.showInfo(
                    "Ordine confermato",
                    "Ordine confermato con successo."
            );

        } catch (EmptyCartException
                 | InsufficientStockException
                 | PaymentFailedException
                 | InvalidDeliveryAddressException e) {

            messageLabel.setText(e.getMessage());

        } catch (SQLException e) {
            messageLabel.setText(CHECKOUT_ERROR_MESSAGE);
        }
    }

    @FXML
    private void handleBackToCatalog() {
        try {
            SceneNavigator.goTo(
                    (Stage) checkoutTable.getScene().getWindow(),
                    ViewPaths.CATALOG_VIEW,
                    UiTitles.CATALOG_CUSTOMER
            );
        } catch (IOException e) {
            messageLabel.setText(BACK_TO_CATALOG_ERROR_MESSAGE);
        }
    }

    @FXML
    private void handleRemoveBouquet() {
        if (!CustomBouquetSession.hasBouquet()) {
            messageLabel.setText("Nessun bouquet presente nel pagamento corrente.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Conferma rimozione");
        alert.setHeaderText("Rimuovere il bouquet dal pagamento corrente?");
        alert.setContentText("Il bouquet personalizzato verrà eliminato dal checkout.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            messageLabel.setText("Operazione annullata.");
            return;
        }

        CustomBouquetSession.clear();
        bouquetInfoLabel.setText(NO_BOUQUET_MESSAGE);

        double total = getSafeCartTotal();
        totalLabel.setText(UiMessages.formatOrderTotal(total));
        messageLabel.setText("Bouquet rimosso dal pagamento corrente con successo.");
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleLogout() {
        try {
            CartTimerManager.stopTimer();
            CartSession.resetCart();
            CustomBouquetSession.clear();
            SceneNavigator.logoutToLogin((Stage) checkoutTable.getScene().getWindow());
        } catch (IOException e) {
            messageLabel.setText(LOGOUT_ERROR_MESSAGE);
        }
    }

    private CustomerCartController getCartController() {
        return CartSession.getCartController();
    }

    private void configureCheckoutTable() {
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }

    private void configureDeliveryOptions() {
        deliveryModeComboBox.setItems(
                FXCollections.observableArrayList(
                        DeliveryModes.DELIVERY,
                        DeliveryModes.PICKUP
                )
        );

        pickupTimeComboBox.setItems(FXCollections.observableArrayList(
                "09:00", "09:30", "10:00", "10:30",
                "11:00", "11:30", "12:00", "12:30",
                "16:00", "16:30", "17:00", "17:30",
                "18:00", "18:30", "19:00"
        ));

        setDeliveryFieldsState(true);
        setPickupFieldsState(true);

        deliveryModeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> updateDeliveryModeUi(newValue));
    }

    private void updateDeliveryModeUi(String deliveryMode) {
        if (DeliveryModes.DELIVERY.equals(deliveryMode)) {
            setDeliveryFieldsState(false);
            setPickupFieldsState(true);
            pickupDatePicker.setValue(null);
            pickupTimeComboBox.setValue(null);
            return;
        }

        if (DeliveryModes.PICKUP.equals(deliveryMode)) {
            setDeliveryFieldsState(true);
            addressField.clear();
            setPickupFieldsState(false);
        }
    }

    private void setDeliveryFieldsState(boolean disabled) {
        addressField.setDisable(disabled);
    }

    private void setPickupFieldsState(boolean disabled) {
        pickupDatePicker.setDisable(disabled);
        pickupTimeComboBox.setDisable(disabled);
    }

    private void loadCheckoutData() {
        if (getCartController() == null) {
            return;
        }

        checkoutTable.setItems(FXCollections.observableArrayList(getCartController().getCartItems()));

        double total = getSafeCartTotal();
        total += updateBouquetInfoAndGetTotal();

        totalLabel.setText(UiMessages.formatOrderTotal(total));
    }

    private boolean isCheckoutAvailable() {
        if ((getCartController() == null || getCartController().isCartEmpty()) && !CustomBouquetSession.hasBouquet()) {
            AlertUtils.showWarning(
                    "Ordine non confermato",
                    "Il carrello e il bouquet personalizzato sono vuoti."
            );
            return false;
        }
        return true;
    }

    private CheckoutBean buildCheckoutBean() {
        CheckoutBean checkoutBean = new CheckoutBean();
        checkoutBean.setUsername(Session.getInstance().getLoggedUsername());
        checkoutBean.setDeliveryMode(deliveryModeComboBox.getValue());
        checkoutBean.setPaymentMethod(paymentField.getText());
        checkoutBean.setDeliveryAddress(addressField.getText());
        checkoutBean.setPickupDate(null);
        checkoutBean.setPickupTime(null);
        return checkoutBean;
    }

    private boolean validateCheckoutBean(CheckoutBean checkoutBean) {
        if (checkoutBean.getDeliveryMode() == null
                || checkoutBean.getPaymentMethod() == null
                || checkoutBean.getPaymentMethod().isBlank()) {
            messageLabel.setText("Compila tutti i campi del checkout.");
            return false;
        }

        if (DeliveryModes.DELIVERY.equals(checkoutBean.getDeliveryMode())) {
            return validateDeliveryAddress(checkoutBean);
        }

        if (DeliveryModes.PICKUP.equals(checkoutBean.getDeliveryMode())) {
            return validatePickupData(checkoutBean);
        }

        return true;
    }

    private boolean validateDeliveryAddress(CheckoutBean checkoutBean) {
        if (checkoutBean.getDeliveryAddress() == null || checkoutBean.getDeliveryAddress().isBlank()) {
            messageLabel.setText("Inserisci l'indirizzo di consegna.");
            return false;
        }
        return true;
    }

    private boolean validatePickupData(CheckoutBean checkoutBean) {
        if (pickupDatePicker.getValue() == null || pickupTimeComboBox.getValue() == null) {
            messageLabel.setText("Seleziona giorno e ora del ritiro.");
            return false;
        }

        checkoutBean.setPickupDate(pickupDatePicker.getValue().toString());
        checkoutBean.setPickupTime(pickupTimeComboBox.getValue());
        checkoutBean.setDeliveryAddress(null);
        return true;
    }

    private void clearCheckoutAfterSuccess() {
        if (getCartController() != null) {
            try {
                getCartController().clearCart();
            } catch (EmptyCartException ignored) {
                // nessuna azione necessaria
            }
        }

        CartTimerManager.stopTimer();
        CartSession.resetCart();

        checkoutTable.getItems().clear();
        CustomBouquetSession.clear();
        bouquetInfoLabel.setText(NO_BOUQUET_MESSAGE);
        totalLabel.setText(UiMessages.formatOrderTotal(0.0));
        addressField.clear();
        pickupDatePicker.setValue(null);
        pickupTimeComboBox.setValue(null);
        paymentField.clear();
    }

    private double getSafeCartTotal() {
        if (getCartController() == null) {
            return 0.0;
        }

        try {
            return getCartController().getCartTotal();
        } catch (EmptyCartException e) {
            return 0.0;
        }
    }

    private double updateBouquetInfoAndGetTotal() {
        if (!CustomBouquetSession.hasBouquet()) {
            CustomBouquetSession.clear();
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
}