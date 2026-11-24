package com.svi.solitaire.io;

import com.svi.solitaire.constants.GameConstants;
import com.svi.solitaire.enums.Rank;
import com.svi.solitaire.enums.Suit;
import com.svi.solitaire.models.Card;
import com.svi.solitaire.models.Deck;
import com.svi.solitaire.util.CardFactory;

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
 * <p>
 * This class only reads and validates. It never prints anything; problems are reported by throwing
 * a {@link DeckLoadException}, and the caller decides how to react.
 */
public class FileLoader {

    /**
     * Reads the named deck file. The name is looked up on the classpath first
     * (e.g. {@code input.txt} from src/main/resources), then as a path on disk.
     *
     * @param fileName the resource name or file path
     * @return a valid deck of exactly 52 different cards, top card first
     * @throws DeckLoadException if the file does not exist, cannot be read, contains an unknown or
     *                           badly written card, has a duplicate card, or does not hold exactly 52 cards
     */
    public Deck loadDeck(String fileName) throws DeckLoadException {
        try (InputStream input = openFile(fileName);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(input, StandardCharsets.UTF_8))) {
            return readCards(reader);
        } catch (IOException e) {
            throw new DeckLoadException("File cannot be opened.", e);
        }
    }

    /**
     * Opens the file from the classpath, or from disk when it is not a resource.
     *
     * @throws DeckLoadException if no such file exists
     * @throws IOException       if the file exists but cannot be opened
     */
    private InputStream openFile(String fileName) throws IOException, DeckLoadException {
        InputStream resource = FileLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (resource != null) {
            return resource;
        }
        Path path = Path.of(fileName);
        if (!Files.isRegularFile(path)) {
            throw new DeckLoadException("File does not exist.");
        }
        return Files.newInputStream(path);
    }

    /**
     * Reads every card from the file and checks the deck rules: no duplicates and exactly 52 cards.
     */
    private Deck readCards(BufferedReader reader) throws IOException, DeckLoadException {
        int expected = GameConstants.TOTAL_CARDS;
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
                    throw new DeckLoadException("Duplicate card detected: " + card + ".");
                }
                if (deck.count() == expected) {
                    throw new DeckLoadException("Too many cards in the deck (expected exactly "
                            + expected + ", but found more).");
                }
                deck.addCard(card);
            }
        }
        if (deck.count() != expected) {
            throw new DeckLoadException("Invalid number of cards in the deck (found " + deck.count()
                    + ", expected " + expected + ").");
        }
        return deck;
    }

    /**
     * Converts text such as D-A into a Card object.
     *
     * @throws DeckLoadException if the text is not {@code SUIT-RANK} or uses an unknown suit or rank
     */
    private Card parseCard(String token) throws DeckLoadException {
        StringTokenizer tokenizer = new StringTokenizer(token, "-");
        if (tokenizer.countTokens() != 2) {
            throw new DeckLoadException("Invalid card \"" + token + "\" (expected SUIT-RANK, e.g. D-A).");
        }
        try {
            Suit suit = Suit.fromSymbol(tokenizer.nextToken());
            Rank rank = Rank.fromSymbol(tokenizer.nextToken());
            return CardFactory.of(rank, suit);
        } catch (IllegalArgumentException e) {
            throw new DeckLoadException("Invalid card \"" + token + "\": " + e.getMessage() + ".", e);
        }
    }
}
