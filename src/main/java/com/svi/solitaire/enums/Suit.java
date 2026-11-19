package com.svi.solitaire.enums;

/**
 * The declaration order is also the foundation order (index = ordinal()).
 */
public enum Suit {
    HEARTS('H', "♥", true),
    DIAMONDS('D', "♦", true),
    CLUBS('C', "♣", false),
    SPADES('S', "♠", false);

    private final char letter;
    private final String symbol;
    private final boolean red;

    Suit(char letter, String symbol, boolean red) {
        this.letter = letter;
        this.symbol = symbol;
        this.red = red;
    }

    public char getLetter() {
        return letter;
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean isRed() {
        return red;
    }

    public static Suit fromLetter(char letter) {
        for (Suit suit : values()) {
            if (suit.letter == Character.toUpperCase(letter)) {
                return suit;
            }
        }
        throw new IllegalArgumentException("Unknown suit: '" + letter + "' (use H, D, C or S)");
    }
}
