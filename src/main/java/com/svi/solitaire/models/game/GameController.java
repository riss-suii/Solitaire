package com.svi.solitaire.models.game;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.DrawMove;
import com.svi.solitaire.io.ConsoleDisplay;
import com.svi.solitaire.io.FileLoader;
import com.svi.solitaire.io.InputHandler;
import com.svi.solitaire.models.Deck;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.engine.MoveExecutor;
import com.svi.solitaire.models.rules.GameRules;
import com.svi.solitaire.models.shuffle.ShuffleFactory;
import com.svi.solitaire.models.shuffle.ShuffleStrategy;
import com.svi.solitaire.models.simulation.SimulationRunning;
import com.svi.solitaire.models.simulation.SimulationStatistics;
import com.svi.solitaire.models.strategy.RuleBasedPlayer;
import com.svi.solitaire.models.tableau.Manoeuvre;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.function.IntFunction;

/**
 * Runs the application: the menus, single games and simulations.
 * {@link #launch(String[])} is the composition root that wires every collaborator.
 */
public class GameController {

    private static final String NO_COLOR_FLAG = "--no-color";

    private static final int SHUFFLE_RANDOM = 1;
    private static final int SHUFFLE_PERFECT_IN = 2;
    private static final int SHUFFLE_PERFECT_OUT = 3;

    private static final int MODE_WATCH = 1;
    private static final int MODE_SIMULATE = 2;

    private final GameRunner runner;
    private final GameInitializer initializer;
    private final ShuffleFactory shuffles;
    private final ConsoleDisplay view;
    private final InputHandler input;
    private final FileLoader fileLoader;
    private final SimulationRunning simulation;

    public GameController(GameRunner runner,
                          GameInitializer initializer, ShuffleFactory shuffles,
                          ConsoleDisplay view, InputHandler input, FileLoader fileLoader,
                          SimulationRunning simulation) {
        this.runner = runner;
        this.initializer = initializer;
        this.shuffles = shuffles;
        this.view = view;
        this.input = input;
        this.fileLoader = fileLoader;
        this.simulation = simulation;
    }

    /**
     * Builds the object graph and runs the menu loop.
     * Pass {@code --no-color} (or set the NO_COLOR environment variable) to turn ANSI colors off.
     */
    public static void launch(String[] args) {
        boolean useColor = Arrays.stream(args).noneMatch(NO_COLOR_FLAG::equals) && System.getenv("NO_COLOR") == null;

        GameRules rules = new GameRules();
        MoveExecutor executor = new MoveExecutor();
        RuleBasedPlayer player = new RuleBasedPlayer(rules);
        GameRunner runner = new GameRunner(player, executor, rules);

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ConsoleDisplay view = new ConsoleDisplay(out, useColor);
        InputHandler input = new InputHandler(new Scanner(System.in, StandardCharsets.UTF_8), view);
        FileLoader fileLoader = new FileLoader(view);
        GameInitializer initializer = new GameInitializer();
        ShuffleFactory shuffles = new ShuffleFactory(new Random());
        SimulationRunning simulation = new SimulationRunning(runner, initializer);

        GameController controller = new GameController(runner, initializer, shuffles,
                view, input, fileLoader, simulation);
        try {
            controller.run();
        } catch (InputHandler.InputEndedException e) {
            out.println();
        }
    }

    // ------------------------------------------------------------------- menu

    public void run() {
        Deck deck = promptForDeck();                      // loaded once; every new game reuses it
        if (deck == null) {
            return;                                       // input ended
        }
        int again;
        do {
            view.displayMainMenu();
            DrawMove drawMove = DrawMove.fromCount(input.askDrawCount());

            view.showMenu("SELECT SHUFFLE", "[1] Random", "[2] Perfect In", "[3] Perfect Out");
            int shuffle = input.askChoice("Enter your choice : ",
                    SHUFFLE_RANDOM, SHUFFLE_PERFECT_IN, SHUFFLE_PERFECT_OUT);

            view.showMenu("SELECT MODE", "[1] Watch one game being played",
                    "[2] Run a simulation (win-rate statistics)");
            int mode = input.askChoice("Enter your choice : ", MODE_WATCH, MODE_SIMULATE);
            if (mode == MODE_WATCH) {
                watchOneGame(deck, drawMove, shuffle);
            } else {
                simulate(deck, drawMove, shuffle);
            }

            view.showMenu("PLAY AGAIN?", "[1] Yes, same deck", "[2] Yes, load another deck file", "[0] Quit");
            again = input.askChoice("Enter your choice : ", 0, 1, 2);
            if (again == 2) {
                deck = promptForDeck();
                if (deck == null) {
                    return;                               // input ended
                }
            }
        } while (again != 0);
        view.showMessage("Thanks for playing!");
    }

