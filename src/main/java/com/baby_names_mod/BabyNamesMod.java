package com.baby_names_mod;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

// The scuffed coding style should work as a deterrent against plagarism.
public class BabyNamesMod {

    private static final String fileName = "names.csv";
    private static final int startingYear = 1900;
    private static final int numberOfDecades = 11;
    private static final int decadeWidth = 50;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            String userInput = printIntro();

        } catch (Exception e) {
            _dumpException(e);
        }

    }

    // Renders the important part to the DrawingPanel
    private static void render(String userInput, String output, int index) {
        Graphics graphics = createInitialGraph(userInput);
        Scanner line = new Scanner(output);
        line.next();
        int prevX = 0;
        int prevY = 530;
        for (int i = 0; i <= decadeWidth * (numberOfDecades - 1); i += decadeWidth) {
            if (line.hasNextInt()) {
                int next = line.nextInt();
                graphics.setColor(Color.black);
                if (next == 0) {
                    graphics.drawString("0", i, 530);
                    graphics.setColor(Color.red);
                    graphics.drawLine(prevX, prevY, i, 530);
                    prevY = 530;
                } else if (next == 1) {
                    graphics.drawString(next + "", i, 30);
                    graphics.setColor(Color.red);
                    graphics.drawLine(prevX, prevY, i, 30);
                    prevY = 30;
                } else {
                    graphics.drawString(next + "", i, 30 + (next / 2));
                    graphics.setColor(Color.red);
                    graphics.drawLine(prevX, prevY, i, 30 + (next / 2));
                    prevY = 30 + (next / 2);
                }
                prevX = i;
            } else {
                e("One of the ranking tokens is not a number", index);
            }
        }
    }

    // Creates the graphics panel with only the basic stuff
    private static Graphics createInitialGraph(String name) {
        DrawingPanel panel = new DrawingPanel(decadeWidth * numberOfDecades, 560);
        Graphics graphics = panel.getGraphics();
        graphics.setColor(Color.yellow);
        graphics.fillRect(0, 0, decadeWidth * numberOfDecades, 30);
        graphics.setColor(Color.black);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 16));
        graphics.drawString("Ranking of name \"" + name + "\":", 0, 16);
        graphics.setColor(Color.lightGray);
        for (int i = decadeWidth; i <= decadeWidth * numberOfDecades; i += decadeWidth) {
            graphics.drawLine(i, 30, i, 530);
        }
        for (int i = 30; i <= 30 + (numberOfDecades - 1) * decadeWidth; i += decadeWidth) {
            graphics.drawLine(0, i, numberOfDecades * decadeWidth, i);
        }
        graphics.setColor(Color.yellow);
        graphics.fillRect(0, 530, decadeWidth * numberOfDecades, 30);
        graphics.setColor(Color.black);
        int iterator = startingYear;
        for (int i = 0; i <= numberOfDecades * decadeWidth; i += decadeWidth) {
            graphics.drawString(iterator + "", i, 550);
            iterator += 10;
        }
        return graphics;
    }

    // Prints console intro
    private static String printIntro() {
        $("This program graphs the popularity if a name");
        $("in Social Security baby name statistics");
        $("recorded since the year " + startingYear + ".");
        $("");
        $$("Type a name: ");
        String userInput = scanner.next();
        $("Popularity ranking of name \"" + green(userInput) + "\"");
        return userInput;
    }

    // Deals with an unhandled exception
    private static void _dumpException(Exception e) {
        e("Unknown error");
        $$("Would you like to print detailed error information? (y/n) ");
        if (scanner.hasNext()) {
            String data = scanner.next();
            if (data.equalsIgnoreCase("y")) {
                e.printStackTrace();
            } else if (!data.equalsIgnoreCase("n")) {
                w("Invalid input");
            }
        }
    }

    // Prints error message with proper formatting and color
    private static void e(String errorMsg, int lineNumber) {
        e(errorMsg + " (line:" + lineNumber + ")");
    }

    // Prints error without error code
    private static void e(String errorMsg) {
        $("\u001B[31mERR: " + errorMsg + "\u001B[0m");
    }

    // Loads a file
    private static List<String> getFile(String fileName) throws IOException {
        return Files.readAllLines(Paths.get(fileName), StandardCharsets.ISO_8859_1);
    }

    // Always a good sign when you're suppressing warnings
    // Prints a warning message with proper coloring and formatting
    @SuppressWarnings("SameParameterValue")
    private static void w(String warnMsg, int lineNumber) {
        w(warnMsg + " (line:" + lineNumber + ")");
    }

    private static void w(String warnMsg) {
        $("\u001B[33mERR: " + warnMsg + "\u001B[0m");
    }

    // Returns a colored green string
    private static String green(String input) {
        return "\u001B[32m" + input + "\u001B[0m";
    }

    // Prints line to the console
    private static void $(String input) {
        System.out.println(input);
    }

    // Prints segment to the console
    private static void $$(String input) {
        System.out.print(input);
    }
}