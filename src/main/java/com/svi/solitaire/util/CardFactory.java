package com.svi.solitaire.util;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;

/**
 * Creates and hands out the 52 cards of a standard deck. Cards are immutable, so each rank and
 * suit combination is built once and the same instance is reused everywhere.
 */
public final class CardFactory {

    private static final Card[][] CARDS = buildAll();

    private CardFactory() {
    }

    private static Card[][] buildAll() {
        Card[][] cards = new Card[Suit.values().length][Rank.values().length];
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards[suit.ordinal()][rank.ordinal()] = new Card(rank, suit);
            }
        }
        return cards;
    }

    /**
     * Returns the shared card for a rank and suit.
     *
     * @param rank the card's rank
     * @param suit the card's suit
     * @return the unique card with that rank and suit
     */
    public static Card of(Rank rank, Suit suit) {
        return CARDS[suit.ordinal()][rank.ordinal()];
    }
}
