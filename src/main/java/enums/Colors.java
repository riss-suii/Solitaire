package enums;

public enum Colors {
    Red("\u001B[31m"),
    Yellow("\u001B[38;5;214m"),
    BLUE("\u001B[38;5;18m"),
    RESET("\u001B[0m"),
    BOLD("\u001B[1m");

    public final String code;

    Colors(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
