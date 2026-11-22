package com.svi.solitaire.models.tableau;

import com.svi.solitaire.models.Card;

import java.util.List;

/**
 * The face-down draw pile. Index 0 is the next card to be drawn.
 */
public class Stock extends Talon {

    /** Replaces the contents; the first card in the list will be drawn first. */
    public void load(List<Card> newCards) {
        cards.clear();
        cards.addAll(newCards);
    }

    /** Turns the waste over: it becomes the stock, in the same order, and the waste is emptied. */
    public void refillFrom(Waste waste) {
        cards.clear();
        waste.drainInto(cards);
    }

    /** Removes and returns the next card to be drawn. */
    public Card draw() {
        return cards.removeFirst();
    }
}
