package com.svi.solitaire.models.shuffle;

import java.util.Random;

/**
 * Creates the shuffle strategies, so nothing else has to know the concrete shuffle classes.
 * The random source is injected.
 */
public class ShuffleFactory {

    private final Random random;

    public ShuffleFactory(Random random) {
        this.random = random;
    }

    /** A random shuffle that uses the injected random source. */
    public ShuffleStrategy random() {
        return new RandomShuffle(random);
    }

    /** A random shuffle with its own seeded generator (reproducible, safe to use from parallel workers). */
    public ShuffleStrategy seeded(long seed) {
        return new RandomShuffle(new Random(seed));
    }

    public ShuffleStrategy perfectIn(int rounds) {
        return new PerfectInShuffle(rounds);
    }

    public ShuffleStrategy perfectOut(int rounds) {
        return new PerfectOutShuffle(rounds);
    }
}
