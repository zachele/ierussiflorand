package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderMemoryDAO {

    private static final List<OrderSummary> orders = new ArrayList<>();
    private static final List<OrderItemMemory> orderItems = new ArrayList<>();
    private static int nextId = 1;

    public int saveOrder(Order order) throws SQLException {
        int id = nextId++;

        OrderSummary summary = new OrderSummary(
                id,
                order.getUsername(),
                "DemoName",
                "DemoSurname",
                order.getDeliveryMode(),
                order.getDeliveryAddress(),
                order.getPickupDate(),
                order.getPickupTime(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getTotal(),
                LocalDateTime.now().toString()
        );

        orders.add(summary);
        return id;
    }

    public void saveOrderItems(int orderId, Order order) throws SQLException {
        for (CartItem item : order.getItems()) {
            orderItems.add(new OrderItemMemory(
                    orderId,
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice()
            ));
        }
    }

    public List<OrderSummary> findOrdersByUsername(String username) throws SQLException {
        List<OrderSummary> result = new ArrayList<>();

        for (OrderSummary order : orders) {
            if (order.getUsername().equals(username)) {
                result.add(order);
            }
        }

        return result;
    }

    public List<OrderItemSummary> findItemsByOrderId(int orderId) throws SQLException {
        List<OrderItemSummary> result = new ArrayList<>();

        for (OrderItemMemory item : orderItems) {
            if (item.orderId == orderId) {
                result.add(new OrderItemSummary(
                        item.productName,
                        item.quantity,
                        item.unitPrice
                ));
            }
        }

        return result;
    }

    private static class OrderItemMemory {
        int orderId;
        String productName;
        int quantity;
        double unitPrice;

        OrderItemMemory(int orderId, String productName, int quantity, double unitPrice) {
            this.orderId = orderId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}