package enums;

public enum Constants {
    COLUMN_WIDTH(16);

    private final int value;

    Constants(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
