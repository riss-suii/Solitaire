package com.svi.solitaire.strategy;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.rules.GameRules;
import com.svi.solitaire.models.BoardMove;

import java.util.List;
import java.util.Optional;

/**
 * Plays Klondike the way a careful person does, from what is on the table only.
 *
 * <p>It receives a {@link PlayerView} (face-up cards, how many cards are face down, foundations,
 * waste, stock size and - once the stock has been turned over - the stock order), so it can never
 * look at a face-down card. Each call scans the whole board and returns one move:
 *
 * <ol>
 *   <li><b>Free foundation moves.</b> Aces and 2s, and any card that can no longer be needed as a
 *       parent in the tableau, go up at once. Other cards are not sent up.</li>
 *   <li><b>King with a stack into an empty column</b>.</li>
 *   <li><b>Flip a face-down card</b>: move a pile's whole face-up run onto another pile.
 *       The last column is attacked first; the run lands on the pile with the fewest hidden cards.</li>
 *   <li><b>A King from the waste into an empty column</b>.</li>
 *   <li><b>Empty a column</b>, but only when a King is ready to fill it.</li>
 *   <li><b>Waste card onto the tableau.</b></li>
 *   <li><b>Stock</b> - only when nothing above is possible, and only while some card in the
 *       waste or stock could still be played. Flipped cards are remembered: once every talon card has
 *       been seen and none of them fits the board, cycling the stock again is pointless.</li>
 * </ol>
 *
 * <p>When a position keeps coming back (the stock has been through a full cycle with no progress) the
 * caller sets {@code stuck}; then the player also accepts the moves it normally refuses: a card that
 * might still be needed goes to the foundation, or a partial run is shifted to uncover a foundation card.
 *
 * <p>The class has no per-game state, so one instance can be shared across threads.
 */
public class RuleBasedPlayer {

    private static final int PILES = GameConstants.TABLEAU_PILE_COUNT;

    // priorities, highest first
    private static final int FREE_TO_FOUNDATION = 10000;
    private static final int KING_STACK_TO_EMPTY = 9000;
    private static final int FLIP = 8000;
    private static final int WASTE_KING_TO_EMPTY = 7000;
    private static final int EMPTY_A_COLUMN = 6500;
    private static final int WASTE_TO_TABLEAU = 5000;
    private static final int STUCK_TO_FOUNDATION = 4000;
    private static final int STUCK_UNCOVER = 3500;
    private static final int STUCK_EMPTY_A_COLUMN = 3000;
    private static final int DRAW = 1000;
    private static final int RECYCLE = 900;

    private final GameRules rules;

    /**
     * Creates a player.
     *
     * @param rules used to check which cards fit where
     */
    public RuleBasedPlayer(GameRules rules) {
        this.rules = rules;
    }

    /**
     * Returns the next move, or empty when none is possible.
     *
     * @param view  what the player can see
     * @param stuck true when this position has already come up before, so the usual moves are exhausted
     * @return the next move, or empty when none is possible
     */
    public Optional<BoardMove> nextMove(PlayerView view, boolean stuck) {
        // once every talon card has been flipped the player remembers them all, so it can tell
        // that going through the stock again is pointless instead of cycling until a position repeats.
        boolean talonUseless = isTalonKnown(view) && !hasPlayableTalonCard(view);
        stuck = stuck || talonUseless;

        Best best = new Best();
        // a King is "ready" only if there are more Kings available than columns already empty
        boolean kingReady = countReadyKings(view) > countEmptyPiles(view);
        int emptyPile = firstEmptyPile(view);

        offerFoundationMoves(view, stuck, best);
        for (int from = 0; from < PILES; from++) {
            offerRunMoves(view, from, emptyPile, kingReady, stuck, best);
        }
        offerWasteToTableau(view, emptyPile, best);
        if (!talonUseless) {
            offerStockMove(view, best);
        }
        return Optional.ofNullable(best.move);
    }

    // ------------------------------------------------------------ foundation

    private void offerFoundationMoves(PlayerView view, boolean stuck, Best best) {
        List<Card> waste = view.waste();
        if (!waste.isEmpty()) {
            Card card = waste.getLast();
            if (fitsFoundation(view, card)) {
                int score = isFree(view, card) ? FREE_TO_FOUNDATION : stuck ? STUCK_TO_FOUNDATION - 100 : 0;
                best.offer(BoardMove.wasteToFoundation(card.suit().ordinal()), score);
            }
        }
        for (int pile = 0; pile < PILES; pile++) {
            Card card = top(view, pile);
            if (card == null || !fitsFoundation(view, card)) {
                continue;
            }
            int base = isFree(view, card) ? FREE_TO_FOUNDATION : stuck ? STUCK_TO_FOUNDATION : 0;
            if (base > 0) {
                best.offer(BoardMove.tableauToFoundation(pile, card.suit().ordinal()), base + pile);
            }
        }
    }

