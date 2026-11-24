package com.svi.solitaire.game;

import com.svi.solitaire.constants.PlayLimits;
import com.svi.solitaire.enums.GameStatus;
import com.svi.solitaire.enums.MoveType;
import com.svi.solitaire.rules.GameRules;
import com.svi.solitaire.strategy.PlayerView;
import com.svi.solitaire.strategy.RuleBasedPlayer;
import com.svi.solitaire.models.BoardMove;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controls one game from start to finish, one move at a time. Before each move the player is shown
 * only a {@link PlayerView}, never the hidden cards. Stateless, so it can be shared across threads.
 */
public class GameEngine {

    /** Called around every move (for the console display). */
    public interface Listener {
        default void beforeMove(int number, BoardMove move, GameState state) {
        }

        default void afterMove(GameState state) {
        }
    }

    /** How a game ended. */
    public record Outcome(GameStatus status, int moves) {
    }

    private static final Listener SILENT = new Listener() {
    };

    private final RuleBasedPlayer player;
    private final MoveExecutor executor;
    private final GameRules rules;

    public GameEngine(RuleBasedPlayer player, MoveExecutor executor, GameRules rules) {
        this.player = player;
        this.executor = executor;
        this.rules = rules;
    }

    /**
     * Plays the game in place. It ends when the game is won, when no move is left, or when the same
     * position keeps coming back (nothing more can be achieved).
     */
    public Outcome play(GameState state, Listener listener) {
        Listener events = listener == null ? SILENT : listener;
        Map<Long, Integer> visits = new HashMap<>();
        boolean stockSeen = false;       // true once the stock has been turned over: its order is known
        int moves = 0;

        while (!rules.isWon(state) && moves < PlayLimits.MAX_GAME_MOVES) {
            int seen = visits.merge(state.exactFingerprint(), 1, Integer::sum);
            if (seen > PlayLimits.MAX_POSITION_REPEATS) {
                break;
            }
            // the same position again means the stock has been through a full cycle with nothing to play
            Optional<BoardMove> choice = player.nextMove(PlayerView.of(state, stockSeen), seen > 1);
            if (choice.isEmpty()) {
                break;
            }
            BoardMove move = choice.get();
            if (!rules.isValid(state, move)) {
                throw new IllegalStateException("Player chose an illegal move: " + move);
            }
            events.beforeMove(++moves, move, state);
            executor.execute(state, move);
            if (move.type() == MoveType.RECYCLE) {
                stockSeen = true;
            }
            events.afterMove(state);
        }
        return new Outcome(rules.isWon(state) ? GameStatus.WON : GameStatus.LOST, moves);
    }
}
