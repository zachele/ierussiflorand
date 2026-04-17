package com.example.shopflowers.model.dao;

import com.example.shopflowers.model.entity.CustomBouquetOrderData;
import com.example.shopflowers.model.entity.CustomBouquetOrderSummary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class CustomBouquetOrderFileDAO implements CustomBouquetOrderDAO {

    private static final String FILE_PATH = "data/custom_bouquet_orders.csv";
    private static final String FILE_HEADER =
            "orderId;size;packaging;cardIncluded;vaseIncluded;totalPrice";
    private static final int EXPECTED_PARTS = 6;

    @Override
    public void save(CustomBouquetOrderData bouquetData) throws SQLException {
        ensureFileExists();

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILE_PATH),
                java.nio.file.StandardOpenOption.APPEND)) {

            writer.write(String.format(
                    "%d;%s;%s;%b;%b;%.2f",
                    bouquetData.getOrderId(),
                    escape(bouquetData.getSize()),
                    escape(bouquetData.getPackaging()),
                    bouquetData.isCardIncluded(),
                    bouquetData.isVaseIncluded(),
                    bouquetData.getTotalPrice()
            ));
            writer.newLine();

        } catch (IOException e) {
            throw new SQLException("Errore nella scrittura bouquet su file.", e);
        }
    }

    @Override
    public CustomBouquetOrderSummary findByOrderId(int orderId) throws SQLException {
        ensureFileExists();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String header = reader.readLine();

            if (header == null) {
                return null;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                CustomBouquetOrderSummary summary = parseSummaryIfMatching(line, orderId);

                if (summary != null) {
                    return summary;
                }
            }

        } catch (IOException e) {
            throw new SQLException("Errore nella lettura bouquet da file.", e);
        }

        return null;
    }

    private CustomBouquetOrderSummary parseSummaryIfMatching(String line, int orderId) {
        String[] parts = line.split(";", -1);

        if (parts.length != EXPECTED_PARTS) {
            return null;
        }

        try {
            int currentOrderId = Integer.parseInt(parts[0]);

            if (currentOrderId != orderId) {
                return null;
            }

            return new CustomBouquetOrderSummary(
                    parts[1],
                    parts[2],
                    Boolean.parseBoolean(parts[3]),
                    Boolean.parseBoolean(parts[4]),
                    Double.parseDouble(parts[5])
            );
        } catch (NumberFormatException e) {
            return null;
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
                    writer.write(FILE_HEADER);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new SQLException("Errore creazione file bouquet.", e);
        }
    }

    private String escape(String value) {
        return value == null ? "" : value.replace(";", ",");
    }
}