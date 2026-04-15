package com.example.shopflowers.cli;

import com.example.shopflowers.model.entity.FlowerProduct;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class ConsolePrinter {

    private static final PrintWriter WRITER = new PrintWriter(
            new OutputStreamWriter(
                    new FileOutputStream(FileDescriptor.out),
                    StandardCharsets.UTF_8
            ),
            true
    );

    private ConsolePrinter() {
    }

    public static void print(String message) {
        WRITER.print(message);
        WRITER.flush();
    }

    public static void println(String message) {
        WRITER.println(message);
    }

    public static void println() {
        WRITER.println();
    }

    public static void printProduct(FlowerProduct product) {
        println(
                "ID: " + product.getId()
                        + " | Nome: " + product.getName()
                        + " | Prezzo: € " + String.format("%.2f", product.getPrice())
                        + " | Colore: " + product.getColor()
                        + " | Varietà: " + product.getVariety()
                        + " | Stock: " + product.getStockQuantity()
        );
    }

    public static void printProducts(List<FlowerProduct> products) {
        for (FlowerProduct product : products) {
            printProduct(product);
        }
    }
}