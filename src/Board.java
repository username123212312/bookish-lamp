import java.util.*;

public class Board {
    private final Pawn[] list;
    private Action action;
    private Map<Integer, Pawn> whitePawnMap = new HashMap<>();
    private Map<Integer, Pawn> blackPawnMap = new HashMap<>();
    private List<String> whiteFinishList = new ArrayList<>();
    private List<String> blackFinishList = new ArrayList<>();

    public Board() {
        list = new Pawn[31];
        for (int i = 0; i < 14; i = i + 2) {
            list[i] = new Pawn(true, i);
            whitePawnMap.put(i, list[i]);

            list[i + 1] = new Pawn(false, i + 1);
            blackPawnMap.put(i + 1, list[i + 1]);
        }
    }

    // Private constructor for deep copy
    private Board(Pawn[] list, Map<Integer, Pawn> whitePawnMap,
                  Map<Integer, Pawn> blackPawnMap,
                  List<String> whiteFinishList,
                  List<String> blackFinishList) {
        this.list = list;
        this.blackPawnMap = blackPawnMap;
        this.blackFinishList = blackFinishList;
        this.whitePawnMap = whitePawnMap;
        this.whiteFinishList = whiteFinishList;
    }

    private List<Action> getPossibleActions(char player, int numMoves) {
        List<Action> possibleActions = new ArrayList<>();
        for (Pawn pawn : player == 'W' ? whitePawnMap.values() : blackPawnMap.values()) {
            int currentIndex = pawn.getIndex() ;
            int newIndex = currentIndex + numMoves;
            Action action = null;
            if(currentIndex < 25 && newIndex > 25)
            {
               continue ;
            }
            if(currentIndex == 25)
            {
                if(numMoves == 5) {
                    action = new Action(pawn, 30);
                }

                else if (newIndex <= 29) {
                    Pawn nextSquare = list[newIndex];
                    if (nextSquare == null || (nextSquare.isWhite() != pawn.isWhite())) {
                        if (!( (newIndex == 25 || newIndex == 27 || newIndex == 28) && nextSquare != null )) {
                            action = new Action(pawn, newIndex);
                        }
                    }
                }
            }

            else if (currentIndex == 27) {
                if (numMoves == 3) {
                    action = new Action(pawn, 30);

                }
            }
            else if (currentIndex == 28) {
                if (numMoves == 2) {
                    action = new Action(pawn, 30);

                }
            }
            else if (currentIndex == 29) {
               action = new Action(pawn, 30);

            }
            else if (newIndex <= 29) {
                Pawn nextSquare = list[newIndex];
                if (nextSquare == null || (nextSquare.isWhite() != pawn.isWhite())) {
                    boolean isSpecialSquare = (newIndex == 25 || newIndex == 27 || newIndex == 28);
                    if (isSpecialSquare && nextSquare != null) {
                        continue;
                    }
                    action = new Action(pawn, newIndex);
                }
            }
            if (action != null) {
                    possibleActions.add(action);
               }
        }
        return possibleActions;
    }

    public void applyAction(Action action) {
        if (action.newIndex() == 30) {
            list[action.pawn().getIndex()] = null;
            if (action.pawn().isWhite()) {
                whitePawnMap.remove(action.pawn().getIndex());
                whiteFinishList.add("W");
            } else {
                blackPawnMap.remove(action.pawn().getIndex());
                blackFinishList.add("B");
            }
        } else if (action.newIndex() == 26) {
            returnToHouseOfReborn(action.pawn());
        } else {
            replaceIfNotNull(action);
        }
        checkAndApplyPenalties(action.pawn());
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
        List<String> whiteFinishList = new ArrayList<>(this.whiteFinishList);
        List<String> blackFinishList = new ArrayList<>(this.blackFinishList);
        Map<Integer, Pawn> whitePawnMap = new HashMap<>();
        Map<Integer, Pawn> blackPawnMap = new HashMap<>();
        for (Integer key : this.blackPawnMap.keySet()) {
            blackPawnMap.put(key, this.blackPawnMap.get(key).deepCopy());
        }
        for (Integer key : this.whitePawnMap.keySet()) {
            whitePawnMap.put(key, this.whitePawnMap.get(key).deepCopy());
        }

        return new Board(newList, whitePawnMap, blackPawnMap, whiteFinishList, blackFinishList);
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
        int oldIndex = pawn.getIndex();
        if (list[14] == null) {
            list[oldIndex] = null;

            pawn.setIndex(14);
            changeIndexesInMaps(pawn, oldIndex, 14);
            list[14] = pawn;
        } else {
            for (int i = 13; i > 0; i--) {
                if (list[i] == null) {
                    list[oldIndex] = null;
                    pawn.setIndex(i);
                    changeIndexesInMaps(pawn, oldIndex, i);
                    list[i] = pawn;
                    break;
                }
            }
        }
    }

    private void replaceIfNotNull(Action action) {
        int toBeReplacedIndex = action.newIndex();
        Pawn toBeReplaced = list[toBeReplacedIndex] == null ? null : list[toBeReplacedIndex].deepCopy();

        int replacementIndex = action.pawn().getIndex();
        Pawn replacement = action.pawn().deepCopy();

        replacement.setIndex(toBeReplacedIndex);
        changeIndexesInMaps(replacement, replacementIndex, toBeReplacedIndex);
        list[toBeReplacedIndex] = replacement;

        if (toBeReplaced != null) {
            if (toBeReplacedIndex > 26 && toBeReplacedIndex < 30) {
                returnToHouseOfReborn(toBeReplaced);
            } else {
                toBeReplaced.setIndex(replacementIndex);
                changeIndexesInMaps(toBeReplaced, toBeReplacedIndex, replacementIndex);
                list[replacementIndex] = toBeReplaced;
                }

        } else {
            list[replacementIndex] = null;
        }
    }

    private void changeIndexesInMaps(Pawn pawn, int oldIndex, int newIndex) {
        if (pawn.isWhite()) {
            whitePawnMap.remove(oldIndex);
            whitePawnMap.put(newIndex, pawn);
        } else {
            blackPawnMap.remove(oldIndex);
            blackPawnMap.put(newIndex, pawn);
        }
    }
    //penalized Pawns
    private void checkAndApplyPenalties(Pawn movedpawn) {
       Map <Integer,Pawn> currentMap = movedpawn.isWhite() ? whitePawnMap : blackPawnMap;

       List<Pawn> penalizedPawns = new ArrayList<>();
       for (Pawn p : currentMap.values()) {
           int position =  p.getIndex();
           if((position == 27 || position == 28 || position == 29) && p != movedpawn ) {
               penalizedPawns.add(p);
           }
       }
       for (Pawn p : penalizedPawns) {
           returnToHouseOfReborn(p);
       }
    }

    // skip if no possible action and apply penalized Pawns
    public void applySkipTurn(char player)
    {
        Map<Integer, Pawn> currentMap = (player == 'W') ? whitePawnMap : blackPawnMap;
        List<Pawn> penalizedPawns = new ArrayList<>();
        for (Pawn p : currentMap.values()) {
            int pos = p.getIndex();
            if (pos == 27 || pos == 28 || pos == 29) {
                penalizedPawns.add(p);
            }
        }
        for (Pawn p : penalizedPawns) {
            returnToHouseOfReborn(p);
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