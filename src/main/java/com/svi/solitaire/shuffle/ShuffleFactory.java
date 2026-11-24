package com.svi.solitaire.shuffle;

import java.util.Random;

/**
 * Creates the shuffle strategies, so nothing else has to know the concrete shuffle classes.
 * The random source is injected.
 */
public class ShuffleFactory {

    private final Random random;

    /**
     * Creates a factory.
     *
     * @param random the random source used by {@link #random()}
     */
    public ShuffleFactory(Random random) {
        this.random = random;
    }

    /**
     * Keeps the deck in the order it was loaded.
     *
     * @return a shuffle that changes nothing
     */
    public ShuffleStrategy none() {
        return new NoShuffle();
    }

    /**
     * A random shuffle that uses the injected random source.
     *
     * @return a Fisher-Yates shuffle
     */
    public ShuffleStrategy random() {
        return new RandomShuffle(random);
    }

    /**
     * A random shuffle with its own seeded generator (reproducible, safe to use from parallel workers).
     *
     * @param seed the seed of the new generator
     * @return a Fisher-Yates shuffle that always gives the same result for the same seed
     */
    public ShuffleStrategy seeded(long seed) {
        return new RandomShuffle(new Random(seed));
    }

    /**
     * A perfect in-shuffle.
     *
     * @param rounds how many times the deck is shuffled
     * @return the shuffle
     */
    public ShuffleStrategy perfectIn(int rounds) {
        return new PerfectInShuffle(rounds);
    }

    /**
     * A perfect out-shuffle.
     *
     * @param rounds how many times the deck is shuffled
     * @return the shuffle
     */
    public ShuffleStrategy perfectOut(int rounds) {
        return new PerfectOutShuffle(rounds);
    }
}
