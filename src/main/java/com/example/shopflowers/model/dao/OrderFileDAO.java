package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CartItem;
import com.example.shopflowers.model.entity.CustomBouquet;
import com.example.shopflowers.model.entity.CustomBouquetItem;
import com.example.shopflowers.model.entity.Order;
import com.example.shopflowers.model.entity.OrderItemSummary;
import com.example.shopflowers.model.entity.OrderSummary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderFileDAO implements OrderDAO {

    private static final String ORDERS_FILE_PATH = "data/orders.csv";
    private static final String ORDER_ITEMS_FILE_PATH = "data/order_items.csv";

    private static final String ORDERS_HEADER =
            "id;username;name;surname;deliveryMode;deliveryAddress;pickupDate;pickupTime;total;paymentMethod;statusNotified;status;orderDate";
    private static final String ORDER_ITEMS_HEADER =
            "orderId;productName;quantity;unitPrice";

    private static final int ORDER_PARTS_COUNT = 13;
    private static final int ORDER_ITEM_PARTS_COUNT = 4;

    @Override
    public int saveOrder(Order order) throws SQLException {
        List<OrderSummary> orders = findAllOrders();
        int nextId = getNextOrderId(orders);

        ensureOrdersFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(ORDERS_FILE_PATH),
                java.nio.file.StandardOpenOption.APPEND)) {

            writer.write(String.format(
                    "%d;%s;%s;%s;%s;%s;%s;%s;%.2f;%s;%b;%s;%s",
                    nextId,
                    escape(order.getUsername()),
                    "",
                    "",
                    escape(order.getDeliveryMode()),
                    escape(order.getDeliveryAddress()),
                    escape(order.getPickupDate()),
                    escape(order.getPickupTime()),
                    order.getTotal(),
                    escape(order.getPaymentMethod()),
                    false,
                    escape(order.getStatus()),
                    LocalDateTime.now()
            ));
            writer.newLine();

        } catch (IOException e) {
            throw new SQLException("Errore nel salvataggio ordine su file.", e);
        }

        return nextId;
    }

    @Override
    public void saveOrderItems(int orderId, Order order) throws SQLException {
        ensureOrderItemsFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(ORDER_ITEMS_FILE_PATH),
                java.nio.file.StandardOpenOption.APPEND)) {

            for (CartItem item : order.getItems()) {
                writer.write(String.format(
                        "%d;%s;%d;%.2f",
                        orderId,
                        escape(item.getProduct().getName()),
                        item.getQuantity(),
                        item.getProduct().getPrice()
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new SQLException("Errore nel salvataggio articoli ordine su file.", e);
        }
    }

    @Override
    public List<OrderSummary> findAllOrders() throws SQLException {
        ensureOrdersFileExists();

        List<OrderSummary> orders = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(ORDERS_FILE_PATH))) {
            String header = reader.readLine();
            if (header == null) {
                return orders;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                OrderSummary orderSummary = parseOrderSummary(line);
                if (orderSummary != null) {
                    orders.add(orderSummary);
                }
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura ordini da file.", e);
        }

        return orders;
    }

    @Override
    public List<OrderSummary> findOrdersByUsername(String username) throws SQLException {
        List<OrderSummary> result = new ArrayList<>();

        for (OrderSummary order : findAllOrders()) {
            if (order.getUsername().equals(username)) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<OrderSummary> findActiveOrders() throws SQLException {
        List<OrderSummary> result = new ArrayList<>();

        for (OrderSummary order : findAllOrders()) {
            if (!"CONSEGNATO".equals(order.getStatus())) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<OrderSummary> findCompletedOrders() throws SQLException {
        List<OrderSummary> result = new ArrayList<>();

        for (OrderSummary order : findAllOrders()) {
            if ("CONSEGNATO".equals(order.getStatus())) {
                result.add(order);
            }
        }

        return result;
    }

    @Override
    public List<OrderSummary> findOrdersWithStatusUpdate(String username) throws SQLException {
        List<OrderSummary> result = new ArrayList<>();

        for (String[] row : readOrderRows()) {
            boolean statusNotified = Boolean.parseBoolean(row[10]);

            if (row[1].equals(username) && !statusNotified) {
                result.add(mapOrderSummary(row));
            }
        }

        return result;
    }

    @Override
    public void markOrdersAsNotified(String username) throws SQLException {
        List<String[]> rows = readOrderRows();

        for (String[] row : rows) {
            if (row[1].equals(username)) {
                row[10] = "true";
            }
        }

        writeOrderRows(rows);
    }

    @Override
    public List<OrderItemSummary> findItemsByOrderId(int orderId) throws SQLException {
        ensureOrderItemsFileExists();

        List<OrderItemSummary> items = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(ORDER_ITEMS_FILE_PATH))) {
            String header = reader.readLine();
            if (header == null) {
                return items;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                OrderItemSummary itemSummary = parseOrderItemSummaryIfMatching(line, orderId);
                if (itemSummary != null) {
                    items.add(itemSummary);
                }
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura articoli ordine da file.", e);
        }

        return items;
    }

    @Override
    public void updateOrderStatus(int orderId, String newStatus) throws SQLException {
        List<String[]> rows = readOrderRows();

        for (String[] row : rows) {
            if (Integer.parseInt(row[0]) == orderId) {
                row[11] = newStatus;
                row[10] = "false";
            }
        }

        writeOrderRows(rows);
    }

    @Override
    public void saveCustomBouquetItems(int orderId, CustomBouquet bouquet) throws SQLException {
        ensureOrderItemsFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(ORDER_ITEMS_FILE_PATH),
                java.nio.file.StandardOpenOption.APPEND)) {

            for (CustomBouquetItem item : bouquet.getItems()) {
                writer.write(String.format(
                        "%d;%s;%d;%.2f",
                        orderId,
                        escape(item.getFlowerProduct().getName()),
                        item.getQuantity(),
                        item.getFlowerProduct().getPrice()
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new SQLException("Errore nel salvataggio bouquet ordine su file.", e);
        }
    }

    private List<String[]> readOrderRows() throws SQLException {
        ensureOrdersFileExists();
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(ORDERS_FILE_PATH))) {
            String header = reader.readLine();
            if (header == null) {
                return rows;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] row = parseCsvRow(line, ORDER_PARTS_COUNT);
                if (row.length == ORDER_PARTS_COUNT) {
                    rows.add(row);
                }
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura del file ordini.", e);
        }

        return rows;
    }

    private void writeOrderRows(List<String[]> rows) throws SQLException {
        ensureOrdersFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(ORDERS_FILE_PATH))) {
            writer.write(ORDERS_HEADER);
            writer.newLine();

            for (String[] row : rows) {
                writer.write(String.join(";", row));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella scrittura del file ordini.", e);
        }
    }

    private void ensureOrdersFileExists() throws SQLException {
        ensureCsvFileExists(
                ORDERS_FILE_PATH,
                ORDERS_HEADER,
                "Errore nella creazione del file ordini."
        );
    }

    private void ensureOrderItemsFileExists() throws SQLException {
        ensureCsvFileExists(
                ORDER_ITEMS_FILE_PATH,
                ORDER_ITEMS_HEADER,
                "Errore nella creazione del file articoli ordine."
        );
    }

    private void ensureCsvFileExists(String filePathString, String header, String errorMessage) throws SQLException {
        try {
            Path filePath = Paths.get(filePathString);
            Path parent = filePath.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filePath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write(header);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new SQLException(errorMessage, e);
        }
    }

    private OrderSummary parseOrderSummary(String line) {
        String[] row = parseCsvRow(line, ORDER_PARTS_COUNT);

        if (row.length != ORDER_PARTS_COUNT) {
            return null;
        }

        try {
            return mapOrderSummary(row);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private OrderItemSummary parseOrderItemSummaryIfMatching(String line, int orderId) {
        String[] parts = parseCsvRow(line, ORDER_ITEM_PARTS_COUNT);

        if (parts.length != ORDER_ITEM_PARTS_COUNT) {
            return null;
        }

        try {
            int currentOrderId = Integer.parseInt(parts[0]);

            if (currentOrderId != orderId) {
                return null;
            }

            return new OrderItemSummary(
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Double.parseDouble(parts[3])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String[] parseCsvRow(String line, int expectedParts) {
        if (line == null || line.isBlank()) {
            return new String[0];
        }

        String[] parts = line.split(";", -1);
        return parts.length == expectedParts ? parts : new String[0];
    }

    private OrderSummary mapOrderSummary(String[] parts) {
        return new OrderSummary(
                Integer.parseInt(parts[0]),
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5],
                parts[6],
                parts[7],
                parts[9],
                parts[11],
                Double.parseDouble(parts[8]),
                parts[12]
        );
    }

    private int getNextOrderId(List<OrderSummary> orders) {
        int maxId = 0;

        for (OrderSummary order : orders) {
            if (order.getId() > maxId) {
                maxId = order.getId();
            }
        }

        return maxId + 1;
    }

    private String escape(String value) {
        return value == null ? "" : value.replace(";", ",");
    }
}