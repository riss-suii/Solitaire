package com.svi.solitaire.enums;

/**
 * How many cards are turned over from the stock at a time.
 */
public enum DrawMode {
    /** Turn over one card at a time. */
    ONE(1),
    /** Turn over three cards at a time. */
    THREE(3);

    private final int count;

    DrawMode(int count) {
        this.count = count;
    }

    /**
     * Returns how many cards one draw turns over.
     *
     * @return how many cards one draw turns over
     */
    public int getCount() {
        return count;
    }

    /**
     * Finds the draw mode that turns over the given number of cards.
     *
     * @param count the number of cards per draw, 1 or 3
     * @return the matching draw mode
     * @throws IllegalArgumentException if no draw mode uses that count
     */
    public static DrawMode fromCount(int count) {
        for (DrawMode mode : values()) {
            if (mode.count == count) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Draw mode must be 1 or 3, got: " + count);
    }
}