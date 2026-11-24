package com.svi.solitaire.enums;

/**
 * The four suits. The declaration order is also the foundation order (index = {@code ordinal()}).
 */
public enum Suit {
    /** Hearts (red). */
    HEARTS("H", true),
    /** Diamonds (red). */
    DIAMONDS("D", true),
    /** Clubs (black). */
    CLUBS("C", false),
    /** Spades (black). */
    SPADES("S", false);

    private final String symbol;
    private final boolean red;

    Suit(String symbol, boolean red) {
        this.symbol = symbol;
        this.red = red;
    }

    /**
     * Returns the symbol used in deck files and on the board, for example {@code H}.
     *
     * @return the symbol used in deck files and on the board, for example {@code H}
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns true for hearts and diamonds, false for clubs and spades.
     *
     * @return true for hearts and diamonds, false for clubs and spades
     */
    public boolean isRed() {
        return red;
    }

    /**
     * Finds the suit with the given symbol.
     *
     * @param text the suit symbol, for example {@code D}
     * @return the matching suit
     * @throws IllegalArgumentException if no suit has that symbol
     */
    public static Suit fromSymbol(String text) {
        for (Suit currentSuit : values()) {
            if (currentSuit.symbol.equals(text)) {
                return currentSuit;
            }
        }
        throw new IllegalArgumentException("Unknown suit \"" + text + "\"");
    }
}
