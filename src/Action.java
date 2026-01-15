public class Action {
    private static boolean isDetailed = false;
    private ActionType actionType;
    private Pawn pawn;
    int newIndex;

    public Action(Pawn pawn, int newIndex, ActionType actionType) {
        this.actionType = actionType;
        this.pawn = pawn;
        this.newIndex = newIndex;
    }

    public Action deepCopy() {
        return new Action(pawn.deepCopy(), newIndex, actionType);
    }

    public static void setIsDetailed(boolean isDetailed) {
        Action.isDetailed = isDetailed;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public int getNewIndex() {
        return newIndex;
    }

    @Override
    public String toString() {
        if (newIndex == 30) {
            return "Promote " + (pawn.isWhite() ? "White" : "Black")
                    + " Pawn ";
        } else {
            return "Move " + (pawn.isWhite() ? "White" : "Black")
                    + " Pawn From " + pawn.getIndex() + " To " + newIndex;
        }
    }
}
