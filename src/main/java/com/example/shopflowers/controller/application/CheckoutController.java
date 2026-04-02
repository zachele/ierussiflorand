package com.example.shopflowers.controller.application;

import com.example.shopflowers.model.bean.CheckoutBean;
import com.example.shopflowers.model.dao.CustomBouquetOrderDAO;
import com.example.shopflowers.model.dao.DAOFactory;
import com.example.shopflowers.model.dao.FlowerProductDAO;
import com.example.shopflowers.model.dao.OrderDAO;
import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.CustomBouquetItem;
import com.example.shopflowers.model.entity.CustomBouquetOrderData;
import com.example.shopflowers.model.entity.FlowerProduct;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.util.CustomBouquetSession;
import com.example.shopflowers.model.dao.CustomBouquetOrderDBDAO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutController {

    private final OrderDAO orderDAO;
    private final FlowerProductDAO flowerProductDAO;
    private final CustomBouquetOrderDAO customBouquetOrderDAO;

    public CheckoutController() {
        try {
            this.flowerProductDAO = DAOFactory.getFlowerProductDAO();
            this.orderDAO = DAOFactory.getOrderDAO();
            this.customBouquetOrderDAO = DAOFactory.getCustomBouquetOrderDAO();
        } catch (SQLException e) {
            throw new IllegalStateException("Impossibile inizializzare le DAO dell'applicazione.", e);
        }
    }

    public Order createOrder(CheckoutBean checkoutBean, List<CartItem> cartItems) {
        double total = 0;

        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        if (CustomBouquetSession.hasBouquet()) {
            total += CustomBouquetSession.getCurrentBouquet().getTotalPrice();
        }

        return new Order(
                checkoutBean.getUsername(),
                cartItems,
                checkoutBean.getDeliveryMode(),
                checkoutBean.getDeliveryAddress(),
                checkoutBean.getPickupDate(),
                checkoutBean.getPickupTime(),
                checkoutBean.getPaymentMethod(),
                "IN_PREPARAZIONE",
                total
        );
    }

    public boolean confirmOrder(Order order) throws SQLException {
        if ((order.getItems() == null || order.getItems().isEmpty()) && !CustomBouquetSession.hasBouquet()) {
            return false;
        }

        Map<Integer, Integer> requiredQuantities = new HashMap<>();

        for (CartItem item : order.getItems()) {
            int productId = item.getProduct().getId();
            requiredQuantities.put(
                    productId,
                    requiredQuantities.getOrDefault(productId, 0) + item.getQuantity()
            );
        }

        CustomBouquet bouquet = null;
        if (CustomBouquetSession.hasBouquet()) {
            bouquet = CustomBouquetSession.getCurrentBouquet();

            for (CustomBouquetItem item : bouquet.getItems()) {
                int productId = item.getFlowerProduct().getId();
                requiredQuantities.put(
                        productId,
                        requiredQuantities.getOrDefault(productId, 0) + item.getQuantity()
                );
            }
        }

        for (Map.Entry<Integer, Integer> entry : requiredQuantities.entrySet()) {
            FlowerProduct product = flowerProductDAO.findById(entry.getKey());

            if (product == null || product.getStockQuantity() < entry.getValue()) {
                return false;
            }
        }

        int orderId = orderDAO.saveOrder(order);

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            orderDAO.saveOrderItems(orderId, order);
        }

        if (bouquet != null) {
            orderDAO.saveCustomBouquetItems(orderId, bouquet);

            CustomBouquetOrderData bouquetData = new CustomBouquetOrderData(
                    orderId,
                    bouquet.getSize(),
                    bouquet.getPackaging(),
                    bouquet.isCardIncluded(),
                    bouquet.isVaseIncluded(),
                    bouquet.getTotalPrice()
            );

            customBouquetOrderDAO.save(bouquetData);
        }

        for (Map.Entry<Integer, Integer> entry : requiredQuantities.entrySet()) {
            FlowerProduct product = flowerProductDAO.findById(entry.getKey());
            int newStock = product.getStockQuantity() - entry.getValue();
            flowerProductDAO.updateStock(entry.getKey(), newStock);
        }

        CustomBouquetSession.clear();
        return true;
    }
}