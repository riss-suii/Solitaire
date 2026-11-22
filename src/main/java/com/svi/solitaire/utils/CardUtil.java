package com.svi.solitaire.utils;

import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.tableau.Manoeuvre;

/**
 * Card rules and text conversion.
 */
public final class CardUtil {
    private CardUtil() {
    }

    public static boolean isOppositeColor(Card a, Card b) {
        return a.isRed() != b.isRed();
    }

    /** True if {@code moving} may be placed on the (face-up) card {@code target} in a tableau column. */
    public static boolean canStackOnTableau(Card moving, Card target) {
        return isOppositeColor(moving, target)
                && target.getRank().getValue() == moving.getRank().getValue() + 1;
    }

    /** {@code top} is the current top of the foundation, or null when it is empty. */
    public static boolean canPlaceOnFoundation(Card card, Card top) {
        if (top == null) {
            return card.getRank() == Rank.ACE;
        }
        return card.getSuit() == top.getSuit()
                && card.getRank().getValue() == top.getRank().getValue() + 1;
    }

    /** Display text such as "10♥" or "A♠". */
    public static String label(Card card) {
        return card.getRank().getSymbol() + card.getSuit().getIcon();
    }

    /** File/keyboard text such as "H-10" or "S-A". */
    public static String code(Card card) {
        return card.getSuit().getSymbol() + "-" + card.getRank().getSymbol();
    }
}
