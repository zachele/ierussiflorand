package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CustomBouquetOrderData;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;

import java.sql.SQLException;

public interface CustomBouquetOrderDAO {

    void save(CustomBouquetOrderData bouquetData) throws SQLException;

    CustomBouquetOrderSummary findByOrderId(int orderId) throws SQLException;
}