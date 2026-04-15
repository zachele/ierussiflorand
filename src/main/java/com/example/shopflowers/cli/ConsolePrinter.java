package com.example.shopflowers.cli;

import com.example.shopflowers.model.entity.FlowerProduct;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public final class ConsolePrinter {

    private static final PrintWriter OUT =
            new PrintWriter(new FileOutputStream(FileDescriptor.out), true);

    private ConsolePrinter() {
    }

    public static void println() {
        OUT.println();
    }

    public static void println(String message) {
        OUT.println(message);
    }

    public static void print(String message) {
        OUT.print(message);
        OUT.flush();
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
}