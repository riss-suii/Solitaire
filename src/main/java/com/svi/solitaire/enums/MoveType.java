package com.svi.solitaire.enums;

/**
 * Every kind of single move in the game.
 */
public enum MoveType {
    /** Turn cards over from the stock onto the waste. */
    DRAW,
    /** Turn the waste back over to become the stock. */
    RECYCLE,
    /** Play the top waste card onto a tableau pile. */
    WASTE_TO_TABLEAU,
    /** Play the top waste card onto its foundation. */
    WASTE_TO_FOUNDATION,
    /** Play the top card of a tableau pile onto its foundation. */
    TABLEAU_TO_FOUNDATION,
    /** Move a face-up card, or a run of them, from one tableau pile to another. */
    TABLEAU_TO_TABLEAU,
    /** Take the top card of a foundation back onto a tableau pile. */
    FOUNDATION_TO_TABLEAU
}

