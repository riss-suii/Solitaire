package com.svi.solitaire.enums;

/**
 * How many cards are turned over from the stock at a time, and the score
 * penalty applied when the waste pile is recycled back into the stock.
 */
public enum DrawMove {
    ONE(1),
    THREE(3);

    private final int count;

    DrawMove(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public static DrawMove fromCount(int count) {
        for (DrawMove mode : values()) {
            if (mode.count == count) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Draw mode must be 1 or 3, got: " + count);
    }
}