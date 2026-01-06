import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Pawn[] list;
    private Action action;
    private List<Pawn> whitePawnList = new ArrayList<>();
    private List<Pawn> blackPawnList = new ArrayList<>();
    private List<String> whiteFinishList = new ArrayList<>();
    private List<String> blackFinishList = new ArrayList<>();

    public Board() {
        list = new Pawn[31];
        for (int i = 0; i < 14; i = i + 2) {
            list[i] = new Pawn(true, i);
            whitePawnList.add(list[i]);

            list[i + 1] = new Pawn(false, i + 1);
            blackPawnList.add(list[i + 1]);
        }
    }

    // Private constructor for deep copy
    private Board(Pawn[] list) {
        this.list = list;
    }

    private List<Action> getPossibleActions(char player, int numMoves) {
        List<Action> possibleActions = new ArrayList<>();
        for (Pawn pawn : player == 'W' ? whitePawnList : blackPawnList) {
            int newIndex = pawn.getIndex() + numMoves;
            Pawn nextSquare = list[newIndex];
            Action action;
            if (nextSquare != null && (nextSquare.isWhite() != pawn.isWhite())) {
                if (pawn.getIndex() < 25 && newIndex <= 25) {
                    action = new Action(pawn, newIndex);
                    possibleActions.add(action);
                } else if (pawn.getIndex() == 29) {
                    action = new Action(pawn, 30);
                    possibleActions.add(action);
                } else if (newIndex == 30) {
                    switch (pawn.getIndex()) {
                        case 27:
                        case 28:
                            action = new Action(pawn, newIndex);
                            possibleActions.add(action);
                    }
                } else if (pawn.getIndex() >= 25 && newIndex <= 29) {
                    action = new Action(pawn, newIndex);
                    possibleActions.add(action);
                }
            }
        }
        return possibleActions;
    }

    public void applyAction(Action action) {
        if (action.getNewIndex() == 30) {
            list[action.getPawn().getIndex()] = null;
            if (action.getPawn().isWhite()) {
                whiteFinishList.add("W");
            } else {
                blackFinishList.add("B");
            }
        } else if (action.getNewIndex() == 26) {
            returnToHouseOfReborn(action.getPawn());
        } else {
            replaceIfNotNull(action);
        }
    }

    public List<Board> generateNextStates(char player, int numMoves) {
        List<Board> nextStates = new ArrayList<>();
        List<Action> possibleActions = getPossibleActions(player, numMoves);
        for (Action possibleAction : possibleActions) {
            Board board = deepCopy();
            board.applyAction(possibleAction);
            board.setAction(possibleAction);
            nextStates.add(board);
        }
        return nextStates;
    }

    private Board deepCopy() {
        Pawn[] newList = new Pawn[31];
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null) {
                newList[i] = list[i].deepCopy();
            }
        }
        return new Board(newList);
    }

    public boolean isFinal() {
        return whiteFinishList.size() == 7 || blackFinishList.size() == 7;
    }

    public boolean checkWin(char p) {
        return p == 'W' ? whiteFinishList.size() == 7 : blackFinishList.size() == 7;
    }


    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }


    private void returnToHouseOfReborn(Pawn pawn) {
        if (list[14] == null) {
            list[pawn.getIndex()] = null;
            pawn.setIndex(14);
            list[14] = pawn;
        } else {
            for (int i = 13; i > 0; i--) {
                if (list[i] == null) {
                    list[pawn.getIndex()] = null;
                    pawn.setIndex(i);
                    list[i] = pawn;
                }
            }
        }
    }

    private void replaceIfNotNull(Action action) {
        int toBeReplacedIndex = action.getNewIndex();
        Pawn toBeReplaced = list[toBeReplacedIndex].deepCopy();
        int replacementIndex = action.getPawn().getIndex();
        Pawn replacement = action.getPawn().deepCopy();
        if (toBeReplaced != null) {
            replacement.setIndex(toBeReplacedIndex);
            list[toBeReplacedIndex] = replacement;
            toBeReplaced.setIndex(replacementIndex);
            list[replacementIndex] = toBeReplaced;
        } else {
            replacement.setIndex(toBeReplacedIndex);
            list[toBeReplacedIndex] = replacement;
        }
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
            case 26 -> "M";//   House of Water
            case 27 -> "T";//   House of Three Truths
            case 28 -> "A";//   House of Re-Atoum
            case 29 -> "H";//   House of Horus
            default -> "";
        };
    }
}