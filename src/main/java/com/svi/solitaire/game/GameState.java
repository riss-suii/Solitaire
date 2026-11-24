package com.svi.solitaire.game;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.DrawMode;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.piles.Foundation;
import com.svi.solitaire.models.piles.TableauPile;
import com.svi.solitaire.models.piles.Talon;

import java.util.List;

/**
 * Complete, compact game position: seven tableau piles, the foundations and the talon.
 *
 * <p>Tableau piles are fixed arrays; {@code faceUp[pile]} says how many cards at the end of a pile
 * are face up. The stock and waste together are held by a single {@link Talon}: cards
 * {@code [0, pointer)} are the waste (last one on top) and cards {@code [pointer, size)} are the
 * stock, so turning the waste back over keeps the original order.
 *
 * <p>This class only stores state and exposes low-level mutators. Game logic lives in
 * {@code MoveExecutor}; legality lives in {@code GameRules}.
 */
public class GameState {

    private static final int PILES = GameConstants.TABLEAU_PILE_COUNT;

    private final DrawMode drawMode;
    private final TableauPile[] tableau;
    private final Foundation foundation;
    private final Talon talon;

    public GameState(DrawMode drawMode) {
        this.drawMode = drawMode;
        this.tableau = new TableauPile[PILES];
        for (int pile = 0; pile < PILES; pile++) {
            tableau[pile] = new TableauPile();
        }
        this.foundation = new Foundation();
        this.talon = new Talon();
    }

    // ------------------------------------------------------------------ queries

    /**
     * @return how many cards a draw turns over
     */
    public DrawMode getDrawMode() {
        return drawMode;
    }

    public int getTableauSize(int pile) {
        return tableau[pile].size();
    }

    public int getFaceUpCount(int pile) {
        return tableau[pile].getFaceUpCount();
    }

    public int getFaceDownCount(int pile) {
        return tableau[pile].getFaceDownCount();
    }

    public Card getTableauCard(int pile, int index) {
        return tableau[pile].getPosition(index);
    }

    /** Top (last) card of a pile, or null when empty. */
    public Card getTopTableauCard(int pile) {
        return tableau[pile].getTop();
    }

    /**
     * @param suitIndex the suit's ordinal
     * @return how many cards are on that suit's foundation
     */
    public int getFoundationHeight(int suitIndex) {
        return foundation.height(suitIndex);
    }

    public int getFoundationHeight(Suit suit) {
        return foundation.height(suit);
    }

    public Card getTopFoundationCard(Suit suit) {
        return foundation.top(suit);
    }

    public int getTotalFoundationCards() {
        return foundation.total();
    }

    /**
     * @return how many cards are still in the stock
     */
    public int getStockSize() {
        return talon.stockSize();
    }

    /**
     * @return how many cards are in the waste
     */
    public int getWasteSize() {
        return talon.wasteSize();
    }

    /** Top (playable) card of the waste, or null when the waste is empty. */
    public Card getWasteTop() {
        return talon.wasteTop();
    }

    /**
     * @param index the position in the waste, from 0 (oldest) to {@code getWasteSize() - 1} (top)
     * @return the waste card at that position
     * @throws IndexOutOfBoundsException if the index is not inside the waste
     */
    public Card getWasteCard(int index) {
        if (index < 0 || index >= talon.wasteSize()) {
            throw new IndexOutOfBoundsException("Waste index out of range: " + index);
        }
        return talon.getPosition(index);
    }

    /** Stock and waste together: waste cards first, then the stock in draw order. */
    public int getTalonSize() {
        return talon.size();
    }

    /** Number of talon cards already in the waste. */
    public int getPointer() {
        return talon.wasteSize();
    }

    /**
     * @param index the position in the combined talon (waste cards first, then the stock)
     * @return the talon card at that position
     * @throws IndexOutOfBoundsException if the index is outside the talon
     */
    public Card getTalonCard(int index) {
        return talon.getPosition(index);
    }

    // ---------------------------------------------------------------- mutators

    /**
     * Puts a card on top of a tableau pile during the initial deal.
     *
     * @param isFaceUp true if the card is dealt face up
     */
    public void dealToTableau(int pile, Card card, boolean isFaceUp) {
        tableau[pile].deal(card, isFaceUp);
    }

    /**
     * Fills the stock in draw order (first element is drawn first) and empties the waste.
     *
     * @throws IllegalArgumentException if there are more cards than the stock can hold
     */
    public void setStock(List<Card> cards) {
        if (cards.size() > GameConstants.STOCK_SIZE) {
            throw new IllegalArgumentException("Too many stock cards: " + cards.size());
        }
        talon.load(cards);
    }

    public Card removeTableauTop(int pile) {
        return tableau[pile].removeTop();
    }

    /** Puts a card face up on top of a tableau pile (used by moves, not by the initial deal). */
    public void addTableauTop(int pile, Card card) {
        tableau[pile].addFaceUp(card);
    }

    /** Turns the top card over when a pile has cards but none face up. */
    public void revealTopIfHidden(int pile) {
        tableau[pile].revealTopIfHidden();
    }

    /**
     * Removes the top card of the waste, for example when it is played onto the tableau or a
     * foundation.
     *
     * @return the card that was on top of the waste
     * @throws IllegalStateException if the waste is empty
     */
    public Card removeWasteTop() {
        return talon.removeWasteTop();
    }

    /** Turns {@code drawMode.getCount()} cards (or what is left) from the stock onto the waste. */
    public void draw() {
        talon.draw(getDrawMode().getCount());
    }

    /** Flips the waste back over to become the stock (order is preserved). */
    public void recycle() {
        talon.recycle();
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
            TableauPile pile = tableau[p];
            h = mix(h, 1000 + pile.getFaceUpCount());
            for (int i = 0; i < pile.size(); i++) {
                h = mix(h, pile.getPosition(i).id() + 1);
            }
            h = mix(h, 2000);
        }
        for (int suit = 0; suit < GameConstants.FOUNDATION_COUNT; suit++) {
            h = mix(h, 3000 + foundation.height(suit));
        }
        int talon = getTalonSize();
        for (int i = 0; i < talon; i++) {
            h = mix(h, getTalonCard(i).id() + 1);
        }
        return mix(h, 4000 + getPointer());
    }

    private static long mix(long h, long v) {
        h ^= v * 0x9E3779B97F4A7C15L;
        h = Long.rotateLeft(h, 27) * 0xBF58476D1CE4E5B9L;
        return h ^ (h >>> 31);
    }
}
