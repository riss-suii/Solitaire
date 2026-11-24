package com.svi.solitaire.constants;

/**
 * Limits that keep games and simulations finite. They are choices of this program, not rules of
 * Klondike.
 */
public final class PlayLimits {

    /** A game is abandoned after this many moves (draws included). */
    public static final int MAX_GAME_MOVES = 2000;
    /** A game is lost when the same position comes up more than this many times. */
    public static final int MAX_POSITION_REPEATS = 2;
    /** Largest number of games accepted for one simulation run. */
    public static final int MAX_SIMULATION_GAMES = 100000;

    private PlayLimits() {
    }
}
