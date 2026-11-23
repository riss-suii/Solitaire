package com.svi.solitaire.models.rules;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.tableau.Manoeuvre;

/** Klondike rules: what may be stacked where, which moves are legal, and when the game is won. */
public class GameRules {

    private static final int PILES = Constants.TABLEAU_COUNT.getValue();

    /** A card goes on a tableau pile if colors alternate and rank is one lower; only Kings go on an empty pile. */
    public boolean canPlaceOnTableau(Card moving, Card target) {
        if (target == null) {
            return moving.getRank() == Rank.KING;
        }
        return moving.isRed() != target.isRed()
                && target.getRank().getValue() == moving.getRank().getValue() + 1;
    }

    /** A card goes on its foundation if it is the next rank up. */
    public boolean canPlaceOnFoundation(Card card, int foundationHeight) {
        return card.getRank().getValue() == foundationHeight + 1;
    }

    /** A draw turns over the draw-mode count (1 or 3) of cards, or whatever is left in the stock. */
    public int cardsToDraw(GameState state) {
        return Math.min(state.getDrawMove().getCount(), state.stockSize());
    }

    public boolean isWon(GameState state) {
        return state.totalFoundationCards() == Constants.TOTAL_CARDS.getValue();
    }

    /** Decides whether a single move is legal in the given position. */
    public boolean isValid(GameState state, Manoeuvre move) {
        return switch (move.type()) {
            case DRAW -> state.stockSize() > 0;
            case RECYCLE -> state.stockSize() == 0 && state.wasteSize() > 0;
            case WASTE_TO_FOUNDATION -> {
                Card card = state.wasteTop();
                yield card != null
                        && move.to() == card.getSuit().ordinal()
                        && canPlaceOnFoundation(card, state.foundationHeight(card.getSuit()));
            }
            case WASTE_TO_TABLEAU -> {
                Card card = state.wasteTop();
                yield card != null && isPile(move.to()) && canPlaceOnTableau(card, state.topTableauCard(move.to()));
            }
            case TABLEAU_TO_FOUNDATION -> {
                if (!isPile(move.from())) {
                    yield false;
                }
                Card card = state.topTableauCard(move.from());
                yield card != null
                        && move.to() == card.getSuit().ordinal()
                        && canPlaceOnFoundation(card, state.foundationHeight(card.getSuit()));
            }
            case TABLEAU_TO_TABLEAU -> {
                if (!isPile(move.from()) || !isPile(move.to()) || move.from() == move.to()) {
                    yield false;
                }
                int count = move.count();
                if (count < 1 || count > state.faceUpCount(move.from())) {
                    yield false;
                }
                Card bottom = state.tableauCard(move.from(), state.tableauSize(move.from()) - count);
                yield canPlaceOnTableau(bottom, state.topTableauCard(move.to()));
            }
            case FOUNDATION_TO_TABLEAU -> {
                if (move.from() < 0 || move.from() >= Constants.FOUNDATION_COUNT.getValue() || !isPile(move.to())) {
                    yield false;
                }
                Card card = state.topFoundationCard(Suit.values()[move.from()]);
                yield card != null && canPlaceOnTableau(card, state.topTableauCard(move.to()));
            }
        };
    }

    private boolean isPile(int index) {
        return index >= 0 && index < PILES;
    }
}
