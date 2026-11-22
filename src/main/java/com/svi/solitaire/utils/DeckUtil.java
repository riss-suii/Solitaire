package com.svi.solitaire.utils;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;

import java.util.ArrayList;
import java.util.List;

public final class DeckUtil {

    private DeckUtil() {
    }

    /** A new, ordered deck: each suit Ace to King. */
    public static List<Card> createStandardDeck() {
        List<Card> cards = new ArrayList<>(Constants.TOTAL_CARDS.getValue());
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
        return cards;
    }

    /** True when the list holds each of the 52 cards exactly once. */
    public static boolean isCompleteDeck(List<Card> cards) {
        int total = Constants.TOTAL_CARDS.getValue();
        if (cards.size() != total) {
            return false;
        }
        boolean[] seen = new boolean[total];
        for (Card card : cards) {
            if (seen[card.id()]) {
                return false;
            }
            seen[card.id()] = true;
        }
        return true;
    }
}
