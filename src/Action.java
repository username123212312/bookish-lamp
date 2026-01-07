public record Action(Pawn pawn, int newIndex) {
    private static boolean isDetailed = false;

    public Action deepCopy() {
        return new Action(pawn.deepCopy(), newIndex);
    }

    public static void setIsDetailed(boolean isDetailed) {
        Action.isDetailed = isDetailed;
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
