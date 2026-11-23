package com.svi.solitaire.enums;

/**
 * Fixed numbers that define a game of Klondike solitaire.
 */
public enum Constants {
    TABLEAU_COUNT(7),
    FOUNDATION_COUNT(4),
    TOTAL_CARDS(52),
    CARDS_PER_SUIT(13),

    /** Cards left in the stock after dealing the tableau (52 - 28). */
    STOCK_SIZE(24),
    /** 6 face-down cards + a full King..Ace run of 13 = 19 at most. */
    MAX_TABLEAU_PILE_SIZE(20),

    /** A game is abandoned after this many moves (draws included). */
    MAX_GAME_MOVES(2000),
    /** A game is lost when the same position comes up more than this many times. */
    MAX_POSITION_REPEATS(2),

    BANNER_WIDTH(78),
    MAX_SIMULATION_GAMES(100000);

    private final int value;

    Constants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
