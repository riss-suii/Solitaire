package com.svi.solitaire.utils;

import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.tableau.Manoeuvre;

/**
 * Card text conversion and lookup helpers. The game rules live in {@code GameRules}.
 */
public final class CardUtil {
    private CardUtil() {
    }

    /** The card a move picks up (the bottom card of the run for multi-card moves); null for stock moves. */
    public static Card movingCard(GameState state, Manoeuvre move) {
        return switch (move.type()) {
            case WASTE_TO_TABLEAU, WASTE_TO_FOUNDATION -> state.wasteTop();
            case TABLEAU_TO_TABLEAU ->
                    state.tableauCard(move.from(), state.tableauSize(move.from()) - move.count());
            case TABLEAU_TO_FOUNDATION -> state.topTableauCard(move.from());
            case FOUNDATION_TO_TABLEAU -> state.topFoundationCard(Suit.values()[move.from()]);
            case DRAW, RECYCLE -> null;
        };
    }
}
