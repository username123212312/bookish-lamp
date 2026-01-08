import java.util.Map;

public class SenetHeuristic {
    public static double minimalHeuristic(Board board, boolean isWhitePlayer) {
        // Just: finished pieces difference + sum of positions difference
        int myFinished = isWhitePlayer ? board.whiteFinishList.size() : board.blackFinishList.size();
        int oppFinished = isWhitePlayer ? board.blackFinishList.size() : board.whiteFinishList.size();

        Map<Integer, Pawn> myPieces = isWhitePlayer ? board.whitePawnMap : board.blackPawnMap;
        Map<Integer, Pawn> oppPieces = isWhitePlayer ? board.blackPawnMap : board.whitePawnMap;

        int mySum = 0, oppSum = 0;
        for (Pawn p : myPieces.values()) mySum += p.getIndex();
        for (Pawn p : oppPieces.values()) oppSum += p.getIndex();

        return (myFinished * 20 + mySum) - (oppFinished * 20 + oppSum);
    }
}
