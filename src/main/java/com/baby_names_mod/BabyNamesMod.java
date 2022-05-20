package com.baby_names_mod;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.stream.IntStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

// The scuffed coding style should work as a deterrent to plagiarism.
public class BabyNamesMod {

    private static final int startingYear = 1900;
    private static final int numberOfDecades = 11;
    private static final int decadeWidth = 50;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            final InputStream serviceAccount = new FileInputStream("api-key.json");
            final GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

            final Firestore db = FirestoreClient.getFirestore();
            final String name = printIntro();
            final DocumentReference doc = db.collection("names").document(name);
            final Map<String, Object> personData = doc.get().get().getData();
            if (personData == null) {
                e("The name \"" + name + "\" was not found in the database.");
                return;
            }
            final List<Long> years = (List<Long>) personData.get("years");
            IntStream.range(0, 11).forEach((i) -> {
                $(startingYear + i * 10 + ": " + years.get(i));
            });
            render(name, years);
        } catch (Exception e) {
            _dumpException(e);
        }

    }

    /**
     * Renders the drawing panel.
     * 
     * @param name  The name of the person you wish to render.
     * @param years The year data for the person.
     */
    private static void render(String name, List<Long> years) {
        final Graphics graphics = createInitialGraph(name);
        int prevX = 0;
        int prevY = 530;
        for (int i = 0; i <= decadeWidth * (numberOfDecades - 1); i += decadeWidth) {
            long next = years.get(i / decadeWidth);
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
                graphics.drawString(next + "", i, (int) (30 + (next / 2)));
                graphics.setColor(Color.red);
                graphics.drawLine(prevX, prevY, i, (int) (30 + (next / 2)));
                prevY = 30 + ((int) next / 2);
            }
            prevX = i;
        }
    }

    /**
     * Starts the work on creating the graph.
     * 
     * @param name The name of the person you wish to render.
     * @return The graphics object.
     */
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

    /**
     * Prints the intro to the program.
     * 
     * @return The name of the person the user wishes to render.
     */
    private static String printIntro() {
        $("This program graphs the popularity of a name");
        $("in Social Security baby name statistics");
        $("recorded since the year " + startingYear + ".");
        $("");
        $$("Type a name: ");
        String userInput = scanner.next();
        $("Popularity ranking of name \"" + green(userInput) + "\":");
        return userInput;
    }

    /**
     * Handles an unknown exception.
     * 
     * @param e The unhandled exception.
     */
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

    /**
     * Prints an error message to the console.
     * 
     * @param errorMsg The error message to print.
     */
    private static void e(String errorMsg) {
        $("\u001B[31mERR: " + errorMsg + "\u001B[0m");
    }

    /**
     * Prints a warning message with the specified string.
     * 
     * @param warnMsg The warning message to print.
     */
    private static void w(String warnMsg) {
        $("\u001B[33mERR: " + warnMsg + "\u001B[0m");
    }

    /**
     * Formats a string to be printed in green.
     * 
     * @param input The string to be formatted.
     * @return The formatted string.
     */
    private static String green(String input) {
        return "\u001B[32m" + input + "\u001B[0m";
    }

    /**
     * Prints a string to the console.
     * 
     * @param input The string to print.
     */
    private static void $(String input) {
        System.out.println(input);
    }

    /**
     * Prints a string to the console without a newline.
     * 
     * @param input The string to print.
     */
    private static void $$(String input) {
        System.out.print(input);
    }
}