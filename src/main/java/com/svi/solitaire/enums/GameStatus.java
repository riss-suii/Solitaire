package com.svi.solitaire.enums;

/**
 * How a game ended.
 */
public enum GameStatus {
    /** All 52 cards reached the foundations. */
    WON,
    /** No useful move was left, or a limit in {@code PlayLimits} was reached. */
    LOST
}
