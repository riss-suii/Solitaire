package com.svi.solitaire.enums;

/**
 * The thirteen ranks of a suit. The declaration order, Ace up to King, is relied on elsewhere
 * (for example by {@code Foundation}), so do not reorder it.
 */
public enum Rank {
    /** Ace (value 1). */
    ACE("A"),
    /** Two. */
    TWO("2"),
    /** Three. */
    THREE("3"),
    /** Four. */
    FOUR("4"),
    /** Five. */
    FIVE("5"),
    /** Six. */
    SIX("6"),
    /** Seven. */
    SEVEN("7"),
    /** Eight. */
    EIGHT("8"),
    /** Nine. */
    NINE("9"),
    /** Ten. */
    TEN("10"),
    /** Jack. */
    JACK("J"),
    /** Queen. */
    QUEEN("Q"),
    /** King (value 13). */
    KING("K");

    private final String symbol;

    Rank(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the symbol used in deck files and on the board, for example {@code A} or {@code 10}.
     *
     * @return the symbol used in deck files and on the board, for example {@code A} or {@code 10}
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the numeric value, Ace = 1 ... King = 13.
     *
     * @return the numeric value, Ace = 1 ... King = 13
     */
    public int getValue() {
        return ordinal() + 1;
    }

    /**
     * Finds the rank with the given symbol.
     *
     * @param text the rank symbol, for example {@code A} or {@code 10}
     * @return the matching rank
     * @throws IllegalArgumentException if no rank has that symbol
     */
    public static Rank fromSymbol(String text) {
        for (Rank currentRank : values()) {
            if (currentRank.symbol.equals(text)) {
                return currentRank;
            }
        }
        throw new IllegalArgumentException("Unknown rank \"" + text + "\"");
    }
}