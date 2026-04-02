package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;

import java.sql.SQLException;
import java.util.List;

public interface OrderDAO {

    int saveOrder(Order order) throws SQLException;

    void saveOrderItems(int orderId, Order order) throws SQLException;

    List<OrderSummary> findAllOrders() throws SQLException;

    List<OrderSummary> findOrdersByUsername(String username) throws SQLException;

    List<OrderSummary> findActiveOrders() throws SQLException;

    List<OrderSummary> findCompletedOrders() throws SQLException;

    List<OrderSummary> findOrdersWithStatusUpdate(String username) throws SQLException;

    void markOrdersAsNotified(String username) throws SQLException;

    List<OrderItemSummary> findItemsByOrderId(int orderId) throws SQLException;

    void updateOrderStatus(int orderId, String newStatus) throws SQLException;

    void saveCustomBouquetItems(int orderId, CustomBouquet bouquet) throws SQLException;
}