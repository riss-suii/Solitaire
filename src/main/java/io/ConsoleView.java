package io;

import java.util.*;
import enums.Colors;
import enums.Constants;

public class ConsoleView {
    static final String RED = "\u001B[31m";
    static final String RESET = "\u001B[0m";
    static final int W = 16; // column width

    static class Card {
        int rank;          // 1..13
        char suit;         // 'S','H','D','C'
        boolean faceUp;

        Card(int rank, char suit, boolean faceUp) {
            this.rank = rank; this.suit = suit; this.faceUp = faceUp;
        }

        String label() {
            String r = switch (rank) {
                case 1 -> "A"; case 11 -> "J"; case 12 -> "Q"; case 13 -> "K";
                default -> String.valueOf(rank);
            };
            String s = switch (suit) {
                case 'S' -> "♠"; case 'H' -> "♥"; case 'D' -> "♦"; default -> "♣";
            };
            return r + s;
        }

        boolean isRed() { return suit == 'H' || suit == 'D'; }
    }

    // Pad based on VISIBLE length, since color codes add invisible characters
    static String cell(String text, boolean red) {
        String shown = red ? RED + text + RESET : text;
        int pad = Math.max(0, W - text.length());
        return shown + " ".repeat(pad);
    }

    static String cardCell(Card c) {
        if (c == null) return " ".repeat(W);
        if (!c.faceUp) return cell("[ ]", false);
        return cell(c.label(), c.isRed());
    }

    static void render(List<Card> stock, List<Card> waste,
                       List<List<Card>> foundations,
                       List<List<Card>> columns, int score) {

        // --- Top row headers ---
        System.out.print(cell("Stock", false));
        System.out.print(cell("Waste", false));
        System.out.print(" ".repeat(W));          // gap
        for (int i = 1; i <= 4; i++) System.out.print(cell("Foundation " + i, false));
        System.out.println();

        // --- Top row boxes ---
        System.out.print(cell(stock.isEmpty() ? "[ ]" : "[ ]", false));
        Card w = waste.isEmpty() ? null : waste.get(waste.size() - 1);
        System.out.print(w == null ? cell("[ ]", false) : cardCell(w));
        System.out.print(" ".repeat(W));
        for (List<Card> f : foundations) {
            Card top = f.isEmpty() ? null : f.get(f.size() - 1);
            System.out.print(top == null ? cell("[   ]", false) : cardCell(top));
        }
        System.out.println();

        // --- Top row counts ---
        System.out.print(cell("(" + stock.size() + " cards)", false));
        System.out.print(cell("(" + waste.size() + " cards)", false));
        System.out.print(" ".repeat(W));
        for (List<Card> f : foundations)
            System.out.print(cell("(" + f.size() + " cards)", false));
        System.out.println("\n");

        // --- Column headers ---
        for (int i = 0; i < 7; i++) System.out.print(cell("Column " + (i + 1), false));
        System.out.println();
        for (List<Card> col : columns)
            System.out.print(cell("(" + col.size() + " cards)", false));
        System.out.println();

        // --- Tableau rows ---
        int maxHeight = columns.stream().mapToInt(List::size).max().orElse(0);
        for (int row = 0; row < maxHeight; row++) {
            for (List<Card> col : columns) {
                Card c = row < col.size() ? col.get(row) : null;
                System.out.print(cardCell(c));
            }
            System.out.println();
        }

        System.out.println("\nScore: " + score + "\n");
    }

    public static void main(String[] args) {
        // Build a sample deal
        List<Card> stock = new ArrayList<>();
        List<Card> waste = new ArrayList<>(List.of(new Card(2, 'D', true)));
        List<List<Card>> foundations = List.of(
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        List<List<Card>> columns = new ArrayList<>();
        char[] suits = {'C', 'D', 'H', 'H', 'H', 'S', 'H'};
        int[] ranks  = {7, 3, 4, 7, 9, 7, 6};
        for (int i = 0; i < 7; i++) {
            List<Card> col = new ArrayList<>();
            for (int j = 0; j < i; j++) col.add(new Card(1, 'S', false)); // hidden
            col.add(new Card(ranks[i], suits[i], true));                  // top card
            columns.add(col);
        }

        render(stock, waste, foundations, columns, 0);

        Scanner in = new Scanner(System.in);
        System.out.print("solitaire-cli > ");
        String cmd = in.nextLine();
        // parse cmd here...
    }
}