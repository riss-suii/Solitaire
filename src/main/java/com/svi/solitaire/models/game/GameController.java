package com.svi.solitaire.models.game;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.DrawMove;
import com.svi.solitaire.io.ConsoleDisplay;
import com.svi.solitaire.io.FileLoader;
import com.svi.solitaire.io.InputHandler;
import com.svi.solitaire.models.Deck;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GameController {

    private static final int SHUFFLE_RANDOM = 1;
    private static final int SHUFFLE_PERFECT_IN = 2;
    private static final int SHUFFLE_PERFECT_OUT = 3;

    private final ConsoleDisplay view;
    private final InputHandler input;
    private final FileLoader fileLoader;

    public GameController(ConsoleDisplay view, InputHandler input, FileLoader fileLoader) {
        this.view = view;
        this.input = input;
        this.fileLoader = fileLoader;
    }

    public static void launch(String[] args) {
        boolean useColor = System.getenv("NO_COLOR") == null;

        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        ConsoleDisplay view = new ConsoleDisplay(out, useColor);
        InputHandler input = new InputHandler(
                new Scanner(System.in, StandardCharsets.UTF_8), view);
        FileLoader fileLoader = new FileLoader(view);

        GameController controller = new GameController(view, input, fileLoader);

        try {
            controller.run();
        } catch (InputHandler.InputEndedException e) {
            out.println();
        }
    }

    public void run() {
        do {
            Deck deck = promptForDeck();

            if (deck == null) {
                return;
            }

            view.displayMainMenu();

            DrawMove drawMove = DrawMove.fromCount(input.askDrawCount());

            view.showMenu("SELECT SHUFFLE",
                    "[1] Random",
                    "[2] Perfect In",
                    "[3] Perfect Out");

            int shuffle = input.askChoice("Enter your choice : ",
                    SHUFFLE_RANDOM, SHUFFLE_PERFECT_IN, SHUFFLE_PERFECT_OUT);

            // Game initialization and gameplay will be added later.

            view.showMenu("PLAY AGAIN?", "[1] Yes", "[0] Quit");

        } while (input.askChoice("Enter your choice : ", 0, 1) == 1);

        view.showMessage("Thanks for playing!");
    }

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
}