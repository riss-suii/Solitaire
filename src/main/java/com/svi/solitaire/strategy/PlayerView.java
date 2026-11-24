package com.svi.solitaire.strategy;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Everything a real player can know about a game: the face-up tableau cards, how many cards are
 * face down in each pile, the foundations, the waste, and how many cards are left in the stock.
 *
 * <p>The identity of face-down tableau cards is never copied here. The identity of the stock cards
 * is copied only once the player has been through the stock ({@code talonKnown}), because from then
 * on the order is no longer a secret. {@link #of} is the single place that reads a real
 * {@link GameState} for the player.
 */
public final class PlayerView {

    private static final int PILES = GameConstants.TABLEAU_PILE_COUNT;
    private static final int SUITS = GameConstants.FOUNDATION_COUNT;

    private final DrawMode drawMode;
    private final int[] faceDown = new int[PILES];
    private final List<List<Card>> faceUp = new ArrayList<>();
    private final int[] foundation = new int[SUITS];
    private final List<Card> waste = new ArrayList<>();
    private final int stockSize;
    /** The stock in draw order, or null while its order is still unknown. */
    private final List<Card> knownStock;

    private PlayerView(GameState state, boolean talonKnown) {
        this.drawMode = state.getDrawMode();
        for (int pile = 0; pile < PILES; pile++) {
            faceDown[pile] = state.getFaceDownCount(pile);
            List<Card> visible = new ArrayList<>();
            for (int i = faceDown[pile]; i < state.getTableauSize(pile); i++) {
                visible.add(state.getTableauCard(pile, i));
            }
            faceUp.add(visible);
        }
        for (int suit = 0; suit < SUITS; suit++) {
            foundation[suit] = state.getFoundationHeight(suit);
        }
        for (int i = 0; i < state.getWasteSize(); i++) {
            waste.add(state.getWasteCard(i));
        }
        this.stockSize = state.getStockSize();
        if (talonKnown) {
            this.knownStock = new ArrayList<>(stockSize);
            for (int i = 0; i < stockSize; i++) {
                knownStock.add(state.getTalonCard(state.getWasteSize() + i));
            }
        } else {
            this.knownStock = null;
        }
    }

    /** What the player sees in {@code state}. {@code talonKnown}: the stock has been recycled at least once. */
    public static PlayerView of(GameState state, boolean talonKnown) {
        return new PlayerView(state, talonKnown);
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public int faceDownCount(int pile) {
        return faceDown[pile];
    }

    public List<Card> faceUpCards(int pile) {
        return faceUp.get(pile);
    }

    public int foundationHeight(int suit) {
        return foundation[suit];
    }

    public List<Card> waste() {
        return waste;
    }

    public int stockSize() {
        return stockSize;
    }

    /** The stock in draw order, or null when the player has not seen those cards yet. */
    public List<Card> knownStock() {
        return knownStock;
    }
}
