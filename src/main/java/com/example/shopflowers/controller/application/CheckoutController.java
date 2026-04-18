package com.example.shopflowers.controller.application;

import com.example.shopflowers.exception.EmptyCartException;
import com.example.shopflowers.exception.InsufficientStockException;
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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutController {

    private static final String INITIAL_ORDER_STATUS = "IN_PREPARAZIONE";

    private final OrderDAO orderDAO;
    private final FlowerProductDAO flowerProductDAO;
    private final CustomBouquetOrderDAO customBouquetOrderDAO;

    public CheckoutController() {
        this.flowerProductDAO = DAOFactory.getFlowerProductDAO();
        this.orderDAO = DAOFactory.getOrderDAO();
        this.customBouquetOrderDAO = DAOFactory.getCustomBouquetOrderDAO();
    }

    public Order createOrder(CheckoutBean checkoutBean, List<CartItem> cartItems) {
        double total = calculateOrderTotal(cartItems);

        return new Order.Builder()
                .username(checkoutBean.getUsername())
                .items(cartItems)
                .deliveryMode(checkoutBean.getDeliveryMode())
                .deliveryAddress(checkoutBean.getDeliveryAddress())
                .pickupDate(checkoutBean.getPickupDate())
                .pickupTime(checkoutBean.getPickupTime())
                .paymentMethod(checkoutBean.getPaymentMethod())
                .status(INITIAL_ORDER_STATUS)
                .total(total)
                .build();
    }

    public void confirmOrder(Order order)
            throws SQLException, EmptyCartException, InsufficientStockException {

        if ((order.getItems() == null || order.getItems().isEmpty()) && !CustomBouquetSession.hasBouquet()) {
            throw new EmptyCartException("Impossibile confermare un ordine vuoto.");
        }

        Map<Integer, Integer> requiredQuantities = buildRequiredQuantities(order);
        CustomBouquet bouquet = CustomBouquetSession.hasBouquet()
                ? CustomBouquetSession.getCurrentBouquet()
                : null;

        validateStockAvailability(requiredQuantities);

        int orderId = orderDAO.saveOrder(order);

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            orderDAO.saveOrderItems(orderId, order);
        }

        if (bouquet != null) {
            saveCustomBouquet(orderId, bouquet);
        }

        updateProductStocks(requiredQuantities);
        CustomBouquetSession.clear();
    }

    private double calculateOrderTotal(List<CartItem> cartItems) {
        double total = 0.0;

        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }

        if (CustomBouquetSession.hasBouquet()) {
            total += CustomBouquetSession.getCurrentBouquet().getTotalPrice();
        }

        return total;
    }

    private Map<Integer, Integer> buildRequiredQuantities(Order order) {
        Map<Integer, Integer> requiredQuantities = new HashMap<>();

        for (CartItem item : order.getItems()) {
            addRequiredQuantity(requiredQuantities, item.getProduct().getId(), item.getQuantity());
        }

        if (CustomBouquetSession.hasBouquet()) {
            CustomBouquet bouquet = CustomBouquetSession.getCurrentBouquet();

            for (CustomBouquetItem item : bouquet.getItems()) {
                addRequiredQuantity(requiredQuantities, item.getFlowerProduct().getId(), item.getQuantity());
            }
        }

        return requiredQuantities;
    }

    private void addRequiredQuantity(Map<Integer, Integer> requiredQuantities, int productId, int quantity) {
        requiredQuantities.put(
                productId,
                requiredQuantities.getOrDefault(productId, 0) + quantity
        );
    }

    private void validateStockAvailability(Map<Integer, Integer> requiredQuantities)
            throws SQLException, InsufficientStockException {

        for (Map.Entry<Integer, Integer> entry : requiredQuantities.entrySet()) {
            FlowerProduct product = flowerProductDAO.findById(entry.getKey());

            if (product == null || product.getStockQuantity() < entry.getValue()) {
                throw new InsufficientStockException("Stock insufficiente per uno o più prodotti.");
            }
        }
    }

    private void saveCustomBouquet(int orderId, CustomBouquet bouquet) throws SQLException {
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

    private void updateProductStocks(Map<Integer, Integer> requiredQuantities) throws SQLException {
        for (Map.Entry<Integer, Integer> entry : requiredQuantities.entrySet()) {
            FlowerProduct product = flowerProductDAO.findById(entry.getKey());
            int newStock = product.getStockQuantity() - entry.getValue();
            flowerProductDAO.updateStock(entry.getKey(), newStock);
        }
    }
}