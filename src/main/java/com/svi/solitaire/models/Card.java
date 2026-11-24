package com.svi.solitaire.models;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;

public class Card {
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public boolean isRed() {
        return suit.isRed();
    }

    /** Unique id 0..51 (used for hashing positions). */
    public int id() {
        return suit.ordinal() * GameConstants.CARDS_PER_SUIT + rank.ordinal();
    }

    /** Identity is rank + suit. Whether a card is face up is tracked by its tableau pile in GameState. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card other)) return false;
        return rank == other.rank && suit == other.suit;
    }

    @Override
    public int hashCode() {
        return id();
    }

    @Override
    public String toString() {
        {
            return this.rank.getSymbol() + "-" + this.suit.getSymbol();
        }
    }
}
