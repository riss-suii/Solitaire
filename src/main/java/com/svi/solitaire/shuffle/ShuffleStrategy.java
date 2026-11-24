package com.svi.solitaire.shuffle;

import com.svi.solitaire.models.Card;

import java.util.List;

/** Strategy for reordering a deck. Implementations must not modify the input and return a new, modifiable list. */
public interface ShuffleStrategy {

    /**
     * Reorders a list of cards.
     *
     * @param cards the cards to shuffle; not modified
     * @return a new, modifiable list holding the same cards in the new order
     */
    List<Card> shuffle(List<Card> cards);

    /**
     * Returns the name shown to the user.
     *
     * @return a short name, by default the simple class name
     */
    default String getName() {
        return getClass().getSimpleName();
    }
}
