package com.example.shopflowers.cli;

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
}