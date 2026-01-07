public class Pawn {
    private final boolean isWhite;
    private int index;

    public Pawn(boolean isWhite, int index) {
        this.isWhite = isWhite;
        this.index = index;
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

    public Pawn deepCopy() {
        return new Pawn(isWhite, index);
    }

    @Override
    public String toString() {
        return isWhite ? "\u25CF" : "\u25CB";
    }
}