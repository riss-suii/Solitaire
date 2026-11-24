package com.svi.solitaire.constants;

/**
 * Fixed numbers that define a game of Klondike solitaire.
 */
public final class GameConstants {

    /** Number of tableau piles (columns). */
    public static final int TABLEAU_PILE_COUNT = 7;
    /** Number of foundation piles, one per suit. */
    public static final int FOUNDATION_COUNT = 4;
    /** Cards in a full deck. */
    public static final int TOTAL_CARDS = 52;
    /** Ranks in one suit (Ace to King). */
    public static final int CARDS_PER_SUIT = 13;

    /** Cards left in the stock after dealing the tableau (52 - 28). */
    public static final int STOCK_SIZE = 24;
    /** Capacity of one tableau pile: at most 6 face-down cards plus a full King-to-Ace run of 13 = 19. */
    public static final int MAX_TABLEAU_PILE_SIZE = 19;

    /** Not instantiable: this class only holds constants. */
    private GameConstants() {
    }
}
