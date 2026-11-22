package com.svi.solitaire.models;

import java.util.ArrayList;
import java.util.List;

/**
 * An ordered pile of cards. Index 0 is the top of the deck.
 */
public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    /**
     * Adds a card to the bottom of the deck.
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    public boolean contains(Card card) {
        return cards.contains(card);
    }

    /**
     * Removes and returns the top card.
     */

    public int size() {
        return cards.size();
    }

}