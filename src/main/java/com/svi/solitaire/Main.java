package com.svi.solitaire;

import com.svi.solitaire.game.GameLauncher;

/**
 * Entry point of the Klondike Solitaire application.
 */
public class Main {

    /** Not instantiable: the program starts in {@link #main(String[])}. */
    private Main() {
    }

    /**
     * Starts the console application.
     *
     * @param args command-line arguments; {@code --no-color} turns ANSI colors off
     */
    static void main(String[] args) {
        GameLauncher.launch(args);
    }
}
