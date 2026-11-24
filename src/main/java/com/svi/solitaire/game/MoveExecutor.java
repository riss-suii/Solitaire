package com.svi.solitaire.game;

import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.BoardMove;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.rules.GameRules;

/**
 * Applies moves to a {@link GameState}. The moves should already be checked with
 * {@code GameRules.isValid}. The talon only stores a list and a pointer, so drawing, recycling and
 * taking the waste card are done here, and each one throws if the talon is in the wrong state for it.
 */
public class MoveExecutor {

    private final GameRules rules;

    /**
     * Creates a move executor. It holds no per-game state.
     *
     * @param rules decides how many cards a draw turns over
     */
    public MoveExecutor(GameRules rules) {
        this.rules = rules;
    }

    /**
     * Applies the move in place. When a move uncovers a face-down tableau card, it is turned over.
     *
     * @param state the position to change
     * @param move  a move already checked with {@code GameRules.isValid}
     * @throws IllegalStateException if a draw, recycle or waste move does not fit the talon
     */
    public void execute(GameState state, BoardMove move) {
        switch (move.type()) {
            case DRAW -> draw(state);
            case RECYCLE -> recycle(state);
            case WASTE_TO_TABLEAU -> state.addTableauTop(move.to(), takeWasteTop(state));
            case WASTE_TO_FOUNDATION -> state.addToFoundation(takeWasteTop(state).getSuit());
            case TABLEAU_TO_FOUNDATION -> {
                Card card = state.removeTableauTop(move.from());
                state.revealTopIfHidden(move.from());
                state.addToFoundation(card.getSuit());
            }
            case TABLEAU_TO_TABLEAU -> moveRun(state, move);
            case FOUNDATION_TO_TABLEAU -> {
                Suit suit = Suit.values()[move.from()];
                Card card = state.getTopFoundationCard(suit);
                state.removeFromFoundation(suit);
                state.addTableauTop(move.to(), card);
            }
        }
    }

    /** Moves the pointer past the next 1 or 3 stock cards (or what is left), turning them onto the waste. */
    private void draw(GameState state) {
        if (state.getStockSize() == 0) {
            throw new IllegalStateException("Cannot draw: the stock is empty.");
        }
        state.setWasteSize(state.getWasteSize() + rules.cardsToDraw(state));
    }

    /** Moves the pointer back to 0, so the waste becomes the stock again in the same order. */
    private void recycle(GameState state) {
        if (state.getStockSize() > 0) {
            throw new IllegalStateException("Cannot recycle: the stock is not empty.");
        }
        state.setWasteSize(0);
    }

    /** Removes the top waste card from the talon and moves the pointer down by one. */
    private Card takeWasteTop(GameState state) {
        int top = state.getWasteSize() - 1;
        if (top < 0) {
            throw new IllegalStateException("Cannot play from the waste: it is empty.");
        }
        state.setWasteSize(top);
        return state.removeTalonCard(top);
    }

    private void moveRun(GameState state, BoardMove move) {
        Card[] run = new Card[move.count()];
        for (int i = run.length - 1; i >= 0; i--) {
            run[i] = state.removeTableauTop(move.from());
        }
        state.revealTopIfHidden(move.from());
        for (Card card : run) {
            state.addTableauTop(move.to(), card);
        }
    }
}
