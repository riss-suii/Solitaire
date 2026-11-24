package com.svi.solitaire.shuffle;

/** Perfect out-shuffle: the original top card stays on top. */
public class PerfectOutShuffle extends PerfectShuffle {

    /**
     * Creates an out-shuffle.
     *
     * @param rounds how many times the deck is shuffled, at least 1
     */
    public PerfectOutShuffle(int rounds) {
        super(rounds);
    }

    @Override
    protected boolean topHalfFirst() {
        return true;
    }

    @Override
    protected String baseName() {
        return "Perfect Out";
    }
}
