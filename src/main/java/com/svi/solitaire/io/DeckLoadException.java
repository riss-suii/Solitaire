package com.svi.solitaire.io;


/**
 * Thrown when a deck file cannot be read or does not hold a valid 52-card deck.
 * The message says what is wrong and is suitable to show to the user.
 */
public class DeckLoadException extends Exception {

    /**
     * Creates the exception with a message.
     *
     * @param message what is wrong, suitable to show to the user
     */
    public DeckLoadException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and the problem that caused it.
     *
     * @param message what is wrong, suitable to show to the user
     * @param cause   the underlying problem
     */
    public DeckLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
