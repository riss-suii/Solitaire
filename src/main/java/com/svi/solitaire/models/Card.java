package com.svi.solitaire.models;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.utils.CardUtil;

import java.util.Objects;

public class Card {
    private final Rank rank;
    private final Suit suit;
    private boolean faceUp;

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

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isRed() {
        return suit.isRed();
    }

    /** Identity is rank + suit only; the face-up state is not part of it. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card other)) return false;
        return rank == other.rank && suit == other.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    @Override
    public String toString() {
        return CardUtil.label(this);
    }
}
