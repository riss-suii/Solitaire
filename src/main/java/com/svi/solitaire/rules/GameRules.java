package com.svi.solitaire.rules;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.BoardMove;
import com.svi.solitaire.game.GameState;

/** Klondike rules: what may be stacked where, which moves are legal, and when the game is won. */
public class GameRules {

    private static final int PILES = GameConstants.TABLEAU_PILE_COUNT;

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
        return Math.min(state.getDrawMode().getCount(), state.getStockSize());
    }

    /** The card a move picks up (the bottom card of the run for multi-card moves); null for stock moves. */
    public static Card movingCard(GameState state, BoardMove move) {
        return switch (move.type()) {
            case WASTE_TO_TABLEAU, WASTE_TO_FOUNDATION -> state.getWasteTop();
            case TABLEAU_TO_TABLEAU ->
                    state.getTableauCard(move.from(), state.getTableauSize(move.from()) - move.count());
            case TABLEAU_TO_FOUNDATION -> state.getTopTableauCard(move.from());
            case FOUNDATION_TO_TABLEAU -> state.getTopFoundationCard(Suit.values()[move.from()]);
            case DRAW, RECYCLE -> null;
        };
    }

    /** The game is won when all 52 cards are on the foundations. */
    public boolean isWon(GameState state) {
        return state.getTotalFoundationCards() == GameConstants.TOTAL_CARDS;
    }

    /** Decides whether a single move is legal in the given position. */
    public boolean isValid(GameState state, BoardMove move) {
        return switch (move.type()) {
            case DRAW -> state.getStockSize() > 0;
            case RECYCLE -> state.getStockSize() == 0 && state.getWasteSize() > 0;
            case WASTE_TO_FOUNDATION -> {
                Card card = movingCard(state, move);
                yield card != null
                        && move.to() == card.getSuit().ordinal()
                        && canPlaceOnFoundation(card, state.getFoundationHeight(card.getSuit()));
            }
            case WASTE_TO_TABLEAU -> {
                Card card = movingCard(state, move);
                yield card != null && isPile(move.to()) && canPlaceOnTableau(card, state.getTopTableauCard(move.to()));
            }
            case TABLEAU_TO_FOUNDATION -> {
                if (!isPile(move.from())) {
                    yield false;
                }
                Card card = state.getTopTableauCard(move.from());
                yield card != null
                        && move.to() == card.getSuit().ordinal()
                        && canPlaceOnFoundation(card, state.getFoundationHeight(card.getSuit()));
            }
            case TABLEAU_TO_TABLEAU -> {
                if (!isPile(move.from()) || !isPile(move.to()) || move.from() == move.to()) {
                    yield false;
                }
                int count = move.count();
                if (count < 1 || count > state.getFaceUpCount(move.from())) {
                    yield false;
                }
                Card bottom = state.getTableauCard(move.from(), state.getTableauSize(move.from()) - count);
                yield canPlaceOnTableau(bottom, state.getTopTableauCard(move.to()));
            }
            case FOUNDATION_TO_TABLEAU -> {
                if (move.from() < 0 || move.from() >= GameConstants.FOUNDATION_COUNT || !isPile(move.to())) {
                    yield false;
                }
                Card card = state.getTopFoundationCard(Suit.values()[move.from()]);
                yield card != null && canPlaceOnTableau(card, state.getTopTableauCard(move.to()));
            }
        };
    }

    private boolean isPile(int index) {
        return index >= 0 && index < PILES;
    }
}
