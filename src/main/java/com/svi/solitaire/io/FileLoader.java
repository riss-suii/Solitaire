package com.svi.solitaire.io;

import com.svi.solitaire.enums.Constants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.Deck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;

/**
 * Loads and validates a deck from the file whose name the user typed in.
 * <p>
 * Format: all 52 cards as {@code SUIT-RANK} (e.g. {@code D-A}, {@code H-10}), separated by commas
 * and/or new lines, top of the deck first.
 */
public class FileLoader {

    private final ConsoleDisplay display;

    public FileLoader(ConsoleDisplay display) {
        this.display = display;
    }

    /**
     * Reads the named deck file. The name is looked up on the classpath first
     * (e.g. {@code input.txt} from src/main/resources), then as a path on disk.
     * <p>
     * If anything is wrong, the reason is printed and an empty deck is returned,
     * so the caller only has to check {@code deck.size()}.
     */
    public Deck loadDeck(String fileName) {
        try (InputStream input = openFile(fileName)) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8))) {
                return readCards(reader);
            }
        } catch (IOException | IllegalArgumentException e) {
            String reason = (e instanceof IOException) ? "File cannot be opened." : e.getMessage();
            display.showError(reason + " Please check the " + fileName + " file.");
            return new Deck();
        }
    }

    /**
     * Opens the file from the classpath, or from disk when it is not a resource.
     *
     * @throws IllegalArgumentException if no such file exists
     */
    private InputStream openFile(String fileName) throws IOException {
        InputStream resource = FileLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (resource != null) {
            return resource;
        }
        Path path = Path.of(fileName);
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("File does not exist.");
        }
        return Files.newInputStream(path);
    }

    /**
     * Reads every card from the file and checks the deck rules: no duplicates and exactly 52 cards.
     */
    private Deck readCards(BufferedReader reader) throws IOException {
        Deck deck = new Deck();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            while (tokenizer.hasMoreTokens()) {
                Card card = parseCard(tokenizer.nextToken().trim());
                if (deck.contains(card)) {
                    throw new IllegalArgumentException("Duplicate card detected.");
                }
                if (deck.size() == Constants.TOTAL_CARDS.getValue()) {
                    throw new IllegalArgumentException("Too many cards in the deck (expected exactly " + Constants.TOTAL_CARDS
                            + ", but found more).");
                }
                deck.addCard(card);
            }
        }
        if (deck.size() != Constants.TOTAL_CARDS.getValue()) {
            throw new IllegalArgumentException("Invalid number of cards in the deck (found " + deck.size()
                    + ", expected " + Constants.TOTAL_CARDS + ").");
        }
        return deck;
    }

    /**
     * Converts text such as D-A into a Card object.
     */
    private Card parseCard(String token) {
        StringTokenizer tokenizer = new StringTokenizer(token, "-");
        if (tokenizer.countTokens() != 2) {
            throw new IllegalArgumentException("Invalid card format (expected SUIT-RANK, e.g. D-A).");
        }
        Suit suit = Suit.fromSymbol(tokenizer.nextToken());
        Rank rank = Rank.fromSymbol(tokenizer.nextToken());
        if (suit == null || rank == null) {
            throw new IllegalArgumentException("Invalid card format (unknown suit or rank).");
        }
        return new Card(rank, suit);
    }
}
