// A simple container for the Best Move and its Score
public record MoveResult(Board bestBoard, int score) {}

// Example usage:
// MoveResult result = maxMove(board);
// To get the best move: result.bestBoard();
// To get the score: result.score();