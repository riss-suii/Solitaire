package com.svi.solitaire.models.tableau;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.models.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * What the stock and the waste have in common: an ordered list of cards that can be counted, read
 * by position, emptied, and drained into another list. The subclasses add their own rules for which
 * end the cards are taken from.
 */
public abstract class Talon {

    protected final List<Card> cards = new ArrayList<>(Constants.STOCK_SIZE.getValue());

    public int size() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Card get(int index) {
        return cards.get(index);
    }

    public void clear() {
        cards.clear();
    }

    /** Appends every card (in their current order) to {@code target} and empties this pile. */
    public void drainInto(List<Card> target) {
        target.addAll(cards);
        cards.clear();
    }
}
