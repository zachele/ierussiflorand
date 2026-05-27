package com.example.shopflowers.controller.application;

import com.example.shopflowers.config.AppConfig;
import com.example.shopflowers.config.AppMode;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InsufficientStockException;
import com.example.shopflowers.exception.InvalidDeliveryAddressException;
import com.example.shopflowers.exception.PaymentFailedException;
import com.example.shopflowers.model.bean.CheckoutBean;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.CustomBouquetItem;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.util.CustomBouquetSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutControllerTest {

    private CheckoutController checkoutController;
    private FlowerProductDAO flowerProductDAO;
    private FlowerProduct product;

    @BeforeEach
    void setUp() throws SQLException {
        AppConfig.setMode(AppMode.DEMO);
        CustomBouquetSession.clear();

        checkoutController = new CheckoutController();
        flowerProductDAO = DAOFactory.getFlowerProductDAO();

        product = flowerProductDAO.findById(1);
        assertNotNull(product, "Il prodotto con ID 1 deve esistere in modalità DEMO.");
    }

    @Test
    void createOrder_validDeliveryCheckout_shouldBuildOrderCorrectly() {
        CheckoutBean checkoutBean = buildDeliveryCheckoutBean();
        List<CartItem> cartItems = List.of(new CartItem(product, 2));

        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        assertNotNull(order);
        assertEquals("customer_test", order.getUsername());
        assertEquals("CONSEGNA", order.getDeliveryMode());
        assertEquals("Via Roma 10", order.getDeliveryAddress());
        assertEquals("CARTA", order.getPaymentMethod());
        assertEquals("IN_PREPARAZIONE", order.getStatus());
        assertEquals(1, order.getItems().size());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(product.getPrice() * 2, order.getTotal());
    }

    @Test
    void createOrder_validPickupCheckout_shouldBuildOrderCorrectly() {
        CheckoutBean checkoutBean = buildPickupCheckoutBean();
        List<CartItem> cartItems = List.of(new CartItem(product, 1));

        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        assertNotNull(order);
        assertEquals("RITIRO", order.getDeliveryMode());
        assertNull(order.getDeliveryAddress());
        assertEquals("2026-04-20", order.getPickupDate());
        assertEquals("10:30", order.getPickupTime());
        assertEquals("CONTANTI", order.getPaymentMethod());
    }

    @Test
    void confirmOrder_emptyOrderWithoutBouquet_shouldThrowEmptyCartException() {
        CheckoutBean checkoutBean = buildPickupCheckoutBean();
        Order order = checkoutController.createOrder(checkoutBean, Collections.emptyList());

        assertThrows(
                EmptyCartException.class,
                () -> checkoutController.confirmOrder(order)
        );
    }

    @Test
    void confirmOrder_insufficientStock_shouldThrowInsufficientStockException() {
        CheckoutBean checkoutBean = buildPickupCheckoutBean();
        List<CartItem> cartItems = List.of(new CartItem(product, product.getStockQuantity() + 1));
        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        assertThrows(
                InsufficientStockException.class,
                () -> checkoutController.confirmOrder(order)
        );
    }

    @Test
    void confirmOrder_invalidPaymentMethod_shouldThrowPaymentFailedException() {
        CheckoutBean checkoutBean = buildPickupCheckoutBean();
        checkoutBean.setPaymentMethod("BITCOIN");

        List<CartItem> cartItems = List.of(new CartItem(product, 1));
        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        assertThrows(
                PaymentFailedException.class,
                () -> checkoutController.confirmOrder(order)
        );
    }

    @Test
    void confirmOrder_invalidDeliveryAddress_shouldThrowInvalidDeliveryAddressException() {
        CheckoutBean checkoutBean = buildDeliveryCheckoutBean();
        checkoutBean.setDeliveryAddress("indirizzo sicuramente inesistente zzzxxxqqq 999999");

        List<CartItem> cartItems = List.of(new CartItem(product, 1));
        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        assertThrows(
                InvalidDeliveryAddressException.class,
                () -> checkoutController.confirmOrder(order)
        );
    }

    @Test
    void confirmOrder_validPickupOrder_shouldCompleteSuccessfully() {
        CheckoutBean checkoutBean = buildPickupCheckoutBean();
        List<CartItem> cartItems = List.of(new CartItem(product, 1));
        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        assertDoesNotThrow(() -> checkoutController.confirmOrder(order));
    }

    @Test
    void confirmOrder_validPickupOrder_shouldReduceStock()
            throws SQLException,
            EmptyCartException,
            InsufficientStockException,
            PaymentFailedException,
            InvalidDeliveryAddressException {

        CheckoutBean checkoutBean = buildPickupCheckoutBean();
        int initialStock = product.getStockQuantity();

        List<CartItem> cartItems = List.of(new CartItem(product, 1));
        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        checkoutController.confirmOrder(order);

        FlowerProduct updatedProduct = flowerProductDAO.findById(product.getId());

        assertNotNull(updatedProduct);
        assertEquals(initialStock - 1, updatedProduct.getStockQuantity());
    }

    @Test
    void createOrder_withCustomBouquet_shouldIncludeBouquetInTotal() {
        CheckoutBean checkoutBean = buildPickupCheckoutBean();
        List<CartItem> cartItems = List.of(new CartItem(product, 1));

        CustomBouquet bouquet = buildTestBouquet(product);
        CustomBouquetSession.setCurrentBouquet(bouquet);

        Order order = checkoutController.createOrder(checkoutBean, cartItems);

        assertEquals(product.getPrice() + bouquet.getTotalPrice(), order.getTotal());
    }

    @Test
    void confirmOrder_onlyBouquetWithoutCart_shouldCompleteSuccessfully() {
        CheckoutBean checkoutBean = buildPickupCheckoutBean();

        CustomBouquet bouquet = buildTestBouquet(product);
        CustomBouquetSession.setCurrentBouquet(bouquet);

        Order order = checkoutController.createOrder(checkoutBean, Collections.emptyList());

        assertDoesNotThrow(() -> checkoutController.confirmOrder(order));
    }

    private CheckoutBean buildDeliveryCheckoutBean() {
        CheckoutBean checkoutBean = new CheckoutBean();
        checkoutBean.setUsername("customer_test");
        checkoutBean.setDeliveryMode("CONSEGNA");
        checkoutBean.setDeliveryAddress("Via Roma 10");
        checkoutBean.setPickupDate(null);
        checkoutBean.setPickupTime(null);
        checkoutBean.setPaymentMethod("CARTA");
        return checkoutBean;
    }

    private CheckoutBean buildPickupCheckoutBean() {
        CheckoutBean checkoutBean = new CheckoutBean();
        checkoutBean.setUsername("customer_test");
        checkoutBean.setDeliveryMode("RITIRO");
        checkoutBean.setDeliveryAddress(null);
        checkoutBean.setPickupDate("2026-04-20");
        checkoutBean.setPickupTime("10:30");
        checkoutBean.setPaymentMethod("CONTANTI");
        return checkoutBean;
    }

    private CustomBouquet buildTestBouquet(FlowerProduct flowerProduct) {
        CustomBouquetBuilder builder = new CustomBouquetBuilder();
        builder.setSize("MEDIO");
        builder.setPackaging("PREMIUM");
        builder.setCardIncluded(true);
        builder.setVaseIncluded(false);
        builder.addItem(new CustomBouquetItem(flowerProduct, 1));
        return builder.build();
    }
}