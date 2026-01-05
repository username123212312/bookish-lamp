import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Pawn[] list;
    private boolean[] whiteList;
    private boolean[] blackList;

    public Board() {
        list = new Pawn[31];
        for (int i = 0; i < 14; i = i + 2) {
            list[i] = new Pawn(true);
            list[i + 1] = new Pawn(false);
        }
    }

    // Private constructor for deep copy
    private Board(Pawn[] list) {
        this.list = list;
    }


    public List<Action> getPossibleActions(int numMoves) {
        return new ArrayList<>();
    }

    public Board applyAction(Action action) {
        return new Board();
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

    public Board deepCopy() {
        return new Board(this.list);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.length - 1; i++) {
            if (i == 10 || i == 20) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(" ");
            if (i < 20 && i > 9) {
                int index = 19 - (i - 10);
                if (index == 14) {
                    occupationPrint(stringBuilder, list[index], "R");
                    continue;
                }
                if (list[index] == null) {
                    stringBuilder.append("E");
                } else {
                    stringBuilder.append(list[index]);
                }
                stringBuilder.append("  ");
                continue;
            }
            if (i > 24) {
                occupationPrint(stringBuilder, list[i], getSymbol(i));
                continue;
            }
            if (list[i] == null) {
                stringBuilder.append("E");
            } else {
                stringBuilder.append(list[i]);
            }
            stringBuilder.append("  ");
        }

        return stringBuilder.toString();
    }

    private void occupationPrint(StringBuilder sb, Pawn pawn, String symbol) {
        sb.append(symbol).append(pawn == null ? "" : pawn)
                .append(pawn == null ? "  " : " ");
    }

    private String getSymbol(int index) {
        return switch (index) {
            case 25 -> "S";//   House of Happiness
            case 26 -> "W";//   House of Water
            case 27 -> "T";//   House of Three Truths
            case 28 -> "A";//   House of Re-Atoum
            case 29 -> "H";//   House of Horus
            default -> "";
        };
    }
}