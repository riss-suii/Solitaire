package com.svi.solitaire.shuffle;

import com.svi.solitaire.models.Card;

import java.util.List;

/** Strategy for reordering a deck. Implementations must not modify the input and return a new, modifiable list. */
public interface ShuffleStrategy {

    List<Card> shuffle(List<Card> cards);

    default String getName() {
        return getClass().getSimpleName();
    }
}
