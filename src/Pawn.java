public class Pawn {
    private final boolean isWhite;
    private int index;

    public Pawn(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isWhite() {
        return isWhite;
    }

    @Override
    public String toString() {
        return isWhite ? "W" : "B";
    }
}
