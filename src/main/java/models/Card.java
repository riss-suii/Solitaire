package models;

import enums.Rank;
import enums.Suit;

public class Card {
    private final Rank rank;
    private final Suit suit;
    private boolean faceUp;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }

    public boolean isFaceUp() { return faceUp; }
    public void setFaceUp(boolean faceUp) { this.faceUp = faceUp; }

//    @Override
//    public int compareTo(Card other) {
//        int result = rank.compareTo(other.rank);
//        if (result != 0) return result;
//        return suit.compareTo(other.suit);
//    }

    @Override
    public String toString() {
        return rank.getSymbol() + suit.getIcon();
    }
}