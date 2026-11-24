package com.svi.solitaire.shuffle;

/** Perfect in-shuffle: the original top card moves to the second position. */
public class PerfectInShuffle extends PerfectShuffle {

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