    /** Asks for the deck file first; repeats until it loads as a valid 52-card deck. Null if input ended. */
    private Deck promptForDeck() {
        Deck deck;
        do {
            String fileName = input.askDeckFile();
            if (fileName == null) {
                return null;
            }
            deck = fileLoader.loadDeck(fileName);
        } while (deck.count() != Constants.TOTAL_CARDS.getValue());
        view.showMessage("Deck loaded.");
        return deck;
    }

    // ------------------------------------------------------------ single game

    private void watchOneGame(Deck deck, DrawMove drawMove, int shuffle) {
        ShuffleStrategy shuffleStrategy;
        if (shuffle == SHUFFLE_RANDOM) {
            shuffleStrategy = shuffles.random();
        } else {
            int rounds = input.askNumber("Number of perfect shuffles", 1, 100, 1);
            shuffleStrategy = shuffle == SHUFFLE_PERFECT_IN ? shuffles.perfectIn(rounds) : shuffles.perfectOut(rounds);
        }
        view.showMessage("Shuffle: " + shuffleStrategy.getName() + "   |   Draw " + drawMove.getCount());

        view.showMenu("DISPLAY", "[1] Show the board after every move", "[2] Move list and final board only");
        boolean showEveryBoard = input.askChoice("Enter your choice : ", 1, 2) == 1;
        play(initializer.newGame(deck, shuffleStrategy, drawMove), showEveryBoard);
    }

    /**
     * Plays the dealt game on the console, one move at a time. The player only sees face-up cards.
     *
     * @param state          the dealt game (it is played out in place)
     * @param showEveryBoard true: print the board after every step; false: only the steps and the final board
     */
    public void play(GameState state, boolean showEveryBoard) {
        view.showMessage("Initial deal:");
        view.showBoard(state);

        GameRunner.Listener listener = new GameRunner.Listener() {
            @Override
            public void beforeMove(int number, Manoeuvre move, GameState current) {
                view.showStep(number, move, current);
            }

            @Override
            public void afterMove(GameState current) {
                if (showEveryBoard) {
                    view.showBoard(current);
                }
            }
        };
        GameRunner.Outcome outcome = runner.play(state, listener);

        if (!showEveryBoard) {
            view.showMessage("");
            view.showBoard(state);
        }
        view.showResult(outcome.status(), outcome.moves(), state);
    }

    // ------------------------------------------------------------- simulation

    private void simulate(Deck deck, DrawMove drawMove, int shuffle) {
        int games;
        IntFunction<ShuffleStrategy> shuffleFor;
        String shuffleName;

        if (shuffle == SHUFFLE_RANDOM) {
            games = input.askNumber("Number of games", 1, Constants.MAX_SIMULATION_GAMES.getValue(),
                    Constants.DEFAULT_SIMULATION_GAMES.getValue());
            long baseSeed = System.nanoTime();
            // one seeded Random per game: reproducible and safe to use from parallel workers
            shuffleFor = i -> shuffles.seeded(baseSeed + i);
            shuffleName = "Random (seed " + baseSeed + ")";
        } else {
            view.showMessage("Perfect shuffles are deterministic, so game N uses N shuffle rounds (1..N).");
            games = input.askNumber("Highest number of rounds to test", 1, 200, 52);
            boolean in = shuffle == SHUFFLE_PERFECT_IN;
            shuffleFor = i -> in ? shuffles.perfectIn(i + 1) : shuffles.perfectOut(i + 1);
            shuffleName = (in ? "Perfect In" : "Perfect Out") + " (rounds 1.." + games + ")";
        }

        view.showSimulationHeader(games, drawMove, shuffleName);
        int step = Math.max(1, games / 10);
        SimulationStatistics stats = simulation.run(deck, games, drawMove, shuffleFor, done -> {
            if (done % step == 0 && done != games) {
                view.showProgress(done, games);
            }
        });
        view.showStatistics(stats, drawMove, shuffleName);
    }
}
