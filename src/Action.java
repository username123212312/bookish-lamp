public class Action {
    private final Pawn pawn;
    private final int newIndex;
    private boolean isDetailed = false;

    public Action(Pawn pawn, int newIndex) {
        this.pawn = pawn;
        this.newIndex = newIndex;
    }

    private Action(Pawn pawn, int newIndex, boolean isDetailed) {
        this.pawn = pawn;
        this.newIndex = newIndex;
        this.isDetailed = isDetailed;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public int getNewIndex() {
        return newIndex;
    }

    public Action deepCopy() {
        return new Action(pawn.deepCopy(), newIndex, isDetailed);
    }

    public void setDetailed(boolean detailed) {
        isDetailed = detailed;
    }

    @Override
    public String toString() {
        return "Move " + (pawn.isWhite() ? "White" : "Black")
                + " Pawn From " + pawn.getIndex() + " To " + newIndex;
    }
}