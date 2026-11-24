package com.svi.solitaire.enums;

/**
 * ANSI escape codes used by the console view.
 */
public enum Colors {
    /** Clears all styling. */
    RESET("\u001B[0m"),
    /** Bold text. */
    BOLD("\u001B[1m"),
    /** Red text (hearts and diamonds, errors). */
    RED("\u001B[31m"),
    /** Green text (prompts, wins). */
    GREEN("\u001B[32m"),
    /** Orange text (lost games). */
    YELLOW("\u001B[38;5;214m"),
    /** Gray text (face-down cards and empty slots). */
    GRAY("\u001B[90m"),
    /** Cyan text (rules, menus and the playable waste card). */
    CYAN("\u001B[36m");

    private final String code;

    Colors(String code) {
        this.code = code;
    }

    /**
     * Wraps text in this color and a reset code.
     *
     * @param text the text to color
     * @return the text with this color's escape code in front and {@link #RESET} after it
     */
    public String colorize(String text) {
        return code + text + RESET.code;
    }
}