package com.svi.solitaire.io;

import com.svi.solitaire.enums.Colors;
import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.DrawMove;

import java.io.PrintStream;

/**
 * All console output. The stream is injected so output can be redirected or tested;
 * colors are ANSI escape codes and can be switched off.
 */
public class ConsoleDisplay {
    private static final int WIDTH = Constants.BANNER_WIDTH.getValue();

    private final PrintStream out;
    private final boolean useColor;

    public ConsoleDisplay(PrintStream out, boolean color) {
        this.out = out;
        this.useColor = color;
    }

    // ------------------------------------------------------------------ menus

    public void displayMainMenu() {
        out.println(style(Colors.BLUE, "=========================== ♠ KLONDIKE SOLITAIRE ♥ ==========================="));
        out.println(style(Colors.BOLD, "                 Every move counts. Every card has its place.\n"));
        out.println(style(Colors.BOLD, "                               SELECT DRAW MODE \n"));
        out.println(style(Colors.BOLD, "                               [1] Draw 1 Card"));
        out.println(style(Colors.BOLD, "                               [3] Draw 3 Cards"));
        out.println(style(Colors.BLUE, "=============================================================================="));
    }

    /** Prints a centred menu block: title, options, closing rule. */
    public void showMenu(String title, String... options) {
        out.println(style(Colors.BOLD, center(title)));
        for (String option : options) {
            out.println(style(Colors.BOLD, center(option)));
        }
        out.println(style(Colors.BLUE, "=".repeat(WIDTH)));
    }

    public void showMessage(String message) {
        out.println(message);
    }
    public void showError(String message) {
        out.println(style(Colors.RED, "Error: " + message));
    }

    /** Prints a question without a line break and leaves the cursor after it. */
    public void prompt(String text) {
        out.print(style(Colors.GREEN, text));
        out.flush();
    }

    // ---------------------------------------------------------------- results
    public void showSimulationHeader(int games, DrawMove drawMove, String shuffleName) {
        out.println();
        out.println("  Simulating " + games + " game" + (games == 1 ? "" : "s")
                + "  |  Draw " + drawMove.getCount() + "  |  " + shuffleName);
    }


    // ---------------------------------------------------------------- helpers

    /** Wraps text in an ANSI colors; plain text when colors are off. */
    private String style(Colors color, String text) {
        return useColor ? color.colorize(text) : text;
    }

    private static String center(String text) {
        int left = Math.max(0, (WIDTH - text.length()) / 2);
        return " ".repeat(left) + text;
    }
}
