package com.svi.solitaire.shuffle;

/** Perfect in-shuffle: the original top card moves to the second position. */
public class PerfectInShuffle extends PerfectShuffle {

    /**
     * Creates an in-shuffle.
     *
     * @param rounds how many times the deck is shuffled, at least 1
     */
    public PerfectInShuffle(int rounds) {
        super(rounds);
    }

    @Override
    protected boolean topHalfFirst() {
        return false;
    }

    @Override
    protected String baseName() {
        return "Perfect In";
    }
}
