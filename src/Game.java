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
            4.0 / 16.0,  // Roll = 1 (25%)
            6.0 / 16.0,  // Roll = 2 (37.5%)
            4.0 / 16.0,  // Roll = 3 (25%)
            1.0 / 16.0,  // Roll = 4 (6.25%)
            1.0 / 16.0   // Roll = 5 (6.25%)
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
            computerPlay(HUMAN);
            if (board.checkWin(HUMAN)) {
                System.out.println(board.toString());
                System.out.println(board.promotedNum());
                System.out.println("You Win!");
                break;
            }

            System.out.println(board.promotedNum());
            System.out.println("------------------------------------------------");
            System.out.println(board.toString());
            System.out.println("------------------------------------------------");

            // --- Computer Turn ---
            System.out.println("=== COMPUTER TURN ===");
            computerPlay(COMPUTER);
            if (board.checkWin(COMPUTER)) {
                System.out.println(board.toString());
                System.out.println(board.promotedNum());
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

    private void computerPlay(char player) {
        int tossValue = toss();
        System.out.println("Computer toss: " + tossValue);

        // Get best move for this specific dice roll using expectiminimax
        Board bestMove = getBestMoveForRoll(board, player, tossValue);

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

        if (possibleMoves.isEmpty()) return null;

        Board bestBoard = null;
        double alpha = Double.NEGATIVE_INFINITY;

        for (Board move : possibleMoves) {
            // After Computer moves, we evaluate the resulting state via a CHANCE node
            double value = expectiminimax(move, MAX_DEPTH - 1, "MAX", diceRoll, player == 'W');
            if (value > alpha) {
                alpha = value;
                bestBoard = move;
            }
        }
        return bestBoard;
    }

    private double expectiminimax(Board node, int depth, String nodeType, int roll, boolean isWhite) {
        // 1. Terminal Node / Depth Reach
        if (depth == 0 || node.isFinal()) {
            // Heuristic always from Computer (White) perspective
            return SenetHeuristic.minimalHeuristic(node, isWhite);
        }

        // 2. Adversary is to play (MIN)
        if (nodeType.equals("MIN")) {
            double alpha = Double.POSITIVE_INFINITY;
            List<Board> children = node.generateNextStates(HUMAN, roll);

            if (children.isEmpty()) {
                Board skipped = node.deepCopy();
                skipped.applySkipTurn(HUMAN);
                return expectiminimax(skipped, depth - 1, "CHANCE", roll, isWhite);
            }

            for (Board child : children) {
                alpha = Math.min(alpha, expectiminimax(child, depth - 1, "CHANCE", roll, isWhite));
            }
            return alpha;
        }

        // 3. We are to play (MAX)
        else if (nodeType.equals("MAX")) {
            double alpha = Double.NEGATIVE_INFINITY;
            List<Board> children = node.generateNextStates(COMPUTER, roll);

            if (children.isEmpty()) {
                Board skipped = node.deepCopy();
                skipped.applySkipTurn(COMPUTER);
                return expectiminimax(skipped, depth - 1, "CHANCE", roll, isWhite);
            }

            for (Board child : children) {
                alpha = Math.max(alpha, expectiminimax(child, depth - 1, "CHANCE", roll, isWhite));
            }
            return alpha;
        }

        // 4. Random event (CHANCE)
        else { // nodeType.equals("CHANCE")
            double alpha = 0;
            for (int i = 0; i < ROLLS.length; i++) {
                // Determine who plays after this random toss
                // If the previous layer was MAX, the next layer (after chance) is MIN
                // We use depth - 1 here as the random event itself is a layer
                String nextType = (depth % 2 == 0) ? "MAX" : "MIN";

                // For Senet, we simplify: after computer moves, it's human's turn (MIN)
                // Since this CHANCE node is called after a move, the next player is the opponent.
                alpha += PROBABILITIES[i] * expectiminimax(node, depth - 1, "MIN", ROLLS[i], isWhite);
            }
            return alpha;
        }
    }
}