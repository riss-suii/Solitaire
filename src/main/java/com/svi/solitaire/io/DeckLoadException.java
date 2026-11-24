package com.svi.solitaire.io;

/**
 * Thrown when a deck file cannot be read or does not hold a valid 52-card deck.
 * The message says what is wrong and is suitable to show to the user.
 */
public class DeckLoadException extends Exception {

    public DeckLoadException(String message) {
        super(message);
    }

    public DeckLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}