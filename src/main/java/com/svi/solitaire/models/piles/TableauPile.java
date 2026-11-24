package com.svi.solitaire.models.piles;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.models.Card;

/**
 * One of the seven tableau piles. The cards are kept in order from the bottom of the pile to the
 * top; the last {@code faceUpCount} cards are face up and the ones below them are face down.
 *
 * <p>This class only stores the pile and exposes low-level operations. Which cards may be placed
 * where is decided by {@code GameRules}.
 */
public class TableauPile {

    private final Card[] cards = new Card[GameConstants.MAX_TABLEAU_PILE_SIZE];
    private int size;
    private int faceUpCount;

    /** @return the number of cards in the pile, face up and face down */
    public int size() {
        return size;
    }

    /** @return true when the pile has no cards */
    public boolean isEmpty() {
        return size == 0;
    }

    /** @return how many cards at the top of the pile are face up */
    public int getFaceUpCount() {
        return faceUpCount;
    }

    /** @return how many cards at the bottom of the pile are face down */
    public int getFaceDownCount() {
        return size - faceUpCount;
    }

    /**
     * @param index position from the bottom of the pile (0 = bottom)
     * @return the card at that position
     */
    public Card getPosition(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Pile index out of range: " + index);
        }
        return cards[index];
    }

    /** @return the top (last) card, or null when the pile is empty */
    public Card getTop() {
        return size == 0 ? null : cards[size - 1];
    }

    /**
     * Puts a card on top of the pile during the initial deal.
     *
     * @param isFaceUp true if the card is dealt face up
     */
    public void deal(Card card, boolean isFaceUp) {
        ensureNotFull();
        cards[size++] = card;
        if (isFaceUp) {
            faceUpCount++;
        }
    }

    /** Puts a card face up on top of the pile (used by moves, not by the initial deal). */
    public void addFaceUp(Card card) {
        ensureNotFull();
        cards[size++] = card;
        faceUpCount++;
    }

    /**
     * Removes and returns the top card.
     *
     * @throws ArrayIndexOutOfBoundsException if the pile is empty
     */
    public Card removeTop() {
        if (size == 0) {
            throw new IllegalStateException("The pile is empty.");
        }
        Card card = cards[--size];
        cards[size] = null;
        if (faceUpCount > 0) {
            faceUpCount--;
        }
        return card;
    }


    /** Turns the top card over when the pile has cards but none of them is face up. */
    public void revealTopIfHidden() {
        if (size > 0 && faceUpCount == 0) {
            faceUpCount = 1;
        }
    }

    private void ensureNotFull() {
        if (size >= cards.length) {
            throw new IllegalStateException("The pile is full.");
        }
    }
}
