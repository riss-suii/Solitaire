package com.svi.solitaire.io;

import com.svi.solitaire.enums.Colors;
import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.DrawMove;
import com.svi.solitaire.enums.GameStatus;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.GameState;
import com.svi.solitaire.models.simulation.SimulationStatistics;
import com.svi.solitaire.models.tableau.Manoeuvre;
import com.svi.solitaire.utils.CardUtil;

import java.io.PrintStream;

/**
 * All console output. The stream is injected so output can be redirected or tested;
 * colors are ANSI escape codes and can be switched off.
 */
public class ConsoleDisplay {
    private static final String FACE_DOWN = "[###]";
    private static final String EMPTY_SLOT = "[   ]";
    private static final int CELL_WIDTH = 5;
    private static final int STOCK_COLUMN_WIDTH = 13;
    private static final int WASTE_COLUMN_WIDTH = 18;
    private static final Suit[] FOUNDATION_ORDER = {Suit.SPADES, Suit.HEARTS, Suit.DIAMONDS, Suit.CLUBS};
    private static final int PILES = Constants.TABLEAU_COUNT.getValue();
    private static final int WIDTH = Constants.BANNER_WIDTH.getValue();

    private final PrintStream out;
    private final boolean useColor;

    public ConsoleDisplay(PrintStream out, boolean color) {
        this.out = out;
        this.useColor = color;
    }

    // ------------------------------------------------------------------ menus

    public void displayMainMenu() {
        out.println(style(Colors.CYAN, "=========================== ♠ KLONDIKE SOLITAIRE ♥ ==========================="));
        out.println(style(Colors.BOLD, "                 Every move counts. Every card has its place.\n"));
        out.println(style(Colors.BOLD, "                               SELECT DRAW MODE \n"));
        out.println(style(Colors.BOLD, "                               [1] Draw 1 Card"));
        out.println(style(Colors.BOLD, "                               [3] Draw 3 Cards"));
        out.println(style(Colors.CYAN, "=============================================================================="));
    }

    /** Prints a centred menu block: title, options, closing rule. */
    public void showMenu(String title, String... options) {
        out.println(style(Colors.BOLD, center(title)));
        for (String option : options) {
            out.println(style(Colors.BOLD, center(option)));
        }
        out.println(style(Colors.CYAN, "=".repeat(WIDTH)));
    }

    // ------------------------------------------------------------------ board

    /** Header line for one step of the replay, e.g. {@code --- Step 14: Draw 3 cards from the stock ---}. */
    public void showStep(int number, Manoeuvre move, GameState before) {
        out.println();
        out.println("--- Step " + number + ": " + describe(move, before) + " ---");
    }

    public void showBoard(GameState state) {
        out.println(pad("STOCK", STOCK_COLUMN_WIDTH) + pad("WASTE", WASTE_COLUMN_WIDTH) + "FOUNDATIONS");
        out.println(stockText(state) + wasteText(state) + foundationsText(state));
        out.println();

        StringBuilder header = new StringBuilder();
        for (int pile = 0; pile < PILES; pile++) {
            header.append(String.format("  %-" + (CELL_WIDTH - 1) + "d", pile + 1));
        }
        out.println(header);

        int rows = 1;
        for (int pile = 0; pile < PILES; pile++) {
            rows = Math.max(rows, state.tableauSize(pile));
        }
        for (int row = 0; row < rows; row++) {
            StringBuilder line = new StringBuilder();
            for (int pile = 0; pile < PILES; pile++) {
                if (pile > 0) {
                    line.append(' ');
                }
                line.append(tableauCell(state, pile, row));
            }
            out.println(line);
        }
        out.println();
    }

    private String stockText(GameState state) {
        if (state.stockSize() == 0) {
            return pad(EMPTY_SLOT, STOCK_COLUMN_WIDTH);
        }
        String count = " x" + state.stockSize();
        return style(Colors.GRAY, FACE_DOWN) + pad(count, STOCK_COLUMN_WIDTH - CELL_WIDTH);
    }

    /** The last cards of the waste (1 or 3 depending on draw mode); the playable top card is blue. */
    private String wasteText(GameState state) {
        int waste = state.wasteSize();
        if (waste == 0) {
            return pad(EMPTY_SLOT, WASTE_COLUMN_WIDTH);
        }
        int shown = Math.min(state.getDrawMove().getCount(), waste);
        StringBuilder text = new StringBuilder();
        for (int i = waste - shown; i < waste; i++) {
            Card card = state.wasteCard(i);
            text.append(style(i == waste - 1 ? Colors.CYAN : colorOf(card), cardCell(card)));
        }
        return text + " ".repeat(Math.max(0, WASTE_COLUMN_WIDTH - shown * CELL_WIDTH));
    }

    private String foundationsText(GameState state) {
        StringBuilder text = new StringBuilder();
        for (Suit suit : FOUNDATION_ORDER) {
            Card top = state.topFoundationCard(suit);
            if (top == null) {
                text.append(style(suit.isRed() ? Colors.RED : Colors.RESET, "[ " + suit.getIcon() + " ]"));
            } else {
                text.append(style(colorOf(top), cardCell(top)));
            }
        }
        return text.toString();
    }

