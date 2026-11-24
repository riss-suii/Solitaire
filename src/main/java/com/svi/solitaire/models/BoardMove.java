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

    public static BoardMove draw() {
        return DRAW;
    }

    public static BoardMove recycle() {
        return RECYCLE;
    }

    public static BoardMove wasteToTableau(int toPile) {
        return new BoardMove(MoveType.WASTE_TO_TABLEAU, -1, toPile, 1);
    }

    public static BoardMove wasteToFoundation(int suitIndex) {
        return new BoardMove(MoveType.WASTE_TO_FOUNDATION, -1, suitIndex, 1);
    }

    public static BoardMove tableauToTableau(int fromPile, int toPile, int count) {
        return new BoardMove(MoveType.TABLEAU_TO_TABLEAU, fromPile, toPile, count);
    }

    public static BoardMove tableauToFoundation(int fromPile, int suitIndex) {
        return new BoardMove(MoveType.TABLEAU_TO_FOUNDATION, fromPile, suitIndex, 1);
    }

    public static BoardMove foundationToTableau(int suitIndex, int toPile) {
        return new BoardMove(MoveType.FOUNDATION_TO_TABLEAU, suitIndex, toPile, 1);
    }
}
