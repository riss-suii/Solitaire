package enums;

public enum Suit {
    CLUBS("C", "♣"),
    SPADES("S", "♠"),
    HEARTS("H", "♥"),
    DIAMONDS("D", "♦");

    private final String symbol;
    private final String icon;

    Suit(String symbol, String icon) {
        this.symbol = symbol;
        this.icon = icon;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getIcon() {
        return icon;
    }

    public boolean isRed() {
        return this == HEARTS || this == DIAMONDS;
    }
}