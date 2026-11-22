package com.svi.solitaire.models.tableau;

import com.svi.solitaire.enums.MoveType;

/**
 * One single move.
 *
 * @param type  what kind of move
 * @param from  source tableau pile / foundation (suit ordinal); -1 when unused
 * @param to    destination tableau pile / foundation (suit ordinal); -1 when unused
 * @param count number of cards moved (tableau-to-tableau runs); 1 otherwise
 */
public record Manoeuvre(MoveType type, int from, int to, int count) {

    private static final Manoeuvre DRAW = new Manoeuvre(MoveType.DRAW, -1, -1, 0);
    private static final Manoeuvre RECYCLE = new Manoeuvre(MoveType.RECYCLE, -1, -1, 0);

    public static Manoeuvre draw() {
        return DRAW;
    }

    public static Manoeuvre recycle() {
        return RECYCLE;
    }

    public static Manoeuvre wasteToTableau(int toPile) {
        return new Manoeuvre(MoveType.WASTE_TO_TABLEAU, -1, toPile, 1);
    }

    public static Manoeuvre wasteToFoundation(int suitIndex) {
        return new Manoeuvre(MoveType.WASTE_TO_FOUNDATION, -1, suitIndex, 1);
    }

    public static Manoeuvre tableauToTableau(int fromPile, int toPile, int count) {
        return new Manoeuvre(MoveType.TABLEAU_TO_TABLEAU, fromPile, toPile, count);
    }

    public static Manoeuvre tableauToFoundation(int fromPile, int suitIndex) {
        return new Manoeuvre(MoveType.TABLEAU_TO_FOUNDATION, fromPile, suitIndex, 1);
    }

    public static Manoeuvre foundationToTableau(int suitIndex, int toPile) {
        return new Manoeuvre(MoveType.FOUNDATION_TO_TABLEAU, suitIndex, toPile, 1);
    }
}