    private String tableauCell(GameState state, int pile, int row) {
        int size = state.tableauSize(pile);
        if (row >= size) {
            return row == 0 ? style(Colors.GRAY, EMPTY_SLOT) : " ".repeat(CELL_WIDTH);
        }
        if (row < size - state.faceUpCount(pile)) {
            return style(Colors.GRAY, FACE_DOWN);
        }
        Card card = state.tableauCard(pile, row);
        return style(colorOf(card), cardCell(card));
    }

    /** "[ 4♥]" / "[10♥]": the card right-aligned in three characters. */
    private static String cardCell(Card card) {
        return "[" + String.format("%3s", card) + "]";
    }

    // ------------------------------------------------------------------ moves

    public String describe(Manoeuvre move, GameState state) {
        Card card = CardUtil.movingCard(state, move);
        return switch (move.type()) {
            case DRAW -> {
                int drawn = Math.min(state.getDrawMove().getCount(), state.stockSize());
                yield "Draw " + drawn + (drawn == 1 ? " card" : " cards") + " from the stock";
            }
            case RECYCLE -> "Stock is empty: waste turned back into the stock (no shuffle)";
            case WASTE_TO_TABLEAU -> "Move " + card + " from the waste to tableau " + (move.to() + 1);
            case WASTE_TO_FOUNDATION -> "Move " + card + " from the waste to the foundation";
            case TABLEAU_TO_TABLEAU -> "Move " + card + (move.count() > 1 ? " (+" + (move.count() - 1) + " more)" : "")
                    + " from tableau " + (move.from() + 1) + " to tableau " + (move.to() + 1);
            case TABLEAU_TO_FOUNDATION -> "Move " + card + " from tableau " + (move.from() + 1) + " to the foundation";
            case FOUNDATION_TO_TABLEAU -> "Move " + card + " from the foundation to tableau " + (move.to() + 1);
        };
    }

    // ---------------------------------------------------------------- results

    public void showResult(GameStatus status, int moves, GameState finalState) {
        out.println(style(Colors.CYAN, "=".repeat(WIDTH)));
        if (status == GameStatus.WON) {
            out.println(style(Colors.YELLOW, center("★  YOU WIN!  All 52 cards are on the foundations.  ★")));
        } else {
            out.println(style(Colors.RED, center("No winning line was found for this deal.")));
        }
        out.println(center("Moves: " + moves + "    Cards on foundations: "
                + finalState.totalFoundationCards() + "/" + Constants.TOTAL_CARDS.getValue()));
        out.println(style(Colors.CYAN, "=".repeat(WIDTH)));
    }

    public void showSimulationHeader(int games, DrawMove drawMove, String shuffleName) {
        out.println();
        out.println("  Simulating " + games + " game" + (games == 1 ? "" : "s")
                + "  |  Draw " + drawMove.getCount() + "  |  " + shuffleName);
    }

    public void showProgress(int done, int total) {
        out.printf("  ... %d / %d games%n", done, total);
    }

    public void showStatistics(SimulationStatistics stats, DrawMove drawMove, String shuffleName) {
        out.println();
        out.println(style(Colors.CYAN, titleLine("SIMULATION RESULTS")));
        out.printf("  Draw mode          : Draw %d%n", drawMove.getCount());
        out.printf("  Shuffle            : %s%n", shuffleName);
        out.printf("  Games played       : %d%n", stats.getGamesPlayed());
        out.printf("  Wins/Losses        : %d/%d%n", stats.getWins(), stats.getLosses());
        out.printf("  Win rate           : %.1f%%%n", stats.getWinRate());
        out.println(style(Colors.CYAN, "=".repeat(WIDTH)));
    }

    public void showMessage(String message) {
        out.println(message);
    }

    public void showError(String message) {
        out.println(style(Colors.RED, "Error: " + message));
    }

    /** Prints a question without a line break and leaves the cursor after it. */
    public void prompt(String text) {
        out.print(style(Colors.GREEN, text));
        out.flush();
    }

    // ---------------------------------------------------------------- helpers

    /** Wraps text in an ANSI color; plain text when colors are off. */
    private String style(Colors color, String text) {
        return useColor ? color.colorize(text) : text;
    }

    private static Colors colorOf(Card card) {
        return card.isRed() ? Colors.RED : Colors.RESET;

    }

    /** Pads plain (uncolored) text on the right. */
    private static String pad(String text, int width) {
        return text.length() >= width ? text : text + " ".repeat(width - text.length());
    }

    private static String titleLine(String title) {
        String padded = " " + title + " ";
        int side = Math.max(0, (WIDTH - padded.length()) / 2);
        return "=".repeat(side) + padded + "=".repeat(Math.max(0, WIDTH - side - padded.length()));
    }

    private static String center(String text) {
        int left = Math.max(0, (WIDTH - text.length()) / 2);
        return " ".repeat(left) + text;
    }
}
