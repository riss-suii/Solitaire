package com.svi.solitaire.models.simulation;

import java.util.concurrent.atomic.AtomicInteger;

/** Thread-safe accumulator for simulation results. */
public class SimulationStatistics {

    private final AtomicInteger gamesPlayed = new AtomicInteger();
    private final AtomicInteger wins = new AtomicInteger();

    public void record(boolean won) {
        gamesPlayed.incrementAndGet();
        if (won) {
            wins.incrementAndGet();
        }
    }

    public int getGamesPlayed() {
        return gamesPlayed.get();
    }

    public int getWins() {
        return wins.get();
    }

    public int getLosses() {
        return getGamesPlayed() - getWins();
    }

    public double getWinRate() {
        int games = getGamesPlayed();
        return games == 0 ? 0 : 100.0 * getWins() / games;
    }
}
