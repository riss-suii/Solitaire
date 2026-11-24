package com.svi.solitaire.models;

import com.svi.solitaire.enums.MoveType;

/**
 * One single move.
 *
 * @param type  what kind of move
 * @param from  source tableau pile / foundation (suit ordinal); -1 when unused
 * @param to    destination tableau pile / foundation (suit ordinal); -1 when unused
 * @param count number of cards moved (tableau-to-tableau runs); 1 otherwise
 */
public record BoardMove(MoveType type, int from, int to, int count) {

    private static final BoardMove DRAW = new BoardMove(MoveType.DRAW, -1, -1, 0);
    private static final BoardMove RECYCLE = new BoardMove(MoveType.RECYCLE, -1, -1, 0);

    /**
     * Returns the move that draws from the stock.
     *
     * @return the move that draws from the stock
     */
    public static BoardMove draw() {
        return DRAW;
    }

    /**
     * Returns the move that turns the waste back into the stock.
     *
     * @return the move that turns the waste back into the stock
     */
    public static BoardMove recycle() {
        return RECYCLE;
    }

    /**
     * Returns a move of the top waste card onto that pile.
     *
     * @param toPile the destination tableau pile
     * @return a move of the top waste card onto that pile
     */
    public static BoardMove wasteToTableau(int toPile) {
        return new BoardMove(MoveType.WASTE_TO_TABLEAU, -1, toPile, 1);
    }

    /**
     * Returns a move of the top waste card onto that foundation.
     *
     * @param suitIndex the ordinal of the suit whose foundation receives the card
     * @return a move of the top waste card onto that foundation
     */
    public static BoardMove wasteToFoundation(int suitIndex) {
        return new BoardMove(MoveType.WASTE_TO_FOUNDATION, -1, suitIndex, 1);
    }

    /**
     * Returns a move of a run from one pile to another.
     *
     * @param fromPile the source tableau pile
     * @param toPile   the destination tableau pile
     * @param count    how many cards are moved, counted from the top of the source pile
     * @return a move of a run from one pile to another
     */
    public static BoardMove tableauToTableau(int fromPile, int toPile, int count) {
        return new BoardMove(MoveType.TABLEAU_TO_TABLEAU, fromPile, toPile, count);
    }

    /**
     * Returns a move of the top card of a pile onto its foundation.
     *
     * @param fromPile  the source tableau pile
     * @param suitIndex the ordinal of the suit whose foundation receives the card
     * @return a move of the top card of a pile onto its foundation
     */
    public static BoardMove tableauToFoundation(int fromPile, int suitIndex) {
        return new BoardMove(MoveType.TABLEAU_TO_FOUNDATION, fromPile, suitIndex, 1);
    }
}