    /** Aces and 2s are never needed in the tableau; others only when both parents are already up. */
    private boolean isFree(PlayerView view, Card card) {
        int rank = card.rank().getValue();
        if (rank <= 2) {
            return true;
        }
        for (int suit = 0; suit < GameConstants.FOUNDATION_COUNT; suit++) {
            boolean oppositeColor = isRedSuit(suit) != card.isRed();
            if (oppositeColor && view.foundationHeight(suit) < rank - 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean isRedSuit(int suitIndex) {
        return Suit.values()[suitIndex].isRed();
    }

    private boolean fitsFoundation(PlayerView view, Card card) {
        return rules.canPlaceOnFoundation(card, view.foundationHeight(card.suit().ordinal()));
    }

    // ------------------------------------------------------------- tableau runs

    private void offerRunMoves(PlayerView view, int from, int emptyPile, boolean kingReady,
                               boolean stuck, Best best) {
        List<Card> run = view.faceUpCards(from);
        if (run.isEmpty()) {
            return;
        }
        int hidden = view.faceDownCount(from);

        // the whole face-up run: this is what flips a card (or empties the column)
        Card bottom = run.getFirst();
        for (int to = 0; to < PILES; to++) {
            if (to == from || (view.faceDownCount(to) + view.faceUpCards(to).size() == 0 && to != emptyPile)) {
                continue;
            }
            Card target = top(view, to);
            if (!rules.canPlaceOnTableau(bottom, target)) {
                continue;
            }
            boolean toEmpty = target == null;
            if (hidden > 0) {
                int score = toEmpty ? KING_STACK_TO_EMPTY + run.size() : FLIP + from * 100 - view.faceDownCount(to) * 5;
                best.offer(BoardMove.tableauToTableau(from, to, run.size()), score);
            } else if (!toEmpty) {
                int score = kingReady ? EMPTY_A_COLUMN + from : stuck ? STUCK_EMPTY_A_COLUMN + from : 0;
                best.offer(BoardMove.tableauToTableau(from, to, run.size()), score);
            }
        }

        // a shorter run: only when stuck, and only if it uncovers a card the foundation can take
        if (stuck) {
            for (int count = 1; count < run.size(); count++) {
                Card uncovered = run.get(run.size() - count - 1);
                if (!fitsFoundation(view, uncovered)) {
                    continue;
                }
                Card moving = run.get(run.size() - count);
                for (int to = 0; to < PILES; to++) {
                    if (to != from && top(view, to) != null && rules.canPlaceOnTableau(moving, top(view, to))) {
                        best.offer(BoardMove.tableauToTableau(from, to, count), STUCK_UNCOVER + from);
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------ waste

    private void offerWasteToTableau(PlayerView view, int emptyPile, Best best) {
        List<Card> waste = view.waste();
        if (waste.isEmpty()) {
            return;
        }
        Card card = waste.getLast();
        for (int to = 0; to < PILES; to++) {
            Card target = top(view, to);
            boolean toEmpty = view.faceDownCount(to) + view.faceUpCards(to).size() == 0;
            if (toEmpty && to != emptyPile) {
                continue;
            }
            if (!rules.canPlaceOnTableau(card, target)) {
                continue;
            }
            int score = toEmpty ? WASTE_KING_TO_EMPTY : WASTE_TO_TABLEAU - view.faceDownCount(to);
            best.offer(BoardMove.wasteToTableau(to), score);
        }
    }

    // ------------------------------------------------------------------ stock

    /** The stock is the last resort. */
    private void offerStockMove(PlayerView view, Best best) {
        if (view.stockSize() > 0) {
            best.offer(BoardMove.draw(), DRAW);
        } else if (!view.waste().isEmpty()) {
            best.offer(BoardMove.recycle(), RECYCLE);
        }
    }

    // ----------------------------------------------------------------- helpers

    /**
     * Counts the Kings that could fill an empty column. A King may be in the waste, in the
     * stock (once its order is known), or at the bottom of a face-up run that sits on hidden cards.
     */
    private int countReadyKings(PlayerView view) {
        int count = 0;
        for (Card card : view.waste()) {
            if (card.rank() == Rank.KING) {
                count++;
            }
        }
        if (view.knownStock() != null) {
            for (Card card : view.knownStock()) {
                if (card.rank() == Rank.KING) {
                    count++;
                }
            }
        }
        for (int pile = 0; pile < PILES; pile++) {
            List<Card> run = view.faceUpCards(pile);
            if (!run.isEmpty() && run.getFirst().rank() == Rank.KING && view.faceDownCount(pile) > 0) {
                count++;
            }
        }
        return count;
    }

    private static int countEmptyPiles(PlayerView view) {
        int count = 0;
        for (int pile = 0; pile < PILES; pile++) {
            if (view.faceDownCount(pile) + view.faceUpCards(pile).size() == 0) {
                count++;
            }
        }
        return count;
    }

    /** True when the player has seen every card of the waste and stock. */
    private static boolean isTalonKnown(PlayerView view) {
        return view.stockSize() == 0 || view.knownStock() != null;
    }

    /** Could any remembered talon card be played on the board as it is now? */
    private boolean hasPlayableTalonCard(PlayerView view) {
        for (Card card : view.waste()) {
            if (isPlayable(view, card)) {
                return true;
            }
        }
        if (view.knownStock() != null) {
            for (Card card : view.knownStock()) {
                if (isPlayable(view, card)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPlayable(PlayerView view, Card card) {
        if (fitsFoundation(view, card)) {
            return true;
        }
        for (int pile = 0; pile < PILES; pile++) {
            if (rules.canPlaceOnTableau(card, top(view, pile))) {
                return true;
            }
        }
        return false;
    }

    private static int firstEmptyPile(PlayerView view) {
        for (int pile = 0; pile < PILES; pile++) {
            if (view.faceDownCount(pile) + view.faceUpCards(pile).size() == 0) {
                return pile;
            }
        }
        return -1;
    }

    private static Card top(PlayerView view, int pile) {
        List<Card> run = view.faceUpCards(pile);
        return run.isEmpty() ? null : run.getLast();
    }

    /** Keeps the highest-scoring legal move; a score of 0 or fewer means "not wanted". */
    private static final class Best {
        private BoardMove move;
        private int score;

        void offer(BoardMove candidate, int candidateScore) {
            if (candidateScore > score) {
                move = candidate;
                score = candidateScore;
            }
        }
    }
}
