package com.svi.solitaire.simulation;

import java.util.concurrent.atomic.AtomicInteger;

/** Thread-safe accumulator for simulation results. */
public class SimulationStatistics {

    private final AtomicInteger gamesPlayed = new AtomicInteger();
    private final AtomicInteger wins = new AtomicInteger();

    /** Creates an empty set of statistics. */
    public SimulationStatistics() {
    }

    /**
     * Records the result of one game.
     *
     * @param won true if the game was won
     */
    public void record(boolean won) {
        gamesPlayed.incrementAndGet();
        if (won) {
            wins.incrementAndGet();
        }
    }

    /**
     * Returns the number of games recorded.
     *
     * @return the number of games recorded
     */
    public int getGamesPlayed() {
        return gamesPlayed.get();
    }

    /**
     * Returns the number of games won.
     *
     * @return the number of games won
     */
    public int getWins() {
        return wins.get();
    }

    /**
     * Returns the number of games lost.
     *
     * @return the number of games lost
     */
    public int getLosses() {
        return getGamesPlayed() - getWins();
    }

    /**
     * Returns the share of games won as a percentage from 0 to 100, or 0 when no game was played.
     *
     * @return the share of games won as a percentage from 0 to 100, or 0 when no game was played
     */
    public double getWinRate() {
        int games = getGamesPlayed();
        return games == 0 ? 0 : 100.0 * getWins() / games;
    }
}
