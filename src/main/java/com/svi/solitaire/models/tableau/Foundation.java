package com.svi.solitaire.models.tableau;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;

/**
 * The four foundation piles. Each one is built Ace to King in a single suit,
 * so only its height needs to be stored (the index is the suit's ordinal).
 */
public class Foundation {

    private final int[] heights;

    public Foundation() {
        this.heights = new int[Constants.FOUNDATION_COUNT.getValue()];
    }

    public int height(Suit suit) {
        return heights[suit.ordinal()];
    }

    public int height(int suitIndex) {
        return heights[suitIndex];
    }

    /** The top card of a suit's pile, or null when it is empty. */
    public Card top(Suit suit) {
        int height = heights[suit.ordinal()];
        return height == 0 ? null : new Card(Rank.values()[height - 1], suit);
    }

    public void push(Suit suit) {
        heights[suit.ordinal()]++;
    }

    public void pop(Suit suit) {
        heights[suit.ordinal()]--;
    }

    public int total() {
        int total = 0;
        for (int height : heights) {
            total += height;
        }
        return total;
    }
}
