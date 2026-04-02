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
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length != 13) {
                    continue;
                }

                orders.add(new OrderSummary(
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
                ));
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
        ensureOrdersFileExists();

        List<OrderSummary> result = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(ORDERS_FILE_PATH))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length != 13) {
                    continue;
                }

                boolean statusNotified = Boolean.parseBoolean(parts[10]);

                if (parts[1].equals(username) && !statusNotified) {
                    result.add(new OrderSummary(
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
                    ));
                }
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura aggiornamenti ordini da file.", e);
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
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length != 4) {
                    continue;
                }

                if (Integer.parseInt(parts[0]) == orderId) {
                    items.add(new OrderItemSummary(
                            parts[1],
                            Integer.parseInt(parts[2]),
                            Double.parseDouble(parts[3])
                    ));
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
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                String[] parts = line.split(";", -1);
                if (parts.length == 13) {
                    rows.add(parts);
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
            writer.write("id;username;name;surname;deliveryMode;deliveryAddress;pickupDate;pickupTime;total;paymentMethod;statusNotified;status;orderDate");
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
        try {
            Path filePath = Paths.get(ORDERS_FILE_PATH);
            Path parent = filePath.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filePath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write("id;username;name;surname;deliveryMode;deliveryAddress;pickupDate;pickupTime;total;paymentMethod;statusNotified;status;orderDate");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new SQLException("Errore nella creazione del file ordini.", e);
        }
    }

    private void ensureOrderItemsFileExists() throws SQLException {
        try {
            Path filePath = Paths.get(ORDER_ITEMS_FILE_PATH);
            Path parent = filePath.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filePath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write("orderId;productName;quantity;unitPrice");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new SQLException("Errore nella creazione del file articoli ordine.", e);
        }
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