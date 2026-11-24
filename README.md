# Klondike Solitaire

A console Klondike Solitaire written in Java. A rule-based computer player plays each game, using only
the cards a real player could see. You can watch a single game move by move, or run thousands of games
and get win-rate statistics. Decks are loaded from a text file, so any deal can be replayed exactly.

## Features

- **Draw 1 or Draw 3** Klondike.
- **Watch mode**: prints every move, with the board after each one or just the move list and final board.
- **Simulation mode**: plays many games in parallel across all CPU cores and reports wins, losses and win rate.
- **Shuffles**: random, perfect in-shuffle, perfect out-shuffle (repeated any number of times), or no shuffle.
- **Deck files**: load any 52-card deck from a file; invalid files are rejected with a clear message.
- **No cheating**: the player never sees face-down cards. It learns the stock order only after the stock has
  been turned over once.
- **Colored console output**, which can be turned off.

## Requirements

- JDK 25 (`pom.xml` sets `maven.compiler.source` and `target` to 25)
- Maven 3.x (optional, you can also compile with `javac`)

## Build and run

With Maven:

```bash
mvn compile
java -cp target/classes com.svi.solitaire.Main
```

Without Maven:

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
cp src/main/resources/*.txt out/
java -cp out com.svi.solitaire.Main
```

Or run `Main` from your IDE.

To turn colors off (useful if your terminal shows the escape codes as garbage), pass `--no-color` or set the
`NO_COLOR` environment variable:

```bash
java -cp target/classes com.svi.solitaire.Main --no-color
```

## How to use

The program asks for a deck file first, then walks through these menus:

1. **Draw mode**: `1` or `3` cards per draw.
2. **Shuffle**: Random, Perfect In, Perfect Out or No shuffle. The perfect shuffles ask how many times to shuffle (1 to 52).
3. **Mode**:
   - *Watch one game*: choose whether to print the board after every move.
   - *Run a simulation*: choose how many games to play (up to 100,000 for a random shuffle).
4. **Play again**: same deck, another deck file, or quit.

Notes on simulations:

- **Random**: each game is shuffled with its own generator, so every run plays different games.
- **No shuffle**: always deals the same game, so only one game is played.
- **Perfect In / Perfect Out**: game *n* uses *n* rounds of shuffling, so you can see how the win rate changes
  as the deck gets mixed (up to 200 games).

### Example: watching a game

Loading `input.txt`, Draw 3, no shuffle, move list only:

```
--- Step 1: Move H-A from tableau 3 to the foundation ---

--- Step 2: Move H-2 from tableau 6 to the foundation ---

--- Step 3: Move D-A from tableau 1 to the foundation ---

--- Step 4: Move S-K from tableau 7 to tableau 1 ---
...
==============================================================================
                YOU WIN!  All 52 cards are on the foundations.
                   Moves: 90    Cards on foundations: 52/52
==============================================================================
```

### Example: simulation

```
  Draw mode          : Draw 1
  Shuffle            : Random
  Games played       : 1000
  Wins/Losses        : 518/482
  Win rate           : 51.8%
```

The numbers change from run to run because the shuffle is random.

## Deck files

A deck file holds all 52 cards, top of the deck first, written as `SUIT-RANK` and separated by commas and/or
new lines.

- Suits: `H` hearts, `D` diamonds, `C` clubs, `S` spades
- Ranks: `A`, `2` to `10`, `J`, `Q`, `K`

```
D-A,D-K,D-Q,D-J,D-10,D-9,D-8,D-7,D-6,D-5,D-4,D-3,D-2,
H-A,H-K,H-Q,H-J,H-10,H-9,H-8,H-7,H-6,H-5,H-4,H-3,H-2,
...
```

The file name is looked up on the classpath first (the files in `src/main/resources`), then as a path on disk.
So you can type `input.txt` for a bundled file or `C:\decks\mydeck.txt` for your own.

The files in `src/main/resources`:

| File                    | Contents                                                                             |
|-------------------------|--------------------------------------------------------------------------------------|
| `input.txt`, `wrongformat.txt` | Valid 52-card decks in suit order                                                    |
| `duplicate.txt`             | Invalid on purpose: contains `D-A` twice (the program reports a duplicate card)      |
| `empty.txt`              | Invalid on purpose: contains `D-` with no rank (the program reports an invalid card) |

A deck is rejected if a card is badly written, appears twice, or the total is not exactly 52.

## How the computer player decides

`RuleBasedPlayer` scores every legal move and plays the best one. In order of priority:

1. Move "free" cards to the foundation (Aces, 2s, and any card that can no longer be needed in the tableau).
2. Move a King with a stack into an empty column.
3. Flip a face-down card by moving a pile's whole face-up run onto another pile.
4. Move a King from the waste into an empty column.
5. Empty a column, but only when a King is ready to fill it.
6. Play the waste card onto the tableau.
7. Draw from the stock, only when nothing else is possible.

If the same position comes up again, the player gets more willing to take moves it normally avoids, such as
putting a card on the foundation early. A game ends when it is won, when no move is left, when a position
has repeated more than twice, or after 2,000 moves (see `PlayLimits`).

## Project structure

```
src/main/java/com/svi/solitaire
├── Main.java                 entry point
├── constants/                GameConstants (game sizes), PlayLimits (move and simulation limits)
├── enums/                    Suit, Rank, DrawMode, MoveType, GameStatus, Colors (ANSI codes)
├── models/                   Card, Deck, BoardMove
│   └── piles/                TableauPile, Foundation, Talon (stock + waste as a list and a pointer)
├── rules/                    GameRules: what is legal, and when the game is won
├── game/                     GameLauncher (menus), GameEngine (plays one game), GameInitializer (deal),
│                             GameState (the position), MoveExecutor (applies moves)
├── strategy/                 RuleBasedPlayer (chooses moves), PlayerView (what the player may see)
├── shuffle/                  ShuffleStrategy and its implementations, ShuffleFactory
├── simulation/               SimulationRunner, SimulationStatistics
├── io/                       ConsoleDisplay (output), InputHandler (input), FileLoader, DeckLoadException
└── util/                     CardFactory (shared Card instances)
```

Design notes:

- **Separation of concerns**: `GameState` only stores a position, `GameRules` decides what is legal,
  `MoveExecutor` changes the state, and `GameEngine` runs the loop.
- **Talon is plain data**: `Talon` is just a list of cards and a pointer with getters and setters. Drawing,
  recycling and taking the waste card, and the checks for them, live in `MoveExecutor`. The setter refuses
  a pointer outside the list, but otherwise callers must keep the pointer and the list consistent.
- **Dependency injection**: `GameLauncher.launch` builds the whole object graph. Output streams, input and random
  sources are passed in, so each part can be tested on its own.
- **Compact state**: foundations are stored as four heights, and the stock and waste share one list with a pointer,
  so drawing and recycling never copy cards.
- **Thread-safe simulation**: the engine and player hold no per-game state, and each simulated game gets its own
  random generator.

## Generating the Javadoc

```bash
mvn javadoc:javadoc
```

or, without Maven:

```bash
javadoc -d docs -sourcepath src/main/java -subpackages com.svi.solitaire
```

The HTML is written to `target/reports/apidocs` (Maven) or `docs` (plain `javadoc`).
