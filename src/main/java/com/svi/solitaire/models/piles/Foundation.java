package com.svi.solitaire.models.piles;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.util.CardFactory;
import com.svi.solitaire.models.Card;

/**
 * The four foundation piles. Each one is built Ace to King in a single suit,
 * so only its height needs to be stored (the index is the suit's ordinal).
 *
 * <p>This class depends on the declaration order of the enums: {@link Suit#ordinal()} is the index
 * into the height array, and a pile of height {@code h} has the card at position {@code h - 1} of
 * {@link Rank#values()} on top, so {@code Rank} must be declared from Ace up to King. Do not reorder
 * either enum without updating this class.
 */
public class Foundation {

    private static final Rank[] RANKS = Rank.values();

    private final int[] heights;

    public Foundation() {
        this.heights = new int[GameConstants.FOUNDATION_COUNT];
    }

    /**
     * @param suit the suit of the pile
     * @return how many cards are on that suit's pile (0 to 13)
     */
    public int height(Suit suit) {
        return heights[suit.ordinal()];
    }

    /**
     * @param suitIndex the suit's ordinal
     * @return how many cards are on that suit's pile (0 to 13)
     */
    public int height(int suitIndex) {
        return heights[suitIndex];
    }

    /** The top card of a suit's pile, or null when it is empty. */
    public Card top(Suit suit) {
        int height = heights[suit.ordinal()];
        return height == 0 ? null : new Card(Rank.values()[height - 1], suit);
    }

    /**
     * Adds the next card of a suit to its pile by raising the height by one.
     *
     * @param suit the suit of the pile
     * @throws IllegalStateException if the pile already holds all 13 cards
     */
    public void push(Suit suit) {
        if (heights[suit.ordinal()] >= GameConstants.CARDS_PER_SUIT) {
            throw new IllegalStateException("The " + suit + " foundation is already complete.");
        }
        heights[suit.ordinal()]++;
    }

    /**
     * Removes the top card of a suit's pile by lowering the height by one.
     *
     * @param suit the suit of the pile
     * @throws IllegalStateException if the pile is empty
     */
    public void pop(Suit suit) {
        if (heights[suit.ordinal()] == 0) {
            throw new IllegalStateException("The " + suit + " foundation is empty.");
        }
        heights[suit.ordinal()]--;
    }

    /**
     * @return how many cards are on all four piles together
     */
    public int total() {
        int total = 0;
        for (int height : heights) {
            total += height;
        }
        return total;
    }
}
