package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.OrderDAO;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;

import java.sql.SQLException;
import java.util.List;

public class CustomerOrdersController {

    private final OrderDAO orderDAO;

    public CustomerOrdersController() {
        this.orderDAO = new OrderDAO();
    }

    public List<OrderSummary> getOrdersByUsername(String username) throws SQLException {
        return orderDAO.findOrdersByUsername(username);
    }

    public List<OrderItemSummary> getItemsByOrderId(int orderId) throws SQLException {
        return orderDAO.findItemsByOrderId(orderId);
    }
}