package com.example.shopflowers.cli;

import com.example.shopflowers.controller.application.CheckoutController;
import com.example.shopflowers.controller.application.CustomerCartController;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InsufficientStockException;
import com.example.shopflowers.exception.PaymentFailedException;
import com.example.shopflowers.model.bean.CheckoutBean;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.util.Session;

import java.sql.SQLException;
import java.util.Scanner;
import com.example.shopflowers.exception.InvalidDeliveryAddressException;

public class ConsoleCheckoutUI {

    private static final String DELIVERY_MODE = "CONSEGNA";
    private static final String PICKUP_MODE = "RITIRO";

    private final Scanner scanner;
    private final CustomerCartController customerCartController;
    private final CheckoutController checkoutController;

    public ConsoleCheckoutUI(Scanner scanner, CustomerCartController customerCartController) {
        this.scanner = scanner;
        this.customerCartController = customerCartController;
        this.checkoutController = new CheckoutController();
    }

    public void start() {
        ConsolePrinter.println();
        ConsolePrinter.println("============= CHECKOUT CLI =============");

        if (customerCartController.isCartEmpty()) {
            ConsolePrinter.println("Il carrello è vuoto.");
            return;
        }

        try {
            ConsolePrinter.println("Totale carrello: € "
                    + String.format("%.2f", customerCartController.getCartTotal()));
        } catch (EmptyCartException e) {
            ConsolePrinter.println("Il carrello è vuoto.");
            return;
        }

        CheckoutBean checkoutBean = buildCheckoutBean();
        if (checkoutBean == null) {
            return;
        }

        try {
            Order order = checkoutController.createOrder(
                    checkoutBean,
                    customerCartController.getCartItems()
            );

            checkoutController.confirmOrder(order);
            customerCartController.clearCart();

            ConsolePrinter.println("Ordine confermato con successo.");

        } catch (EmptyCartException
         | InsufficientStockException
         | PaymentFailedException
         | InvalidDeliveryAddressException e) {
            ConsolePrinter.println("Errore: " + e.getMessage());
        } catch (SQLException e) {
            ConsolePrinter.println("Errore durante la conferma dell'ordine.");
        }
    }

    private CheckoutBean buildCheckoutBean() {

        CheckoutBean checkoutBean = new CheckoutBean();

        checkoutBean.setUsername(
                Session.getInstance().getLoggedUsername()
        );

        ConsolePrinter.print(
                "Metodo di consegna (CONSEGNA / RITIRO): "
        );

        String deliveryMode =
                scanner.nextLine()
                        .trim()
                        .toUpperCase();

        if (!DELIVERY_MODE.equals(deliveryMode)
                && !PICKUP_MODE.equals(deliveryMode)) {

            ConsolePrinter.println(
                    "Modalità di consegna non valida."
            );

            return null;
        }

        checkoutBean.setDeliveryMode(deliveryMode);

        ConsolePrinter.print("Metodo di pagamento: ");
        String paymentMethod = scanner.nextLine().trim();

        if (paymentMethod.isBlank()) {
            ConsolePrinter.println("Metodo di pagamento obbligatorio.");
            return null;
        }

        checkoutBean.setPaymentMethod(paymentMethod);

        if (DELIVERY_MODE.equals(deliveryMode)) {
            return fillDeliveryData(checkoutBean);
        }

        return fillPickupData(checkoutBean);
    }

    private CheckoutBean fillDeliveryData(CheckoutBean checkoutBean) {
        ConsolePrinter.print("Indirizzo di consegna: ");
        String address = scanner.nextLine().trim();

        if (address.isBlank()) {
            ConsolePrinter.println("Indirizzo di consegna obbligatorio.");
            return null;
        }

        checkoutBean.setDeliveryAddress(address);
        checkoutBean.setPickupDate(null);
        checkoutBean.setPickupTime(null);

        return checkoutBean;
    }

    private CheckoutBean fillPickupData(CheckoutBean checkoutBean) {
        ConsolePrinter.print("Data ritiro (YYYY-MM-DD): ");
        String pickupDate = scanner.nextLine().trim();

        ConsolePrinter.print("Ora ritiro (HH:MM): ");
        String pickupTime = scanner.nextLine().trim();

        if (pickupDate.isBlank() || pickupTime.isBlank()) {
            ConsolePrinter.println("Data e ora di ritiro obbligatorie.");
            return null;
        }

        checkoutBean.setDeliveryAddress(null);
        checkoutBean.setPickupDate(pickupDate);
        checkoutBean.setPickupTime(pickupTime);

        return checkoutBean;
    }
}