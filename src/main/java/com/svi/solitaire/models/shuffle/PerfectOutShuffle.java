package com.svi.solitaire.models.shuffle;

/** Perfect out-shuffle: the original top card stays on top. */
public class PerfectOutShuffle extends PerfectShuffle {

    public PerfectOutShuffle() {
        this(1);
    }

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
