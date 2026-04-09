package com.example.shopflowers.boundary;

import com.example.shopflowers.controller.application.CheckoutController;
import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InsufficientStockException;
import com.example.shopflowers.model.bean.CheckoutBean;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.util.Session;

import java.sql.SQLException;
import java.util.List;

public class CheckoutBoundary {

    private final CheckoutController checkoutController;

    public CheckoutBoundary() {
        this.checkoutController = new CheckoutController();
    }

    public Order confirmOrder(
            String deliveryMode,
            String paymentMethod,
            String deliveryAddress,
            String pickupDate,
            String pickupTime,
            List<CartItem> cartItems
    ) throws SQLException, EmptyCartException, InsufficientStockException {

        CheckoutBean checkoutBean = new CheckoutBean();
        checkoutBean.setUsername(Session.getInstance().getLoggedUsername());
        checkoutBean.setDeliveryMode(deliveryMode);
        checkoutBean.setPaymentMethod(paymentMethod);
        checkoutBean.setDeliveryAddress(deliveryAddress);
        checkoutBean.setPickupDate(pickupDate);
        checkoutBean.setPickupTime(pickupTime);

        Order order = checkoutController.createOrder(checkoutBean, cartItems);
        checkoutController.confirmOrder(order);

        return order;
    }
}