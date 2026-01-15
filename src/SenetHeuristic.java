import java.util.Map;

public class SenetHeuristic {
    public static double minimalHeuristic(Board board, boolean isWhitePlayer) {
        // Just: finished pieces difference + sum of positions difference
        int myFinished = isWhitePlayer ? board.whiteFinishList.size() : board.blackFinishList.size();
        int oppFinished = isWhitePlayer ? board.blackFinishList.size() : board.whiteFinishList.size();

        Map<Integer, Pawn> myPieces = isWhitePlayer ? board.whitePawnMap : board.blackPawnMap;

        int mySum = 0, oppSum = 0;
        for (Pawn p : myPieces.values()) mySum += p.getIndex();

        // Get opponent pieces separately to avoid unused variable
        Map<Integer, Pawn> oppPieces = isWhitePlayer ? board.blackPawnMap : board.whitePawnMap;
        for (Pawn p : oppPieces.values()) oppSum += p.getIndex();

        double score = (myFinished * 20 + mySum) - (oppFinished * 20 + oppSum);

        // Add strategy: If ahead, play safe; if behind, take risks
        if (myFinished > oppFinished) {
            // We're ahead - avoid dangerous squares
            score += safeBonus(board, isWhitePlayer);
        } else if (myFinished < oppFinished) {
            // We're behind - take more risks
            score += aggressiveBonus(board, isWhitePlayer);
        }

        // Always: Avoid House of Water (square 26) unless it helps finish
        score += waterSquarePenalty(board, isWhitePlayer);

        return score;
    }

    private static double safeBonus(Board board, boolean isWhitePlayer) {
        double bonus = 0;
        Map<Integer, Pawn> myPieces = isWhitePlayer ? board.whitePawnMap : board.blackPawnMap;

        // When ahead: Stay away from squares 27-29 (can be sent back)
        for (Pawn p : myPieces.values()) {
            int pos = p.getIndex();
            if (pos >= 27 && pos <= 29) {
                if (pos == 28) {
                    bonus -= 3;
                } else {
                    bonus -= 5; // Penalty for being in danger zone when ahead
                }
            }
        }
        return bonus;
    }

    private static double aggressiveBonus(Board board, boolean isWhitePlayer) {
        double bonus = 0;
        Map<Integer, Pawn> myPieces = isWhitePlayer ? board.whitePawnMap : board.blackPawnMap;

        // When behind: Take risks to finish pieces
        for (Pawn p : myPieces.values()) {
            int pos = p.getIndex();
            // Bonus for being close to finish when behind
            if (pos >= 25) {
                bonus += 3;
            }
        }
        return bonus;
    }

    private static double waterSquarePenalty(Board board, boolean isWhitePlayer) {
        double penalty = 0;
        Map<Integer, Pawn> myPieces = isWhitePlayer ? board.whitePawnMap : board.blackPawnMap;

        // House of Water (square 26) is dangerous - avoid it
        for (Pawn p : myPieces.values()) {
            if (p.getIndex() == 26) {
                penalty -= 10; // Big penalty for being stuck on water square
            }
        }
        return penalty;
    }


}