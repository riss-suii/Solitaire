package com.svi.solitaire.models.engine;

import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.tableau.Manoeuvre;

/** Applies moves to a {@link GameState}. Assumes the move was validated already. */
public class MoveExecutor {

    /** Applies the move in place. */
    public void execute(GameState state, Manoeuvre move) {
        switch (move.type()) {
            case DRAW -> state.draw();
            case RECYCLE -> state.recycle();
            case WASTE_TO_TABLEAU -> state.addTableauTop(move.to(), state.removeWasteTop());
            case WASTE_TO_FOUNDATION -> state.addToFoundation(state.removeWasteTop().getSuit());
            case TABLEAU_TO_FOUNDATION -> {
                Card card = state.removeTableauTop(move.from());
                state.revealTopIfHidden(move.from());
                state.addToFoundation(card.getSuit());
            }
            case TABLEAU_TO_TABLEAU -> moveRun(state, move);
            case FOUNDATION_TO_TABLEAU -> {
                Suit suit = Suit.values()[move.from()];
                Card card = state.topFoundationCard(suit);
                state.removeFromFoundation(suit);
                state.addTableauTop(move.to(), card);
            }
        }
    }

    private void moveRun(GameState state, Manoeuvre move) {
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
