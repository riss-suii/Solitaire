package com.svi.solitaire.models;

import com.svi.solitaire.models.shuffle.ShuffleStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An ordered pile of cards. Index 0 is the top of the deck.
 */
public class Deck {
    private List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();
    }

    public Deck(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    public void shuffle(ShuffleStrategy strategy) {
        this.cards = strategy.shuffle(cards);
    }

    /** Adds a card to the bottom of the deck. */
    public void addCard(Card card) {
        cards.add(card);
    }

    public boolean contains(Card card) {
        return cards.contains(card);
    }

    /** Removes and returns the top card. */
    public Card deal() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty.");
        }
        return cards.removeFirst();
    }

    /** Removes and returns every remaining card, top first. */
    public List<Card> drain() {
        List<Card> rest = new ArrayList<>(cards);
        cards.clear();
        return rest;
    }

    public int count() {
        return cards.size();
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }
}
