// Game.java - Complete with Expectiminimax
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private final static int[] tosses = {1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 5};
    private Board board;
    private final Scanner scanner;

    // Define pieces
    private final char COMPUTER = 'W';
    private final char HUMAN = 'B';

    // Constants for expectiminimax
    private static final int MAX_DEPTH = 3;
    private static final double[] PROBABILITIES = {
            4.0/16.0,  // Roll = 1 (25%)
            6.0/16.0,  // Roll = 2 (37.5%)
            4.0/16.0,  // Roll = 3 (25%)
            1.0/16.0,  // Roll = 4 (6.25%)
            1.0/16.0   // Roll = 5 (6.25%)
    };
    private static final int[] ROLLS = {1, 2, 3, 4, 5};

    public Game() {
        this.board = new Board();
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("Welcome to Senet (3x10)!");
        System.out.println("Game description");

        while (!board.isFinal()) {
            System.out.println(board.promotedNum());
            System.out.println("------------------------------------------------");
            System.out.println(board.toString());
            System.out.println("------------------------------------------------");

            // --- Human Turn ---
            System.out.println("=== HUMAN TURN ===");
            humanPlay(HUMAN);
            if (board.checkWin(HUMAN)) {
                System.out.println(board.toString());
                System.out.println("You Win!");
                break;
            }

            System.out.println(board.promotedNum());
            System.out.println("------------------------------------------------");
            System.out.println(board.toString());
            System.out.println("------------------------------------------------");

            // --- Computer Turn ---
            System.out.println("=== COMPUTER TURN ===");
            computerPlay();
            if (board.checkWin(COMPUTER)) {
                System.out.println(board.toString());
                System.out.println("Computer Wins! You Lose!");
                break;
            }
        }
    }

    private int toss() {
        return tosses[new Random().nextInt(tosses.length)];
    }

    private void humanPlay(char player) {
        int tossValue = toss();
        System.out.println("Toss: " + tossValue);

        List<Board> possibleMoves = board.generateNextStates(player, tossValue);

        if (possibleMoves.isEmpty()) {
            System.out.println("No valid moves available. Skipping turn.");
            board.applySkipTurn(player);
            return;
        }

        System.out.println("Available moves:");
        for (int i = 0; i < possibleMoves.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + possibleMoves.get(i).getAction());
            System.out.println(possibleMoves.get(i));
        }

        boolean validMove = false;
        while (!validMove) {
            System.out.print("Select a move (1-" + possibleMoves.size() + "): ");

            while (!scanner.hasNextInt()) {
                System.out.println("That's not a valid integer! Try again:");
                scanner.next();
            }

            int moveChoice = scanner.nextInt();
            scanner.nextLine();

            if (moveChoice >= 1 && moveChoice <= possibleMoves.size()) {
                board = possibleMoves.get(moveChoice - 1);
                System.out.println("Move applied successfully!");
                validMove = true;
            } else {
                System.out.println("Invalid choice. Please select a number between 1 and " + possibleMoves.size());
            }
        }
    }

    // --- EXPECTIMINIMAX COMPUTER PLAY ---

    private void computerPlay() {
        int tossValue = toss();
        System.out.println("Computer toss: " + tossValue);

        // Get best move for this specific dice roll using expectiminimax
        Board bestMove = getBestMoveForRoll(board, COMPUTER, tossValue);

        if (bestMove != null) {
            board = bestMove;
            System.out.println("Computer plays: " + bestMove.getAction());
        } else {
            System.out.println("No valid moves. Computer skips turn.");
            board.applySkipTurn(COMPUTER);
        }
    }

    private Board getBestMoveForRoll(Board currentBoard, char player, int diceRoll) {
        List<Board> possibleMoves = currentBoard.generateNextStates(player, diceRoll);

        if (possibleMoves.isEmpty()) {
            return null;
        }

        Board bestBoard = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (Board move : possibleMoves) {
            // After computer moves, it's CHANCE (dice) → MIN (opponent)
            double value = expectiminimax(move, MAX_DEPTH - 1, "CHANCE", player == COMPUTER);
            if (value > bestValue) {
                bestValue = value;
                bestBoard = move;
            }
        }

        return bestBoard;
    }

    // Main recursive function with all three node types
    private double expectiminimax(Board board, int depth, String nodeType, boolean currentPlayerIsWhite) {
        // Terminal node
        if (depth == 0 || board.isFinal()) {
            return SenetHeuristic.minimalHeuristic(board, currentPlayerIsWhite);
        }

        return switch (nodeType) {
            case "MAX" -> maxNode(board, depth, currentPlayerIsWhite);
            case "MIN" -> minNode(board, depth, currentPlayerIsWhite);
            case "CHANCE" -> chanceNode(board, depth, !currentPlayerIsWhite);
            default -> 0;
        };
    }

    private double maxNode(Board board, int depth, boolean currentPlayerIsWhite) {
        // MAX node: Computer chooses best move
        char player = currentPlayerIsWhite ? 'W' : 'B';
        double maxValue = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < ROLLS.length; i++) {
            int diceRoll = ROLLS[i];
            List<Board> nextStates = board.generateNextStates(player, diceRoll);

            if (nextStates.isEmpty()) {
                // No moves - skip turn
                Board skipped = board.deepCopy();
                skipped.applySkipTurn(player);
                double value = PROBABILITIES[i] *
                        expectiminimax(skipped, depth - 1, "CHANCE", !currentPlayerIsWhite);
                maxValue = Math.max(maxValue, value);
                continue;
            }

            // Find best move for this dice roll
            double bestForThisRoll = Double.NEGATIVE_INFINITY;
            for (Board nextState : nextStates) {
                double value = PROBABILITIES[i] *
                        expectiminimax(nextState, depth - 1, "CHANCE", !currentPlayerIsWhite);
                bestForThisRoll = Math.max(bestForThisRoll, value);
            }
            maxValue = Math.max(maxValue, bestForThisRoll);
        }

        return maxValue;
    }

    private double minNode(Board board, int depth, boolean currentPlayerIsWhite) {
        // MIN node: Human chooses worst move for computer
        char player = currentPlayerIsWhite ? 'W' : 'B';
        double minValue = Double.POSITIVE_INFINITY;

        for (int i = 0; i < ROLLS.length; i++) {
            int diceRoll = ROLLS[i];
            List<Board> nextStates = board.generateNextStates(player, diceRoll);

            if (nextStates.isEmpty()) {
                // No moves - skip turn
                Board skipped = board.deepCopy();
                skipped.applySkipTurn(player);
                double value = PROBABILITIES[i] *
                        expectiminimax(skipped, depth - 1, "MAX", !currentPlayerIsWhite);
                minValue = Math.min(minValue, value);
                continue;
            }

            // Find worst move for computer (human minimizes)
            double worstForThisRoll = Double.POSITIVE_INFINITY;
            for (Board nextState : nextStates) {
                double value = expectiminimax(nextState, depth - 1, "MAX", !currentPlayerIsWhite);
                worstForThisRoll = Math.min(worstForThisRoll, value);
            }
            minValue = Math.min(minValue, PROBABILITIES[i] * worstForThisRoll);
        }

        return minValue;
    }

    private double chanceNode(Board board, int depth, boolean nextPlayerIsWhite) {
        // CHANCE node: Average over dice outcomes
        double expectedValue = 0;
        char player = nextPlayerIsWhite ? 'W' : 'B';

        for (int i = 0; i < ROLLS.length; i++) {
            int diceRoll = ROLLS[i];
            List<Board> nextStates = board.generateNextStates(player, diceRoll);

            if (nextStates.isEmpty()) {
                // No moves - skip turn
                Board skipped = board.deepCopy();
                skipped.applySkipTurn(player);
                expectedValue += PROBABILITIES[i] *
                        expectiminimax(skipped, depth - 1, "MIN", nextPlayerIsWhite);
                continue;
            }

            // After chance comes MIN (opponent's turn to choose)
            double bestForThisRoll = Double.POSITIVE_INFINITY;
            for (Board nextState : nextStates) {
                double value = expectiminimax(nextState, depth - 1, "MIN", nextPlayerIsWhite);
                bestForThisRoll = Math.min(bestForThisRoll, value);
            }
            expectedValue += PROBABILITIES[i] * bestForThisRoll;
        }

        return expectedValue;
    }

}