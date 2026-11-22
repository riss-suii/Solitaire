package com.svi.solitaire.models.game;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.DrawMove;
import com.svi.solitaire.models.Deck;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.shuffle.ShuffleStrategy;
import com.svi.solitaire.utils.DeckUtil;

/**
 * Shuffles a deck with the given strategy and deals a new Klondike layout.
 * It holds no state, so one instance serves every game.
 */
public class GameInitializer {

    /** Starting deck -> shuffle (the starting deck itself is left untouched) -> deal. */
    public GameState newGame(Deck startingDeck, ShuffleStrategy shuffle, DrawMove drawMove) {
        Deck deck = new Deck(startingDeck.getCards());
        deck.shuffle(shuffle);
        return deal(deck, drawMove);
    }

    /**
     * Deals the deck as is (no shuffling): the tableau is dealt row by row, the last card of each
     * pile face up, and the remaining 24 cards form the stock in deck order. The deck passed in is
     * left untouched; the cards are dealt from a copy.
     */
    public GameState deal(Deck deck, DrawMove drawMove) {
        if (!DeckUtil.isCompleteDeck(deck.getCards())) {
            throw new IllegalArgumentException("A deck must contain each of the 52 cards exactly once");
        }
        Deck remaining = new Deck(deck.getCards());
        int piles = Constants.TABLEAU_COUNT.getValue();
        GameState state = new GameState(drawMove);

        for (int row = 0; row < piles; row++) {
            for (int pile = row; pile < piles; pile++) {
                state.dealToTableau(pile, remaining.deal(), pile == row);
            }
        }
        state.setStock(remaining.drain());
        return state;
    }
}
