package com.svi.solitaire.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An ordered pile of cards. Index 0 is the top of the deck.
 */
public class Deck {
    private final List<Card> cards;

    /** Creates an empty deck. */
    public Deck() {
        this.cards = new ArrayList<>();
    }

    /**
     * Creates a deck holding a copy of the given cards.
     *
     * @param cards the cards, top first
     */
    public Deck(List<Card> cards) {
        this.cards = new ArrayList<>(cards);
    }

    /**
     * Adds a card to the bottom of the deck.
     *
     * @param card the card to add
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Returns true if the deck holds that card.
     *
     * @param card the card to look for
     * @return true if the deck holds that card
     */
    public boolean contains(Card card) {
        return cards.contains(card);
    }

    /**
     * Removes and returns the top card.
     *
     * @return the top card
     * @throws IllegalStateException if the deck is empty
     */
    public Card deal() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty.");
        }
        return cards.removeFirst();
    }

    /**
     * Removes and returns every remaining card, top first.
     *
     * @return the cards that were left; the deck is empty afterwards
     */
    public List<Card> drain() {
        List<Card> rest = new ArrayList<>(cards);
        cards.clear();
        return rest;
    }

    /**
     * Returns the number of cards in the deck.
     *
     * @return the number of cards in the deck
     */
    public int count() {
        return cards.size();
    }

    /**
     * Returns a read-only view of the cards, top first.
     *
     * @return a read-only view of the cards, top first
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(cards);
    }
}
