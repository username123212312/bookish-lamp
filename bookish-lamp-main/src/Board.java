import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board {
    // 3x3 Grid. Empty=' ', Computer='X', Human='O'
    private char[][] grid;
    private final int SIZE = 3;

    public Board() {
        grid = new char[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            Arrays.fill(grid[r], ' ');
        }
    }

    // Private constructor for deep copy
    private Board(char[][] grid) {
        this.grid = new char[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            this.grid[r] = Arrays.copyOf(grid[r], SIZE);
        }
    }


    public boolean play(int col, char player) {
        return true;
    }

    public List<Board> generateNextStates(char player) {
        return new ArrayList<>();
    }

    public boolean isFinal() {
        return true;
    }

    public boolean checkWin(char p) {
        return true;
    }

    public boolean isFull() {
        return true;
    }

    public Board deepCopy() {
        return new Board(this.grid);
    }

    @Override
    public String toString() {
        return "";
    }
}