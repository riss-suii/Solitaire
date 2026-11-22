package com.svi.solitaire.models.tableau;

import com.svi.solitaire.models.Card;

/**
 * The face-up pile next to the stock. The last card is the top (playable) card.
 */
public class Waste extends Talon {

    /** The top card, or null when empty. */
    public Card top() {
        return cards.isEmpty() ? null : cards.getLast();
    }

    public void add(Card card) {
        cards.add(card);
    }

    public Card removeTop() {
        return cards.removeLast();
    }
}
