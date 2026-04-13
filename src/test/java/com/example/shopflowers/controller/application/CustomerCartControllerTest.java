package com.example.shopflowers.controller.application;

import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InvalidQuantityException;
import com.example.shopflowers.exception.ProductNotFoundException;
import com.example.shopflowers.model.entity.FlowerProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerCartControllerTest {

    private CustomerCartController cartController;
    private FlowerProduct product;

    @BeforeEach
    void setUp() {
        cartController = new CustomerCartController();

        product = new FlowerProduct(
                1,
                "Rosa Rossa",
                10.0,
                "Rosso",
                "Romantico",
                10,
                "rose_red.png"
        );
    }

    @Test
    void addToCart_validProduct_shouldAddCorrectly() throws InvalidQuantityException {
        boolean result = cartController.addToCart(product, 2);

        assertTrue(result);
        assertEquals(1, cartController.getCartItems().size());
        assertEquals(2, cartController.getCartItems().get(0).getQuantity());
    }

    @Test
    void addToCart_quantityExceedsStock_shouldFail() throws InvalidQuantityException {
        boolean result = cartController.addToCart(product, 20);

        assertFalse(result);
        assertTrue(cartController.getCartItems().isEmpty());
    }

    @Test
    void addToCart_sameProduct_shouldIncreaseQuantity() throws InvalidQuantityException {
        cartController.addToCart(product, 2);
        cartController.addToCart(product, 3);

        assertEquals(1, cartController.getCartItems().size());
        assertEquals(5, cartController.getCartItems().get(0).getQuantity());
    }

    @Test
    void addToCart_invalidQuantity_shouldThrowException() {
        assertThrows(
                InvalidQuantityException.class,
                () -> cartController.addToCart(product, 0)
        );
    }

    @Test
    void removeFromCart_shouldRemoveItem() throws InvalidQuantityException, ProductNotFoundException {
        cartController.addToCart(product, 2);
        cartController.removeFromCart(product.getId());

        assertTrue(cartController.getCartItems().isEmpty());
    }

    @Test
    void removeFromCart_missingProduct_shouldThrowException() {
        assertThrows(
                ProductNotFoundException.class,
                () -> cartController.removeFromCart(product.getId())
        );
    }

    @Test
    void clearCart_shouldEmptyCart() throws InvalidQuantityException, EmptyCartException {
        cartController.addToCart(product, 2);
        cartController.clearCart();

        assertTrue(cartController.getCartItems().isEmpty());
    }

    @Test
    void clearCart_emptyCart_shouldThrowException() {
        assertThrows(
                EmptyCartException.class,
                cartController::clearCart
        );
    }

    @Test
    void getCartTotal_shouldCalculateCorrectly() throws InvalidQuantityException, EmptyCartException {
        cartController.addToCart(product, 2);

        double total = cartController.getCartTotal();

        assertEquals(20.0, total);
    }

    @Test
    void getCartTotal_emptyCart_shouldThrowException() {
        assertThrows(
                EmptyCartException.class,
                cartController::getCartTotal
        );
    }

    @Test
    void isCartEmpty_shouldReturnTrueWhenEmpty() {
        assertTrue(cartController.isCartEmpty());
    }

    @Test
    void isCartEmpty_shouldReturnFalseWhenNotEmpty() throws InvalidQuantityException {
        cartController.addToCart(product, 1);

        assertFalse(cartController.isCartEmpty());
    }
}