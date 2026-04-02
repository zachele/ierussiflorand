package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.FlowerProduct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FlowerProductFileDAO implements FlowerProductDAO {

    private static final String FILE_PATH = "data/flower_products.csv";

    @Override
    public void save(FlowerProduct product) throws SQLException {
        List<FlowerProduct> products = findAll();
        int nextId = getNextId(products);
        product.setId(nextId);
        products.add(product);
        writeAll(products);
    }

    @Override
    public List<FlowerProduct> findAll() throws SQLException {
        ensureFileExists();

        List<FlowerProduct> products = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
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
                if (parts.length != 6) {
                    continue;
                }

                FlowerProduct product = new FlowerProduct(
                        Integer.parseInt(parts[0]),
                        parts[1],
                        Double.parseDouble(parts[2]),
                        parts[3],
                        parts[4],
                        Integer.parseInt(parts[5])
                );

                products.add(product);
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura dei prodotti da file.", e);
        }

        return products;
    }

    @Override
    public void update(FlowerProduct product) throws SQLException {
        List<FlowerProduct> products = findAll();

        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == product.getId()) {
                products.set(i, product);
                writeAll(products);
                return;
            }
        }
    }

    @Override
    public void deleteById(int id) throws SQLException {
        List<FlowerProduct> products = findAll();
        products.removeIf(product -> product.getId() == id);
        writeAll(products);
    }

    @Override
    public FlowerProduct findById(int id) throws SQLException {
        List<FlowerProduct> products = findAll();

        for (FlowerProduct product : products) {
            if (product.getId() == id) {
                return product;
            }
        }

        return null;
    }

    @Override
    public void updateStock(int productId, int newStock) throws SQLException {
        List<FlowerProduct> products = findAll();

        for (FlowerProduct product : products) {
            if (product.getId() == productId) {
                product.setStockQuantity(newStock);
                writeAll(products);
                return;
            }
        }
    }

    private void writeAll(List<FlowerProduct> products) throws SQLException {
        ensureFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            writer.write("id;name;price;color;variety;stockQuantity");
            writer.newLine();

            for (FlowerProduct product : products) {
                writer.write(String.format(
                        "%d;%s;%.2f;%s;%s;%d",
                        product.getId(),
                        escape(product.getName()),
                        product.getPrice(),
                        escape(product.getColor()),
                        escape(product.getVariety()),
                        product.getStockQuantity()
                ));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella scrittura dei prodotti su file.", e);
        }
    }

    private void ensureFileExists() throws SQLException {
        try {
            Path filePath = Paths.get(FILE_PATH);
            Path parent = filePath.getParent();

            if (parent != null && Files.notExists(parent)) {
                Files.createDirectories(parent);
            }

            if (Files.notExists(filePath)) {
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write("id;name;price;color;variety;stockQuantity");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new SQLException("Errore nella creazione del file prodotti.", e);
        }
    }

    private int getNextId(List<FlowerProduct> products) {
        int maxId = 0;

        for (FlowerProduct product : products) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }

        return maxId + 1;
    }

    private String escape(String value) {
        return value == null ? "" : value.replace(";", ",");
    }
}