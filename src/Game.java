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

        while (!board.isFinal()) {
            System.out.println(board.promotedNum());
            System.out.println("------------------------------------------------");
            System.out.println(board.toString());
            System.out.println("------------------------------------------------");
            // --- Human Turn ---
            humanPlay(HUMAN);
            if (board.checkWin(HUMAN)) {
                System.out.println(board.toString());
                System.out.println("You Win!");
                break;
            }

            humanPlay(COMPUTER);
            if (board.checkWin(COMPUTER)) {
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

    private void humanPlay(char player) {
        int tossValue = toss();
        System.out.println("Toss: " + tossValue);

        // Generate and display possible moves based on the toss
        List<Board> possibleMoves = board.generateNextStates(player, tossValue);

        if (possibleMoves.isEmpty()) {
            System.out.println("No valid moves available. Skipping turn.");
            board.applySkipTurn(player);
            return;
        }

        // Display possible moves with indices
        System.out.println("Available moves:");
        for (int i = 0; i < possibleMoves.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + possibleMoves.get(i).getAction() + "\n" + possibleMoves.get(i));
            System.out.println("------------------------------------------------");
        }

        boolean validMove = false;

        while (!validMove) {

            System.out.print("Select a move (1-" + (possibleMoves.size()) + "): ");

            // Validate input is integer
            while (!scanner.hasNextInt()) {
                System.out.println("That's not a valid integer! Try again:");
                scanner.next(); // Discard invalid input
            }

            int moveChoice = scanner.nextInt();
            scanner.nextLine(); // Clear the newline character

            try {
                // Validate the choice is within range
                if (moveChoice >= 1 && moveChoice < (possibleMoves.size() + 1)) {
                    // Apply the selected move
                    board = possibleMoves.get(moveChoice - 1);
                    System.out.println("Move applied successfully!");
                    validMove = true;
                }
                else {
                    if (possibleMoves.size() == 1) {
                        System.out.println("Invalid choice. There is only one valid move: 1");
                    }
                    else {
                        System.out.println("Invalid choice. Please select a number between 1 and " +
                                (possibleMoves.size()));
                    }
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
        if (result != null && result.bestBoard() != null) {
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