package com.svi.solitaire.enums;

/**
 * The declaration order is also the foundation order (index = ordinal()).
 */
public enum Suit {
    HEARTS("H", true),
    DIAMONDS("D", true),
    CLUBS("C", false),
    SPADES("S", false);

    private final String symbol;
    private final boolean red;

    Suit(String symbol, boolean red) {
        this.symbol = symbol;
        this.red = red;
    }

    public String getSymbol() {
        return symbol;
    }

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
