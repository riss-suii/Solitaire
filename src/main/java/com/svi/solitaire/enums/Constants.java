package com.svi.solitaire.enums;

/**
 * Fixed numbers that define a game of Klondike solitaire and the solver.
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

    /** Search budget for one game (distinct positions explored, all attempts together). */
    DEFAULT_MAX_NODES(300_000),
    /** The budget is split over this many search attempts (see SolitaireStrategy). */
    SEARCH_RESTARTS(40),

    BANNER_WIDTH(78),
    DEFAULT_SIMULATION_GAMES(1_000),
    MAX_SIMULATION_GAMES(100_000);

    private final int value;

    Constants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
