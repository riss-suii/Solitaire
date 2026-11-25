package com.svi.solitaire.models;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;

import java.util.Objects;

/**
 * An immutable playing card: a rank and a suit. Use {@code CardFactory} to get the shared instances.
 */
public final class Card {

    /** The rank of the card. */
    private final Rank rank;
    /** The suit of the card. */
    private final Suit suit;

    /**
     * Creates a card.
     *
     * @param rank the rank of the card
     * @param suit the suit of the card
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    /**
     * Returns the rank of the card.
     *
     * @return the rank of the card
     */
    public Rank rank() {
        return rank;
    }

    /**
     * Returns the suit of the card.
     *
     * @return the suit of the card
     */
    public Suit suit() {
        return suit;
    }

    /**
     * Returns true for hearts and diamonds.
     *
     * @return true for hearts and diamonds
     */
    public boolean isRed() {
        return suit.isRed();
    }

    /**
     * Returns a unique id from 0 to 51 (used for hashing positions).
     *
     * @return a unique id from 0 to 51 (used for hashing positions)
     */
    public int id() {
        return suit.ordinal() * GameConstants.CARDS_PER_SUIT + rank.ordinal();
    }

    /**
     * Identity is rank + suit. Whether a card is face up is tracked by its tableau pile in GameState.
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof Card other && rank == other.rank && suit == other.suit;
    }

    /**
     * Returns a hash code based on rank and suit, consistent with {@link #equals(Object)}.
     *
     * @return a hash code based on rank and suit
     */
    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    /**
     * Returns the card written as in deck files, for example {@code D-A} or {@code H-10}.
     *
     * @return the card written as in deck files, for example {@code D-A} or {@code H-10}
     */
    @Override
    public String toString() {
        return suit.getSymbol() + "-" + rank.getSymbol();
    }
}