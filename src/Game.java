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

    public Game() {
        this.board = new Board();
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("Welcome to Senet (3x10)!");
        System.out.println("Game description");
        System.out.println(board.toString());
        System.out.println("------------------------------------------------");

        while (!board.isFinal()) {
            // --- Human Turn ---
            humanPlay();
            if (board.checkWin(HUMAN)) {
                System.out.println(board.toString());
                System.out.println("You Win!");
                break;
            }

//            // --- Computer Turn ---
//            System.out.println("\nComputer is thinking...");
//            computerPlay();
//            if (board.checkWin(COMPUTER)) {
//                System.out.println(board.toString());
//                System.out.println("Computer Win! You Lose!");
//                break;
//            }
        }
    }

    private int toss() {
        return tosses[new Random().nextInt(tosses.length)];
    }

    private void humanPlay() {
        boolean validMove = false;

        while (!validMove) {
            int tossValue = toss();
            System.out.println("Toss: " + tossValue);

            // Generate and display possible moves based on the toss
            List<Board> possibleMoves = board.generateNextStates(HUMAN, tossValue);

            if (possibleMoves.isEmpty()) {
                System.out.println("No valid moves available. Skipping turn.");
                return;
            }

            // Display possible moves with indices
            System.out.println("Available moves:");
            for (int i = 0; i < possibleMoves.size(); i++) {
                System.out.println("[" + i + "] " + possibleMoves.get(i).getAction() + "\n" + possibleMoves.get(i));
                System.out.println("------------------------------------------------");
            }

            System.out.print("Select a move (0-" + (possibleMoves.size() - 1) + "): ");

            // Validate input is integer
            while (!scanner.hasNextInt()) {
                System.out.println("That's not a valid integer! Try again:");
                scanner.next(); // Discard invalid input
            }

            int moveChoice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character

            try {
                // Validate the choice is within range
                if (moveChoice >= 0 && moveChoice < possibleMoves.size()) {
                    // Apply the selected move
                    board = possibleMoves.get(moveChoice);
                    System.out.println("Move applied successfully!");
                    validMove = true;
                } else {
                    System.out.println("Invalid choice. Please select a number between 0 and " +
                            (possibleMoves.size() - 1));
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                // Optionally: e.printStackTrace();
            }
        }
    }

    // --- MINIMAX ---

    private void computerPlay() {
        // Computer (Max) looks for the best move playing 'X'
        MoveResult result = maxMove(board, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (result.bestBoard() != null) {
            this.board = result.bestBoard();
        }
        System.out.println(board.toString());
    }

    private void expectiMinimax(Board currentBoard) {
    }

    private MoveResult maxMove(Board currentBoard, int alpha, int beta) {
        if (currentBoard.isFinal()) {
            return new MoveResult(null, evaluate(currentBoard));
        }

        int bestScore = Integer.MIN_VALUE;
        Board bestBoard = null;

        for (Board nextState : currentBoard.generateNextStates(COMPUTER, 0)) {
            int score = minMove(nextState, alpha, beta).score();

            if (score > bestScore) {
                bestScore = score;
                bestBoard = nextState;
            }

            alpha = Math.max(alpha, bestScore);
            if (alpha >= beta) {
                break;
            }
        }
        return new MoveResult(bestBoard, bestScore);
    }

    private MoveResult minMove(Board currentBoard, int alpha, int beta) {
        if (currentBoard.isFinal()) {
            return new MoveResult(null, evaluate(currentBoard));
        }

        int bestScore = Integer.MAX_VALUE;
        Board bestBoard = null;

        for (Board nextState : currentBoard.generateNextStates(HUMAN, 0)) {
            int score = maxMove(nextState, alpha, beta).score();

            if (score < bestScore) {
                bestScore = score;
                bestBoard = nextState;
            }

            beta = Math.min(beta, bestScore);
            if (alpha >= beta) {
                break;
            }
        }
        return new MoveResult(bestBoard, bestScore);
    }

    // Evaluation Function
    private int evaluate(Board b) {
        if (b.checkWin(COMPUTER)) return 1;
        if (b.checkWin(HUMAN)) return -1;
        return 0;
    }
}