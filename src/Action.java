public class Action {
    private Pawn pawn;
    private int newIndex;
    private boolean isDetailed = false;

    public Action(Pawn pawn, int newIndex) {
        this.pawn = pawn;
        this.newIndex = newIndex;
    }

    public void setDetailed(boolean detailed) {
        isDetailed = detailed;
    }
}
