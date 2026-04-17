package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CustomBouquetOrderData;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;

import java.util.HashMap;
import java.util.Map;

public class CustomBouquetOrderMemoryDAO implements CustomBouquetOrderDAO {

    private static final Map<Integer, CustomBouquetOrderData> BOUQUET_ORDERS = new HashMap<>();

    @Override
    public void save(CustomBouquetOrderData bouquetData) {
        BOUQUET_ORDERS.put(bouquetData.getOrderId(), bouquetData);
    }

    @Override
    public CustomBouquetOrderSummary findByOrderId(int orderId) {
        CustomBouquetOrderData data = BOUQUET_ORDERS.get(orderId);

        if (data == null) {
            return null;
        }

        return new CustomBouquetOrderSummary(
                data.getSize(),
                data.getPackaging(),
                data.isCardIncluded(),
                data.isVaseIncluded(),
                data.getTotalPrice()
        );
    }
}