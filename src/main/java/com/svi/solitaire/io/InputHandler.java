package com.svi.solitaire.io;

import java.io.Serial;
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

    /**
     * Creates an input handler.
     *
     * @param scanner where the answers are read from
     * @param display where prompts and error messages are printed
     */
    public InputHandler(Scanner scanner, ConsoleDisplay display) {
        this.scanner = scanner;
        this.display = display;
    }

    /**
     * Prints the prompt and returns the typed line, or null when the input has ended.
     *
     * @param prompt the question to show
     * @return the line the user typed, or null at the end of the input
     */
    public String readLine(String prompt) {
        display.prompt(prompt);
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Asks which deck file to load. Repeats until a name is typed.
     *
     * @return the file name or path, or null when the input has ended
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

    /**
     * Repeats until one of the allowed numbers is entered.
     *
     * @param prompt  the question to show
     * @param allowed the numbers that are accepted
     * @return the number the user entered
     * @throws InputEndedException if the input ends
     */
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

    /**
     * Repeats until a number in the range {@code [min, max]} is entered.
     *
     * @param prompt the question to show
     * @param min    the smallest accepted number
     * @param max    the largest accepted number
     * @return the number the user entered
     * @throws InputEndedException if the input ends
     */
    public int askNumber(String prompt, int min, int max) {
        while (true) {
            String answer = readLine(prompt);
            if (answer == null) {
                throw new InputEndedException();
            }
            try {
                int value = Integer.parseInt(answer.trim());
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // falls through to the error message
            }
            display.showError("Please enter a number from " + min + " to " + max + ".");
        }
    }

    /** Thrown when the input has ended (Ctrl+D / end of piped input). */
    public static class InputEndedException extends RuntimeException {
        @Serial
        private static final long serialVersionUID = 1L;

        /** Creates the exception with the message {@code Input ended}. */
        public InputEndedException() {
            super("Input ended");
        }
    }
}
