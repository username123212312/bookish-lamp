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

            // --- Computer Turn ---
            System.out.println("\nComputer is thinking...");
            computerPlay();
            if (board.checkWin(COMPUTER)) {
                System.out.println(board.toString());
                System.out.println("Computer Win! You Lose!");
                break;
            }
        }
    }

    private int toss(){
        return tosses[new Random().nextInt(tosses.length)];
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

    private void expectiMinimax(Board currentBoard) {
    }

    private MoveResult maxMove(Board currentBoard, int alpha, int beta) {
        if (currentBoard.isFinal()) {
            return new MoveResult(null, evaluate(currentBoard));
        }

        int bestScore = Integer.MIN_VALUE;
        Board bestBoard = null;

        for (Board nextState : currentBoard.generateNextStates(COMPUTER,0)) {
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

        for (Board nextState : currentBoard.generateNextStates(HUMAN,0)) {
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