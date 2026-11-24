package com.svi.solitaire.models.piles;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.models.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * The stock and the waste modeled as a single sequence of cards with a pointer between them.
 *
 * <p>Cards at positions {@code [0, pointer)} are the waste (face up, the last of them is the
 * playable top card). Cards at positions {@code [pointer, size)} are the stock (face down, the
 * card at {@code pointer} is the next one drawn).
 *
 * <p>Because the two piles share one list, drawing and recycling only move the pointer. No cards
 * are copied between lists, and the stock and waste can never get out of sync.
 */
public class Talon {

    private final List<Card> cards = new ArrayList<>(GameConstants.STOCK_SIZE);

    /** Boundary between waste and stock: the number of cards currently in the waste. */
    private int pointer;

    /**
     * Replaces the contents of the talon with the given cards. All of them start in the stock,
     * with the first card being the next one drawn.
     *
     * @param newCards the cards left over after the tableau has been dealt
     */
    public void load(List<Card> newCards) {
        cards.clear();
        cards.addAll(newCards);
        pointer = 0;
    }

    /**
     * Returns the total number of cards in the talon (waste plus stock).
     *
     * @return the number of cards in both piles combined
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns the number of cards in the waste.
     *
     * @return the number of cards before the pointer
     */
    public int wasteSize() {
        return pointer;
    }

    /**
     * Returns the number of cards in the stock.
     *
     * @return the number of cards from the pointer onward
     */
    public int stockSize() {
        return cards.size() - pointer;
    }

    /**
     * Returns the card at a position in the combined sequence. Positions before
     * {@link #wasteSize()} are waste cards, the rest are stock cards.
     *
     * @param index the position in the combined sequence
     * @return the card at that position
     * @throws IndexOutOfBoundsException if the index is outside {@code [0, size())}
     */
    public Card getPosition(int index) {
        return cards.get(index);
    }

    /**
     * Returns the top (playable) card of the waste without removing it.
     *
     * @return the top waste card, or {@code null} if the waste is empty
     */
    public Card wasteTop() {
        return pointer == 0 ? null : cards.get(pointer - 1);
    }

    /**
     * Moves up to {@code count} cards from the stock onto the waste by advancing the pointer. If
     * the stock holds fewer cards than requested, only the remaining cards are moved.
     *
     * @param count how many cards to draw (for example 1 or 3 depending on the draw mode)
     */
    public void draw(int count) {
        pointer += Math.min(count, stockSize());
    }

    /**
     * Turns the waste back into the stock by resetting the pointer. The order of the cards is
     * kept, so the first card of the old waste is the next one drawn.
     */
    public void recycle() {
        pointer = 0;
    }

    /**
     * Removes and returns the top card of the waste, for example when it is played onto the
     * tableau or a foundation.
     *
     * @return the card that was on top of the waste
     * @throws IllegalStateException if the waste is empty
     */
    public Card removeWasteTop() {
        if (pointer == 0) {
            throw new IllegalStateException("The waste is empty.");
        }
        return cards.remove(--pointer);
    }
}
