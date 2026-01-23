// Game.java - Complete with Expectiminimax

import java.util.*;

public class Game {
    private final static int[] tosses = {1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 5};
    private Board board;
    private final Scanner scanner;
    private final List<Integer> blackVisitedNodes = new ArrayList<>();
    private final List<Integer> whiteVisitedNodes = new ArrayList<>();
    private boolean isDetailed = true;

    // Define pieces
    private final char COMPUTER = 'W';
    private final char HUMAN = 'B';

    // Constants for expectiminimax
    private int MAX_DEPTH = 5;
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
        isDetailed = promptForBoolean("Do you want to print details?");
        MAX_DEPTH = promptForDepth();

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
        Map<Double, Board> bestMove = getBestMoveForRoll(board, player, tossValue);
        if (bestMove != null) {
            Double evaluationValue = (Double) (bestMove.keySet().toArray())[0];
            Board bestMoveBoard = bestMove.get(evaluationValue);
            board = bestMoveBoard;
            System.out.println("Computer plays: " + bestMoveBoard.getAction());
            if (isDetailed) {
                System.out.println("Evaluation value : " + evaluationValue);
            }
        } else {
            System.out.println("No valid moves. Computer skips turn.");
            board.applySkipTurn(player);
        }
        if (isDetailed) {
            System.out.println("Visited nodes so far : " + (player == 'W' ? whiteVisitedNodes.size()
                    : blackVisitedNodes.size()));
        }

    }

    private Map<Double, Board> getBestMoveForRoll(Board currentBoard, char player, int diceRoll) {
        List<Board> possibleMoves = currentBoard.generateNextStates(player, diceRoll);
        Map<Double, Board> map = new HashMap<>();

        if (possibleMoves.isEmpty()) return null;

        Board bestBoard = null;
        double alpha = player == 'W' ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

        for (Board move : possibleMoves) {
            addVisitedNodes(player == 'W');

            // After Computer moves, we evaluate the resulting state via a CHANCE node
            double value = expectiminimax(move, MAX_DEPTH, player == 'W' ? "MAX" : "MIN",
                    player == 'W' ? "MAX" : "MIN",
                    diceRoll, player == 'W');

            if (player == 'W') {
                if (value > alpha) {
                    alpha = value;
                    bestBoard = move;
                }
            } else {
                if (value < alpha) {
                    alpha = value;
                    bestBoard = move;
                }
            }
        }
        map.put(alpha, bestBoard);
        return map;
    }

    private double expectiminimax(Board node, int depth, String preType,
                                  String nodeType, int roll, boolean isWhite) {
        logAlgorithmDetail("Entering " + nodeType + " node | Depth: " + depth +
                " | Roll: " + roll + " | Player: " + (isWhite ? "White" : "Black"));

        // 1. Terminal Node / Depth Reach
        if (depth == 0 || node.isFinal()) {
            double heuristicValue = SenetHeuristic.minimalHeuristic(node, isWhite);
            logAlgorithmDetail("TERMINAL NODE | Heuristic value: " + heuristicValue +
                    " | Returning: " + heuristicValue);
            return heuristicValue;
        }

        // 2. Adversary is to play (MIN)
        if (nodeType.equals("MIN")) {
            double alpha = Double.POSITIVE_INFINITY;
            List<Board> children = node.generateNextStates(HUMAN, roll);
            logAlgorithmDetail("MIN NODE | Evaluating " + children.size() + " children");

            if (children.isEmpty()) {
                Board skipped = node.deepCopy();
                skipped.applySkipTurn(HUMAN);
                logAlgorithmDetail("MIN NODE | No moves, skipping turn");
                double result = expectiminimax(skipped, depth - 1, "MIN", "CHANCE", roll, isWhite);
                logAlgorithmDetail("MIN NODE | Final value after skip: " + result);
                return result;
            }
            int childNum = 1;
            for (Board child : children) {
                addVisitedNodes(isWhite);
                logAlgorithmDetail("MIN NODE | Child " + childNum + "/" + children.size() +
                        " | Action: " + child.getAction());
                double childValue = expectiminimax(child, depth - 1, "MIN", "CHANCE", roll, isWhite);
                logAlgorithmDetail("MIN NODE | Child " + childNum + " value: " + childValue +
                        " | Current alpha: " + alpha);

                if (childValue < alpha) {
                    alpha = childValue;
                    logAlgorithmDetail("MIN NODE | Updated alpha to: " + alpha);
                }
                childNum++;
            }
            logAlgorithmDetail("MIN NODE | Returning final alpha: " + alpha);
            return alpha;
        }

        // 3. We are to play (MAX)
        else if (nodeType.equals("MAX")) {
            double alpha = Double.NEGATIVE_INFINITY;
            List<Board> children = node.generateNextStates(COMPUTER, roll);
            logAlgorithmDetail("MAX NODE | Evaluating " + children.size() + " children");

            if (children.isEmpty()) {
                Board skipped = node.deepCopy();
                skipped.applySkipTurn(COMPUTER);
                logAlgorithmDetail("MAX NODE | No moves, skipping turn");
                double result = expectiminimax(skipped, depth - 1, "MAX", "CHANCE", roll, isWhite);
                logAlgorithmDetail("MAX NODE | Final value after skip: " + result);
                return result;
            }
            int childNum = 1;
            for (Board child : children) {
                addVisitedNodes(isWhite);
                logAlgorithmDetail("MAX NODE | Child " + childNum + "/" + children.size() +
                        " | Action: " + child.getAction());
                double childValue = expectiminimax(child, depth - 1, "MAX", "CHANCE", roll, isWhite);
                logAlgorithmDetail("MAX NODE | Child " + childNum + " value: " + childValue +
                        " | Current alpha: " + alpha);

                if (childValue > alpha) {
                    alpha = childValue;
                    logAlgorithmDetail("MAX NODE | Updated alpha to: " + alpha);
                }
                childNum++;
            }
            logAlgorithmDetail("MAX NODE | Returning final alpha: " + alpha);
            return alpha;
        }

        // 4. Random event (CHANCE)
        else { // nodeType.equals("CHANCE")
            logAlgorithmDetail("CHANCE NODE | Evaluating 5 possible dice rolls with probabilities");
            double expectedValue = 0;

            for (int i = 0; i < ROLLS.length; i++) {
                String nextType = preType.equals("MAX") ? "MIN" : "MAX";

                logAlgorithmDetail("CHANCE NODE | Roll " + ROLLS[i] + " (prob: " +
                        String.format("%.2f", PROBABILITIES[i]) + ") | Next player: " + nextType);

                double rollValue = expectiminimax(node, depth - 1, preType, nextType, ROLLS[i], isWhite);
                double weightedValue = PROBABILITIES[i] * rollValue;

                logAlgorithmDetail("CHANCE NODE | Roll " + ROLLS[i] + " value: " + rollValue +
                        " | Weighted: " + String.format("%.4f", weightedValue));

                expectedValue += weightedValue;
                logAlgorithmDetail("CHANCE NODE | Cumulative expected value: " +
                        String.format("%.4f", expectedValue));
            }
            logAlgorithmDetail("CHANCE NODE | Returning expected value: " +
                    String.format("%.4f", expectedValue));
            return expectedValue;
        }
    }

    private void addVisitedNodes(boolean isWhite) {
        if (isWhite) {
            whiteVisitedNodes.add(0);
        } else {
            blackVisitedNodes.add(0);
        }
    }

    private boolean promptForBoolean(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("yes") || input.equals("y")) {
                return true;
            } else if (input.equals("no") || input.equals("n")) {
                return false;
            } else {
                System.out.println("Please enter 'yes' or 'no'.");
            }
        }
    }

    private int promptForDepth() {
        final int MIN_DEPTH = 3;
        final int DEFAULT_DEPTH = 5;
        final int MAX_SUGGESTED_DEPTH = 10; // You can adjust this

        while (true) {
            System.out.print("Enter search depth for the algorithm (minimum " + MIN_DEPTH +
                    ", default " + DEFAULT_DEPTH + ", suggested max " + MAX_SUGGESTED_DEPTH + "): ");

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Using default depth: " + DEFAULT_DEPTH);
                return DEFAULT_DEPTH;
            }

            try {
                int depth = Integer.parseInt(input);

                if (depth < MIN_DEPTH) {
                    System.out.println("Depth must be at least " + MIN_DEPTH + ". Please try again.");
                } else if (depth > MAX_SUGGESTED_DEPTH) {
                    System.out.println("Warning: Depth " + depth + " may cause performance issues.");
                    boolean confirm = promptForBoolean("Are you sure you want to use depth " + depth + "?");
                    if (confirm) {
                        return depth;
                    }
                } else {
                    return depth;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }
    }

    private void logAlgorithmDetail(String message) {
        if (isDetailed) {
            System.out.println("[ALGORITHM] " + message);
        }
    }
}