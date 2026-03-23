package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.dao.OrderDAO;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;

import java.sql.SQLException;
import java.util.List;

public class OperatorOrdersController {

    private final OrderDAO orderDAO;

    public OperatorOrdersController() {
        this.orderDAO = new OrderDAO();
    }

    public List<OrderSummary> getAllOrders() throws SQLException {
        return orderDAO.findAllOrders();
    }

    public List<OrderItemSummary> getItemsByOrderId(int orderId) throws SQLException {
        return orderDAO.findItemsByOrderId(orderId);
    }
}