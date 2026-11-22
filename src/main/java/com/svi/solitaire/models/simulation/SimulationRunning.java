package com.svi.solitaire.models.simulation;

import com.svi.solitaire.enums.DrawMove;
import com.svi.solitaire.enums.GameStatus;
import com.svi.solitaire.models.Deck;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.game.GameInitializer;
import com.svi.solitaire.models.game.GameRunner;
import com.svi.solitaire.models.shuffle.ShuffleStrategy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/** Plays many games without any display, spread over all CPU cores, and gathers statistics. */
public class SimulationRunning {

    private final GameRunner runner;
    private final GameInitializer initializer;

    public SimulationRunning(GameRunner runner, GameInitializer initializer) {
        this.runner = runner;
        this.initializer = initializer;
    }

    /**
     * @param startingDeck     the deck every game starts from before it is shuffled
     * @param games            number of games
     * @param drawMove         draw 1 or draw 3
     * @param shuffleFor       gives the shuffle to use for game index i
     * @param progressListener called with the number of finished games (may be null); invoked from worker threads
     */
    public SimulationStatistics run(Deck startingDeck, int games, DrawMove drawMove,
                                    IntFunction<ShuffleStrategy> shuffleFor,
                                    IntConsumer progressListener) {
        SimulationStatistics stats = new SimulationStatistics();
        AtomicInteger finished = new AtomicInteger();

        IntStream.range(0, games).parallel().forEach(i -> {
            GameState state = initializer.newGame(startingDeck, shuffleFor.apply(i), drawMove);

            GameRunner.Outcome outcome = runner.play(state, null);
            stats.record(outcome.status() == GameStatus.WON);

            int done = finished.incrementAndGet();
            if (progressListener != null) {
                progressListener.accept(done);
            }
        });
        return stats;
    }
}
