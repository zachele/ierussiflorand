package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.dao.OrderDAO;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.model.entity.Order;

import java.sql.SQLException;
import java.util.List;

public class CheckoutController {

    private final FlowerProductDAO flowerProductDAO;
    private final OrderDAO orderDAO;

    public CheckoutController() {
        this.flowerProductDAO = new FlowerProductDAO();
        this.orderDAO = new OrderDAO();
    }

    public Order createOrder(String username, java.util.List<CartItem> cartItems,
                             String deliveryMode, String deliveryAddress, String paymentMethod) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        return new Order(
                username,
                cartItems,
                deliveryMode,
                deliveryAddress,
                paymentMethod,
                "IN_PREPARAZIONE",
                total
        );
    }

    public boolean confirmOrder(Order order) throws SQLException {
        List<CartItem> items = order.getItems();

        for (CartItem item : items) {
            FlowerProduct productFromDb = flowerProductDAO.findById(item.getProduct().getId());

            if (productFromDb == null) {
                return false;
            }

            int remainingStock = productFromDb.getStockQuantity() - item.getQuantity();

            if (remainingStock < 0) {
                return false;
            }
        }

        int orderId = orderDAO.saveOrder(order);
        orderDAO.saveOrderItems(orderId, order);

        for (CartItem item : items) {
            FlowerProduct productFromDb = flowerProductDAO.findById(item.getProduct().getId());
            int remainingStock = productFromDb.getStockQuantity() - item.getQuantity();
            flowerProductDAO.updateStock(productFromDb.getId(), remainingStock);
        }

        return true;
    }
}