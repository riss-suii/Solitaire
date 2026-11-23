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

    public static Suit   fromSymbol(String text) {
        for (Suit currentSuit : values()) {
            if (currentSuit.symbol.equals(text)) {
                return currentSuit;
            }
        }
        return null;
    }
}
