package com.svi.solitaire.enums;

/**
 * ANSI escape codes used by the console view.
 */
public enum Colors {
    RESET("\u001B[0m"),
    BOLD("\u001B[1m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    YELLOW("\u001B[38;5;214m"),
    BLUE("\u001B[38;5;18m");

    private final String code;

    Colors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}