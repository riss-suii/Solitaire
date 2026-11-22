package com.svi.solitaire.utils;

import com.svi.solitaire.models.Card;

/**
 * Card rules and text conversion.
 */
public final class CardUtil {

    /**
     * Display text such as "10♥" or "A♠".
     */
    public static String label(Card card) {
        return card.getRank().getSymbol() + card.getSuit().getSymbol();
    }
}