package com.svi.solitaire.enums;

/**
 * Every command a player can issue.
 */
public enum MoveType {
    DRAW,
    WASTE_TO_TABLEAU,
    WASTE_TO_FOUNDATION,
    TABLEAU_TO_FOUNDATION,
    TABLEAU_TO_TABLEAU,
    FOUNDATION_TO_TABLEAU,
    QUIT;

    /** True for commands that change the board (as opposed to HINT, HELP, QUIT). */
    public boolean isBoardMove() {
        return this != QUIT;
    }
}