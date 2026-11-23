package com.svi.solitaire.models.shuffle;

import com.svi.solitaire.models.Card;

import java.util.ArrayList;
import java.util.List;

/** Leaves the deck in the order it was loaded, so the same file always deals the same game. */
public class NoShuffle implements ShuffleStrategy {

    @Override
    public List<Card> shuffle(List<Card> cards) {
        return new ArrayList<>(cards);
    }

    @Override
    public String getName() {
        return "None (deck as loaded)";
    }
}
