package com.svi.solitaire.models.piles;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.models.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The stock and the waste, stored as one list of cards and a pointer between them. This class only
 * holds data; drawing, recycling and playing the waste card are done by {@code MoveExecutor}.
 *
 * <p>Cards at positions {@code [0, pointer)} are the waste (face up, the last of them is the
 * playable top card). Cards at positions {@code [pointer, size)} are the stock (face down, the
 * card at {@code pointer} is the next one drawn).
 *
 * <p>Callers must keep the pointer and the list consistent. The only guard is that the pointer can
 * never be set outside {@code [0, size]}.
 */
public class Talon {

    private final List<Card> cards = new ArrayList<>(GameConstants.STOCK_SIZE);

    /** Boundary between waste and stock: the number of cards currently in the waste. */
    private int pointer;

    /** Creates an empty talon. */
    public Talon() {
    }

    /**
     * Returns a read-only view of all the cards, waste first, then the stock.
     *
     * @return a read-only view of all the cards, waste first, then the stock
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }

    /**
     * Replaces the cards with a copy of the given list and moves the pointer to 0, so all of them
     * are in the stock.
     *
     * @param newCards the new cards, the first one is the next drawn
     */
    public void setCards(List<Card> newCards) {
        cards.clear();
        cards.addAll(newCards);
        pointer = 0;
    }

    /**
     * Removes the card at a position. The pointer is not changed.
     *
     * @param index the position in the list
     * @return the removed card
     * @throws IndexOutOfBoundsException if the index is outside the list
     */
    public Card removeCard(int index) {
        return cards.remove(index);
    }

    /**
     * Returns the pointer: the number of cards in the waste, which is also where the stock starts.
     *
     * @return the pointer
     */
    public int getPointer() {
        return pointer;
    }

    /**
     * Sets the pointer.
     *
     * @param pointer the new number of cards in the waste
     * @throws IndexOutOfBoundsException if the pointer is outside {@code [0, size]}
     */
    public void setPointer(int pointer) {
        if (pointer < 0 || pointer > cards.size()) {
            throw new IndexOutOfBoundsException("Talon pointer " + pointer + " is outside [0, " + cards.size() + "]");
        }
        this.pointer = pointer;
    }
}
