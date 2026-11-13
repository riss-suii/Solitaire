package enums;

public enum DrawMove {
    DRAW_ONE(1),
    DRAW_THREE(3);

    private final int value;

    DrawMove(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
