package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.CustomBouquetItem;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderMemoryDAO implements OrderDAO {

    private final List<OrderSummary> orders = new ArrayList<>();
    private final List<OrderItemMemory> orderItems = new ArrayList<>();
    private int nextId = 1;

    @Override
    public int saveOrder(Order order) {
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

    @Override
    public void saveOrderItems(int orderId, Order order) {
        for (CartItem item : order.getItems()) {
            orderItems.add(createOrderItemMemory(
                    orderId,
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getProduct().getPrice()
            ));
        }
    }

    @Override
    public List<OrderSummary> findAllOrders() {
        return new ArrayList<>(orders);
    }

    @Override
    public List<OrderSummary> findOrdersByUsername(String username) {
        List<OrderSummary> result = new ArrayList<>();

        for (OrderSummary order : orders) {
            if (order.getUsername().equals(username)) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<OrderSummary> findActiveOrders() {
        List<OrderSummary> result = new ArrayList<>();

        for (OrderSummary order : orders) {
            if (!"CONSEGNATO".equals(order.getStatus())) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<OrderSummary> findCompletedOrders() {
        List<OrderSummary> result = new ArrayList<>();

        for (OrderSummary order : orders) {
            if ("CONSEGNATO".equals(order.getStatus())) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<OrderSummary> findOrdersWithStatusUpdate(String username) {
        return findOrdersByUsername(username);
    }

    @Override
    public void markOrdersAsNotified(String username) {
        // In memoria non è necessaria alcuna gestione dello stato di notifica.
    }

    @Override
    public List<OrderItemSummary> findItemsByOrderId(int orderId) {
        List<OrderItemSummary> result = new ArrayList<>();

        for (OrderItemMemory item : orderItems) {
            if (item.orderId() == orderId) {
                result.add(new OrderItemSummary(
                        item.productName(),
                        item.quantity(),
                        item.unitPrice()
                ));
            }
        }

        return result;
    }

    @Override
    public void updateOrderStatus(int orderId, String newStatus) {
        for (int i = 0; i < orders.size(); i++) {
            OrderSummary currentOrder = orders.get(i);

            if (currentOrder.getId() == orderId) {
                orders.set(i, createUpdatedOrderSummary(currentOrder, newStatus));
                return;
            }
        }
    }

    @Override
    public void saveCustomBouquetItems(int orderId, CustomBouquet bouquet) {
        for (CustomBouquetItem item : bouquet.getItems()) {
            orderItems.add(createOrderItemMemory(
                    orderId,
                    item.getFlowerProduct().getName(),
                    item.getQuantity(),
                    item.getFlowerProduct().getPrice()
            ));
        }
    }

    private OrderSummary createUpdatedOrderSummary(OrderSummary currentOrder, String newStatus) {
        return new OrderSummary(
                currentOrder.getId(),
                currentOrder.getUsername(),
                currentOrder.getName(),
                currentOrder.getSurname(),
                currentOrder.getDeliveryMode(),
                currentOrder.getDeliveryAddress(),
                currentOrder.getPickupDate(),
                currentOrder.getPickupTime(),
                currentOrder.getPaymentMethod(),
                newStatus,
                currentOrder.getTotal(),
                currentOrder.getOrderDate()
        );
    }

    private OrderItemMemory createOrderItemMemory(int orderId, String productName, int quantity, double unitPrice) {
        return new OrderItemMemory(orderId, productName, quantity, unitPrice);
    }

    private record OrderItemMemory(
            int orderId,
            String productName,
            int quantity,
            double unitPrice
    ) {
    }
}