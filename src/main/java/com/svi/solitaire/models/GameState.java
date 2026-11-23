package com.svi.solitaire.models;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.DrawMove;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.tableau.Foundation;
import com.svi.solitaire.models.tableau.Stock;
import com.svi.solitaire.models.tableau.Waste;

import java.util.List;

/**
 * Complete, compact game position: seven tableau piles, the foundations, the stock and the waste.
 *
 * <p>Tableau piles are fixed arrays; {@code faceUp[pile]} says how many cards at the end of a pile
 * are face up. The stock and waste together are treated as one "talon": cards {@code [0, pointer)}
 * are the waste (last one on top) and cards {@code [pointer, size)} are the stock, so turning the
 * waste back over keeps the original order.
 *
 * <p>This class only stores state and exposes low-level mutators. Game logic lives in
 * {@code MoveExecutor}; legality lives in {@code GameRules}.
 */
public class GameState {

    private static final int PILES = Constants.TABLEAU_COUNT.getValue();
    private static final int MAX_PILE = Constants.MAX_TABLEAU_PILE_SIZE.getValue();

    private final DrawMove drawMove;
    private final Card[][] tableau;
    private final int[] tableauSize;
    private final int[] faceUp;
    private final Foundation foundation;
    private final Stock stock;
    private final Waste waste;

    public GameState(DrawMove drawMove) {
        this.drawMove = drawMove;
        this.tableau = new Card[PILES][MAX_PILE];
        this.tableauSize = new int[PILES];
        this.faceUp = new int[PILES];
        this.foundation = new Foundation();
        this.stock = new Stock();
        this.waste = new Waste();
    }

    // ------------------------------------------------------------------ queries

    public DrawMove getDrawMove() {
        return drawMove;
    }

    public int tableauSize(int pile) {
        return tableauSize[pile];
    }

    public int faceUpCount(int pile) {
        return faceUp[pile];
    }

    public int faceDownCount(int pile) {
        return tableauSize[pile] - faceUp[pile];
    }

    public Card tableauCard(int pile, int index) {
        return tableau[pile][index];
    }

    /** Top (last) card of a pile, or null when empty. */
    public Card topTableauCard(int pile) {
        int size = tableauSize[pile];
        return size == 0 ? null : tableau[pile][size - 1];
    }

    public int foundationHeight(int suitIndex) {
        return foundation.height(suitIndex);
    }

    public int foundationHeight(Suit suit) {
        return foundation.height(suit);
    }

    public Card topFoundationCard(Suit suit) {
        return foundation.top(suit);
    }

    public int totalFoundationCards() {
        return foundation.total();
    }

    public int stockSize() {
        return stock.size();
    }

    public int wasteSize() {
        return waste.size();
    }

    public Card wasteTop() {
        return waste.top();
    }

    public Card wasteCard(int index) {
        return waste.get(index);
    }

    /** Stock and waste together: waste cards first, then the stock in draw order. */
    public int talonSize() {
        return waste.size() + stock.size();
    }

    /** Number of talon cards already in the waste. */
    public int pointer() {
        return waste.size();
    }

    public Card talonCard(int index) {
        return index < waste.size() ? waste.get(index) : stock.get(index - waste.size());
    }

    // ---------------------------------------------------------------- mutators

    public void dealToTableau(int pile, Card card, boolean isFaceUp) {
        tableau[pile][tableauSize[pile]++] = card;
        if (isFaceUp) {
            faceUp[pile]++;
        }
    }

    /** Fills the stock in draw order (first element is drawn first) and empties the waste. */
    public void setStock(List<Card> cards) {
        if (cards.size() > Constants.STOCK_SIZE.getValue()) {
            throw new IllegalArgumentException("Too many stock cards: " + cards.size());
        }
        stock.load(cards);
        waste.clear();
    }

    public Card removeTableauTop(int pile) {
        int index = --tableauSize[pile];
        Card card = tableau[pile][index];
        tableau[pile][index] = null;
        if (faceUp[pile] > 0) {
            faceUp[pile]--;
        }
        return card;
    }

    public void addTableauTop(int pile, Card card) {
        tableau[pile][tableauSize[pile]++] = card;
        faceUp[pile]++;
    }

    /** Turns the top card over when a pile has cards but none face up. */
    public void revealTopIfHidden(int pile) {
        if (tableauSize[pile] > 0 && faceUp[pile] == 0) {
            faceUp[pile] = 1;
        }
    }

    public Card removeWasteTop() {
        return waste.removeTop();
    }

    /** Turns {@code drawMove.getCount()} cards (or what is left) from the stock onto the waste. */
    public void draw() {
        int count = Math.min(getDrawMove().getCount(), stock.size());
        for (int i = 0; i < count; i++) {
            waste.add(stock.draw());
        }
    }

    /** Flips the waste back over to become the stock (order is preserved). */
    public void recycle() {
        stock.refillFrom(waste);
    }

    public void addToFoundation(Suit suit) {
        foundation.push(suit);
    }

    public void removeFromFoundation(Suit suit) {
        foundation.pop(suit);
    }

    // ----------------------------------------------------------------- hashing

    /**
     * 64-bit fingerprint of the exact position, including how far the stock has been turned, so every
     * draw changes it. Used to notice that a position has come back.
     */
    public long exactFingerprint() {
        long h = 0x243F6A8885A308D3L;
        for (int p = 0; p < PILES; p++) {
            h = mix(h, 1000 + faceUp[p]);
            for (int i = 0; i < tableauSize[p]; i++) {
                h = mix(h, tableau[p][i].id() + 1);
            }
            h = mix(h, 2000);
        }
        for (int suit = 0; suit < Constants.FOUNDATION_COUNT.getValue(); suit++) {
            h = mix(h, 3000 + foundation.height(suit));
        }
        int talon = talonSize();
        for (int i = 0; i < talon; i++) {
            h = mix(h, talonCard(i).id() + 1);
        }
        return mix(h, 4000 + pointer());
    }

    private static long mix(long h, long v) {
        h ^= v * 0x9E3779B97F4A7C15L;
        h = Long.rotateLeft(h, 27) * 0xBF58476D1CE4E5B9L;
        return h ^ (h >>> 31);
    }
}
