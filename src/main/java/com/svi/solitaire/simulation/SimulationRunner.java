package com.svi.solitaire.simulation;

import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.enums.GameStatus;
import com.svi.solitaire.models.Deck;
import com.svi.solitaire.game.GameEngine;
import com.svi.solitaire.game.GameInitializer;
import com.svi.solitaire.game.GameState;
import com.svi.solitaire.shuffle.ShuffleStrategy;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/** Plays many games without any display, spread over all CPU cores, and gathers statistics. */
public class SimulationRunner {

    private final GameEngine engine;
    private final GameInitializer initializer;

    public SimulationRunner(GameEngine engine, GameInitializer initializer) {
        this.engine = engine;
        this.initializer = initializer;
    }

    /**
     * @param startingDeck     the deck every game starts from before it is shuffled
     * @param games            number of games
     * @param drawMode         draw 1 or draw 3
     * @param shuffleFor       gives the shuffle to use for game index i
     * @param progressListener called with the number of finished games (may be null); invoked from worker threads
     */
    public SimulationStatistics run(Deck startingDeck, int games, DrawMode drawMode,
                                    IntFunction<ShuffleStrategy> shuffleFor,
                                    IntConsumer progressListener) {
        SimulationStatistics stats = new SimulationStatistics();
        AtomicInteger finished = new AtomicInteger();

        IntStream.range(0, games).parallel().forEach(i -> {
            GameState state = initializer.newGame(startingDeck, shuffleFor.apply(i), drawMode);

            GameEngine.Outcome outcome = engine.play(state, null);
            stats.record(outcome.status() == GameStatus.WON);

            int done = finished.incrementAndGet();
            if (progressListener != null) {
                progressListener.accept(done);
            }
        });
        return stats;
    }
}
