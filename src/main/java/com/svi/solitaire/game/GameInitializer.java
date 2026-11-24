    package com.svi.solitaire.game;

    import com.svi.solitaire.constants.GameConstants;
    import com.svi.solitaire.enums.DrawMode;
    import com.svi.solitaire.models.Deck;
    import com.svi.solitaire.shuffle.ShuffleStrategy;

    /**
     * Shuffles a deck with the given strategy and deals a new Klondike layout.
     * It holds no state, so one instance serves every game.
     */
    public class GameInitializer {

        /** Creates an initializer. It holds no state. */
        public GameInitializer() {
        }

        /**
         * Starting deck, then shuffle (the starting deck itself is left untouched), then deal.
         *
         * @param startingDeck the deck to start from
         * @param shuffle      how to shuffle it
         * @param drawMode     how many cards a draw turns over
         * @return the dealt game
         */
        public GameState newGame(Deck startingDeck, ShuffleStrategy shuffle, DrawMode drawMode) {
            return deal(new Deck(shuffle.shuffle(startingDeck.getCards())), drawMode);
        }

        /**
         * Deals the deck as is (no shuffling): the tableau is dealt row by row, the last card of each
         * pile face up, and the remaining 24 cards form the stock in deck order. The deck passed in is
         * left untouched; the cards are dealt from a copy.
         *
         * @param deck     the 52 cards in deal order, top card first
         * @param drawMode how many cards a draw turns over
         * @return the dealt game
         */
        public GameState deal(Deck deck, DrawMode drawMode) {
            Deck remaining = new Deck(deck.getCards());
            int piles = GameConstants.TABLEAU_PILE_COUNT;
            GameState state = new GameState(drawMode);

            for (int row = 0; row < piles; row++) {
                for (int pile = row; pile < piles; pile++) {
                    state.dealToTableau(pile, remaining.deal(), pile == row);
                }
            }
            state.setStock(remaining.drain());
            return state;
        }
    }
