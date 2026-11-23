package com.svi.solitaire.game;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.DrawMove;
import com.svi.solitaire.enums.GameStatus;
import com.svi.solitaire.io.ConsoleDisplay;
import com.svi.solitaire.io.FileLoader;
import com.svi.solitaire.io.InputHandler;
import com.svi.solitaire.models.Deck;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.rules.GameRules;
import com.svi.solitaire.shuffle.ShuffleFactory;
import com.svi.solitaire.shuffle.ShuffleStrategy;
import com.svi.solitaire.simulation.SimulationRunning;
import com.svi.solitaire.simulation.SimulationStatistics;
import com.svi.solitaire.strategy.RuleBasedPlayer;
import com.svi.solitaire.models.tableau.Manoeuvre;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.function.IntFunction;

/**
 * Sets up and starts the application: wires every collaborator ({@link #launch(String[])}), runs the
 * menus, and hands each game or simulation to the {@link GameController}, which plays it.
 */
public class GameRunner {

    private static final String NO_COLOR_FLAG = "--no-color";

    private static final int SHUFFLE_RANDOM = 1;
    private static final int SHUFFLE_PERFECT_IN = 2;
    private static final int SHUFFLE_PERFECT_OUT = 3;
    private static final int SHUFFLE_NONE = 4;

    private static final int MODE_WATCH = 1;
    private static final int MODE_SIMULATE = 2;

    private final GameController controller;
    private final GameInitializer initializer;
    private final ShuffleFactory shuffles;
    private final ConsoleDisplay view;
    private final InputHandler input;
    private final FileLoader fileLoader;
    private final SimulationRunning simulation;

    public GameRunner(GameController controller,
                      GameInitializer initializer, ShuffleFactory shuffles,
                      ConsoleDisplay view, InputHandler input, FileLoader fileLoader,
                      SimulationRunning simulation) {
        this.controller = controller;
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
        GameController controller = new GameController(player, executor, rules);

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ConsoleDisplay view = new ConsoleDisplay(out, useColor, rules);
        InputHandler input = new InputHandler(new Scanner(System.in, StandardCharsets.UTF_8), view);
        FileLoader fileLoader = new FileLoader(view);
        GameInitializer initializer = new GameInitializer();
        ShuffleFactory shuffles = new ShuffleFactory(new Random());
        SimulationRunning simulation = new SimulationRunning(controller, initializer);

        GameRunner runner = new GameRunner(controller, initializer, shuffles,
                view, input, fileLoader, simulation);
        try {
            runner.run();
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

            view.showMenu("SELECT SHUFFLE", "[1] Random", "[2] Perfect In", "[3] Perfect Out",
                    "[4] No shuffle");
            int shuffle = input.askChoice("Enter your choice : ",
                    SHUFFLE_RANDOM, SHUFFLE_PERFECT_IN, SHUFFLE_PERFECT_OUT, SHUFFLE_NONE);

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
            }
            if (deck == null) {
                return;                               // input ended
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
        } else if (shuffle == SHUFFLE_NONE) {
            shuffleStrategy = shuffles.none();
        } else {
            int rounds = input.askNumber("Number of perfect shuffles : ", 1, 100);
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
    public GameStatus play(GameState state, boolean showEveryBoard) {
        view.showMessage("Initial deal:");
        view.showBoard(state);

        GameController.Listener listener = new GameController.Listener() {
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
        GameController.Outcome outcome = controller.play(state, listener);

        if (!showEveryBoard) {
            view.showMessage("");
            view.showBoard(state);
        }
        view.showResult(outcome.status(), outcome.moves(), state);
        return outcome.status();
    }

    // ------------------------------------------------------------- simulation

    private void simulate(Deck deck, DrawMove drawMove, int shuffle) {
        int games;
        IntFunction<ShuffleStrategy> shuffleFor;
        String shuffleName;

        if (shuffle == SHUFFLE_RANDOM) {
            games = input.askNumber("Number of games : ", 1, Constants.MAX_SIMULATION_GAMES.getValue());
            long baseSeed = System.nanoTime();
            // one seeded Random per game: reproducible and safe to use from parallel workers
            shuffleFor = i -> shuffles.seeded(baseSeed + i);
            shuffleName = "Random";
        } else if (shuffle == SHUFFLE_NONE) {
            view.showMessage("An unshuffled deck always deals the same game, so one game is played.");
            games = 1;
            shuffleFor = i -> shuffles.none();
            shuffleName = shuffles.none().getName();
        } else {
            view.showMessage("Each game will use one more perfect shuffle than the previous game.");
            games = input.askNumber("Enter the number of games to test : ", 1, 200);
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
