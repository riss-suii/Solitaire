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
 * <p>Each {@link TableauPile} knows how many of its top cards are face up. The stock and waste
 * together are held by a single {@link Talon}: cards {@code [0, pointer)} are the waste (last one on
 * top) and cards {@code [pointer, size)} are the stock, so turning the waste back over keeps the
 * original order.
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

    /**
     * Creates an empty position: empty piles, empty foundations and an empty talon.
     *
     * @param drawMode how many cards a draw turns over
     */
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
     * Returns the draw mode of this game (how many cards a draw turns over).
     *
     * @return the draw mode of this game (how many cards a draw turns over)
     */
    public DrawMode getDrawMode() {
        return drawMode;
    }

    /**
     * Returns the number of cards in the pile, face up and face down.
     *
     * @param pile the tableau pile, 0 to 6
     * @return the number of cards in the pile, face up and face down
     */
    public int getTableauSize(int pile) {
        return tableau[pile].size();
    }

    /**
     * Returns how many cards at the top of the pile are face up.
     *
     * @param pile the tableau pile, 0 to 6
     * @return how many cards at the top of the pile are face up
     */
    public int getFaceUpCount(int pile) {
        return tableau[pile].getFaceUpCount();
    }

    /**
     * Returns how many cards at the bottom of the pile are face down.
     *
     * @param pile the tableau pile, 0 to 6
     * @return how many cards at the bottom of the pile are face down
     */
    public int getFaceDownCount(int pile) {
        return tableau[pile].getFaceDownCount();
    }

    /**
     * Returns the card at that position.
     *
     * @param pile  the tableau pile, 0 to 6
     * @param index the position in the pile, 0 = bottom
     * @return the card at that position
     * @throws IndexOutOfBoundsException if the index is not inside the pile
     */
    public Card getTableauCard(int pile, int index) {
        return tableau[pile].getPosition(index);
    }

    /**
     * Returns the top (last) card of the pile, or null when it is empty.
     *
     * @param pile the tableau pile, 0 to 6
     * @return the top (last) card of the pile, or null when it is empty
     */
    public Card getTopTableauCard(int pile) {
        return tableau[pile].getTop();
    }

    /**
     * Returns how many cards are on that suit's foundation.
     *
     * @param suitIndex the suit's ordinal
     * @return how many cards are on that suit's foundation
     */
    public int getFoundationHeight(int suitIndex) {
        return foundation.height(suitIndex);
    }

    /**
     * Returns how many cards are on that suit's foundation.
     *
     * @param suit the suit of the foundation
     * @return how many cards are on that suit's foundation
     */
    public int getFoundationHeight(Suit suit) {
        return foundation.height(suit);
    }

    /**
     * Returns the top card of that foundation, or null when it is empty.
     *
     * @param suit the suit of the foundation
     * @return the top card of that foundation, or null when it is empty
     */
    public Card getTopFoundationCard(Suit suit) {
        return foundation.top(suit);
    }

    /**
     * Returns how many cards are on all four foundations together.
     *
     * @return how many cards are on all four foundations together
     */
    public int getTotalFoundationCards() {
        return foundation.total();
    }

    /**
     * Returns how many cards are still in the stock.
     *
     * @return how many cards are still in the stock
     */
    public int getStockSize() {
        return talon.getCards().size() - talon.getPointer();
    }

    /**
     * Returns how many cards are in the waste.
     *
     * @return how many cards are in the waste
     */
    public int getWasteSize() {
        return talon.getPointer();
    }

    /**
     * Returns the top (playable) card of the waste, or null when the waste is empty.
     *
     * @return the top (playable) card of the waste, or null when the waste is empty
     */
    public Card getWasteTop() {
        int pointer = talon.getPointer();
        return pointer == 0 ? null : talon.getCards().get(pointer - 1);
    }

    /**
     * Returns the waste card at that position.
     *
     * @param index the position in the waste, from 0 (oldest) to {@code getWasteSize() - 1} (top)
     * @return the waste card at that position
     * @throws IndexOutOfBoundsException if the index is not inside the waste
     */
    public Card getWasteCard(int index) {
        if (index < 0 || index >= talon.getPointer()) {
            throw new IndexOutOfBoundsException("Waste index out of range: " + index);
        }
        return talon.getCards().get(index);
    }

    /**
     * Returns the talon card at that position.
     *
     * @param index the position in the combined talon (waste cards first, then the stock)
     * @return the talon card at that position
     * @throws IndexOutOfBoundsException if the index is outside the talon
     */
    public Card getTalonCard(int index) {
        return talon.getCards().get(index);
    }

    // ---------------------------------------------------------------- mutators

    /**
     * Puts a card on top of a tableau pile during the initial deal.
     *
     * @param pile     the tableau pile, 0 to 6
     * @param card     the card to deal
     * @param isFaceUp true if the card is dealt face up
     */
    public void dealToTableau(int pile, Card card, boolean isFaceUp) {
        tableau[pile].deal(card, isFaceUp);
    }

    /**
     * Fills the stock in draw order (first element is drawn first) and empties the waste.
     *
     * @param cards the cards for the stock
     * @throws IllegalArgumentException if there are more cards than the stock can hold
     */
    public void setStock(List<Card> cards) {
        if (cards.size() > GameConstants.STOCK_SIZE) {
            throw new IllegalArgumentException("Too many stock cards: " + cards.size());
        }
        talon.setCards(cards);
    }

    /**
     * Removes the top card of a tableau pile. It does not turn over the card below; see
     * {@link #revealTopIfHidden(int)}.
     *
     * @param pile the tableau pile, 0 to 6
     * @return the removed card
     * @throws IllegalStateException if the pile is empty
     */
    public Card removeTableauTop(int pile) {
        return tableau[pile].removeTop();
    }

    /**
     * Puts a card face up on top of a tableau pile (used by moves, not by the initial deal).
     *
     * @param pile the tableau pile, 0 to 6
     * @param card the card to add
     */
    public void addTableauTop(int pile, Card card) {
        tableau[pile].addFaceUp(card);
    }

    /**
     * Turns the top card over when a pile has cards but none face up.
     *
     * @param pile the tableau pile, 0 to 6
     */
    public void revealTopIfHidden(int pile) {
        tableau[pile].revealTopIfHidden();
    }

    /**
     * Sets how many talon cards are in the waste; the rest are the stock.
     *
     * @param wasteSize the new number of waste cards
     * @throws IndexOutOfBoundsException if it is outside {@code [0, getTalonSize()]}
     */
    public void setWasteSize(int wasteSize) {
        talon.setPointer(wasteSize);
    }

    /**
     * Removes a card from the talon. The waste size is not changed, so the caller must update it.
     *
     * @param index the position in the combined talon (waste cards first, then the stock)
     * @return the removed card
     * @throws IndexOutOfBoundsException if the index is outside the talon
     */
    public Card removeTalonCard(int index) {
        return talon.removeCard(index);
    }

    /**
     * Adds the next card of a suit to its foundation.
     *
     * @param suit the suit of the foundation
     * @throws IllegalStateException if the foundation is already complete
     */
    public void addToFoundation(Suit suit) {
        foundation.push(suit);
    }

    /**
     * Removes the top card of a suit's foundation.
     *
     * @param suit the suit of the foundation
     * @throws IllegalStateException if the foundation is empty
     */
    public void removeFromFoundation(Suit suit) {
        foundation.pop(suit);
    }

    // ----------------------------------------------------------------- hashing

    /**
     * 64-bit fingerprint of the exact position, including how far the stock has been turned, so every
     * draw changes it. Used to notice that a position has come back.
     *
     * @return the fingerprint of the current position
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
        for (Card card : talon.getCards()) {
            h = mix(h, card.id() + 1);
        }
        return mix(h, 4000 + talon.getPointer());
    }

    private static long mix(long h, long v) {
        h ^= v * 0x9E3779B97F4A7C15L;
        h = Long.rotateLeft(h, 27) * 0xBF58476D1CE4E5B9L;
        return h ^ (h >>> 31);
    }
}
