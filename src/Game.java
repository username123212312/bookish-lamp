import java.util.List;
import java.util.Scanner;

public class Game {
    private Board board;
    private final Scanner scanner;

    // Define pieces
    private final char COMPUTER = 'X';
    private final char HUMAN = 'O';

    public Game() {
        this.board = new Board();
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("Welcome to Connect Three (3x3)!");
        System.out.println("Align 3 pieces to win. Columns are 1, 2, 3.");
        System.out.println(board.toString());
        System.out.println("------------------------------------------------");

        while (!board.isFinal()) {
            // --- Human Turn ---
            humanPlay();
            if (board.checkWin(HUMAN)) {
                System.out.println(board.toString());
                System.out.println("You connected 3! You Win!");
                break;
            }
            if (board.isFull()) {
                System.out.println(board.toString());
                System.out.println("Draw!");
                break;
            }

            // --- Computer Turn ---
            System.out.println("\nComputer is thinking...");
            computerPlay();
            if (board.checkWin(COMPUTER)) {
                System.out.println(board.toString());
                System.out.println("Computer connected 3! You Lose!");
                break;
            }
            if (board.isFull()) {
                System.out.println(board.toString());
                System.out.println("Draw!");
                break;
            }
        }
    }

    private void humanPlay() {
        boolean validMove = false;
        while (!validMove) {
            System.out.print("Your move (Column 1-3): ");
            try {
                System.out.println("PLACEHOLDER");
            } catch (Exception e) {
                System.out.println("Invalid input.");
                scanner.nextLine();
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

    private MoveResult maxMove(Board currentBoard, int alpha , int beta) {
        if (currentBoard.isFinal()) {
            return new MoveResult(null, evaluate(currentBoard));
        }

        int bestScore = Integer.MIN_VALUE;
        Board bestBoard = null;

        for (Board nextState : currentBoard.generateNextStates(COMPUTER)) {
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

        for (Board nextState : currentBoard.generateNextStates(HUMAN)) {
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
        return 0;
    }
}