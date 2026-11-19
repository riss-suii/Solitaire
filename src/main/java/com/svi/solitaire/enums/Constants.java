package com.svi.solitaire.enums;

/**
 * Fixed numbers that define a game of Klondike solitaire.
 */
public enum Constants {
    TABLEAU_COUNT(7),
    FOUNDATION_COUNT(4),
    DECK_LENGTH(52),
    CARDS_PER_SUIT(13);

    private final int value;

    Constants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
