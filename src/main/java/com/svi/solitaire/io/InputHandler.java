package com.svi.solitaire.io;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Reads the user's answers. The scanner (input) and the display (prompts, errors) are injected,
 * so nothing here touches System.in / System.out directly.
 */
public class InputHandler {
    private final Scanner scanner;
    private final ConsoleDisplay display;

    public InputHandler(Scanner scanner, ConsoleDisplay display) {
        this.scanner = scanner;
        this.display = display;
    }

    /**
     * Prints the prompt and returns the typed line, or null when the input has ended.
     */
    public String readLine(String prompt) {
        display.prompt(prompt);
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /** Asks for 1 or 3 cards per draw. Repeats until valid; blank or ended input gives 1. */
    public int askDrawCount() {
        while (true) {
            String answer = readLine("Enter your choice : ");
            if (answer == null || answer.isBlank()) {
                return 1;
            }
            String trimmed = answer.trim();
            if (trimmed.equals("1") || trimmed.equals("3")) {
                return Integer.parseInt(trimmed);
            }
            display.showError("Please enter 1 or 3.");
        }
    }

    /**
     * Asks which deck file to load. Repeats until a name is typed; returns null only when the input has ended.
     */
    public String askDeckFile() {
        while (true) {
            String answer = readLine("Enter the deck file to load (e.g. input.txt) : ");
            if (answer == null) {
                return null;
            }
            answer = answer.trim().replace("\"", "");   // tolerate pasted "Copy as path" quotes
            if (!answer.isEmpty()) {
                return answer;
            }
            display.showError("Please enter a file name.");
        }
    }

    /** Repeats until one of the allowed numbers is entered. Throws {@link InputEndedException} if input ends. */
    public int askChoice(String prompt, int... allowed) {
        while (true) {
            String answer = readLine(prompt);
            if (answer == null) {
                throw new InputEndedException();
            }
            try {
                int value = Integer.parseInt(answer.trim());
                for (int option : allowed) {
                    if (option == value) {
                        return value;
                    }
                }
            } catch (NumberFormatException ignored) {
                // falls through to the error message
            }
            display.showError("Please enter one of: " + Arrays.toString(allowed));
        }
    }

//    /** Asks for a number in [min, max]; blank input gives the default. */
//    public int askNumber(String prompt, int min, int max, int defaultValue) {
//        while (true) {
//            String answer = readLine(prompt + " [" + defaultValue + "] : ");
//            if (answer == null) {
//                throw new InputEndedException();
//            }
//            if (answer.isBlank()) {
//                return defaultValue;
//            }
//            try {
//                int value = Integer.parseInt(answer.trim());
//                if (value >= min && value <= max) {
//                    return value;
//                }
//            } catch (NumberFormatException ignored) {
//                // falls through to the error message
//            }
//            display.showError("Please enter a number from " + min + " to " + max + ".");
//        }
//    }

    /** Thrown when the input has ended (Ctrl+D / end of piped input). */
    public static class InputEndedException extends RuntimeException {
        public InputEndedException() {
            super("Input ended");
        }
    }
}
