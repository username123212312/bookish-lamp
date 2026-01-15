public class Action {
    private static boolean isDetailed = false;
    private ActionType actionType;
    private Pawn toBeReplaced;
    private Pawn pawn;
    int newIndex;

    public Action(Pawn pawn, int newIndex, ActionType actionType) {
        this.actionType = actionType;
        this.pawn = pawn;
        this.newIndex = newIndex;
    }

    public Action(Pawn pawn, int newIndex, ActionType actionType, Pawn toBeReplaced) {
        this.pawn = pawn;
        this.newIndex = newIndex;
        this.actionType = actionType;
        this.toBeReplaced = toBeReplaced;
    }

    public Action deepCopy() {
        return new Action(pawn.deepCopy(), newIndex, actionType);
    }

    public void setToBeReplaced(Pawn toBeReplaced) {
        this.toBeReplaced = toBeReplaced;
    }

    public static void setIsDetailed(boolean isDetailed) {
        Action.isDetailed = isDetailed;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType, Pawn toBeReplaced) {
        this.actionType = actionType;
        this.toBeReplaced = toBeReplaced;
    }

    public Pawn getPawn() {
        return pawn;
    }

    public int getNewIndex() {
        return newIndex;
    }

    @Override
    public String toString() {
        return switch (actionType) {
            case ActionType.NORMAL -> "Move " + (pawn.isWhite() ? "White" : "Black")
                    + " Pawn From " + pawn.getIndex() + " To " + newIndex;
            case ActionType.PROMOTION -> "Promote " + (pawn.isWhite() ? "White" : "Black")
                    + " Pawn ";
            case ActionType.REPLACEMENT -> "Swapped " + (pawn.isWhite() ? "White" : "Black")
                    + " Pawn ( " + pawn.getIndex() +" ) " + " With " + (toBeReplaced.isWhite() ? "White" : "Black")
                    + " Pawn ( " + newIndex + " ) "
            ;
        };
    }
}
