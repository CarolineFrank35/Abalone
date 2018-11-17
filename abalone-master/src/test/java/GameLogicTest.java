import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.logic.Context;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard.Owner;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Logger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Functional interface to enable the use of anonymous lambda expressions
 */
interface Direction {

    Vector[] directions = {
            new Vector(0, -1), new Vector(1, -1), new Vector(1, 0),//NW, NE, E
            new Vector(0, 1), new Vector(-1, 1), new Vector(-1, 0) //SE, SW, W
    };

    static List<Vector> getNeighbors(Vector v) {
        List<Vector> neighbors = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            neighbors.add(v.addVector(directions[i]));
        }
        return neighbors;
    }

    Vector go(Vector c);

}

class GameLogicTest {
    private static final Owner activePlayer = Owner.PLAYER_BLACK;
    private static AbaloneBoard BOARD;
    private static List<Vector> selectedSpaces = new ArrayList<>();
    private static List<Vector> movesAvailable = new ArrayList<>();
    private static List<Vector> selectable = new ArrayList<>();
    private static List<Vector> validMoves = new ArrayList<>();
    private static List<Vector> validClicks = new ArrayList<>();
    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    private List<Vector> translateDirectionsToVectors(List<Vector.Direction> validMoves,
                                                      List<Vector> selected) {
        List<Vector> out = new LinkedList<>();

        for (Vector selection : selected) {
            for (Vector.Direction direction : validMoves) {
                if (!selected.contains(selection.go(direction)) && !out.contains(selection.go(direction))) {
                    out.add(selection.go(direction));
                }
            }
        }
        return out;
    }

    @BeforeEach
    void setUp() {
        int size = 9;
        BOARD = new AbaloneBoard(size);
        selectedSpaces.clear();
    }

    @Test
    @DisplayName("One selected pebble")
    void case1() {

        for (Vector v : BOARD.getBoard().keySet()) {
            if (BOARD.getOwner(v).equals(Owner.PLAYER_BLACK)) {
                selectedSpaces.clear();
                selectedSpaces.add(v);
                moveAt(selectedSpaces);
                validClicks = Context.getValidClicks(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK);
                validMoves = translateDirectionsToVectors(Context.getValidMoves(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK), selectedSpaces);

                StringBuilder sb = new StringBuilder();
                for (Vector k : selectedSpaces) {
                    sb.append(k);
                }
                LOGGER.info("SELECTED = " + sb.toString());
                sb = new StringBuilder();
                for (Vector a : selectable) {
                    sb.append(a);
                }
                LOGGER.info("SELECTABLE = " + sb.toString());
                sb = new StringBuilder();
                for (Vector d : movesAvailable) {
                    sb.append(d);
                }
                LOGGER.info("MOVES = " + sb.toString());
                sb = new StringBuilder();
                for (Vector k : validClicks) {
                    sb.append(k);
                }
                LOGGER.info("JONAS SELECTABLE = " + sb.toString());
                sb = new StringBuilder();
                for (Vector d : validMoves) {
                    sb.append(d);
                }
                LOGGER.info("JONAS MOVES = " + sb.toString());
                assertTrue(selectable.containsAll(validClicks));
                assertThat(validMoves).isEqualTo(movesAvailable);
            }
        }
    }

    @Test
    @DisplayName("Two selected pebble")
    void case2() {
        for (Vector v1 : BOARD.getBoard().keySet()) {
            if (BOARD.getOwner(v1).equals(Owner.PLAYER_BLACK)) {
                selectedSpaces.clear();
                selectedSpaces.add(v1);
                validClicks = Context.getValidClicks(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK);
                for (Vector v2 : validClicks) {
                    selectedSpaces.clear();
                    selectedSpaces.add(v1);
                    selectedSpaces.add(v2);
                    validClicks = Context.getValidClicks(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK);
                    validMoves = translateDirectionsToVectors(Context.getValidMoves(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK), selectedSpaces);
                    moveAt(selectedSpaces);

                    StringBuilder sb = new StringBuilder();
                    for (Vector k : selectedSpaces) {
                        sb.append(k);
                    }
                    LOGGER.info("SELECTED = " + sb.toString());
                    sb = new StringBuilder();
                    for (Vector v : selectable) {
                        sb.append(v);
                    }
                    LOGGER.info("SELECTABLE = " + sb.toString());
                    sb = new StringBuilder();
                    for (Vector d : movesAvailable) {
                        sb.append(d);
                    }
                    LOGGER.info("MOVES = " + sb.toString());
                    sb = new StringBuilder();
                    for (Vector k : validClicks) {
                        sb.append(k);
                    }
                    LOGGER.info("JONAS SELECTABLE = " + sb.toString());
                    sb = new StringBuilder();
                    for (Vector d : validMoves) {
                        sb.append(d);
                    }
                    LOGGER.info("JONAS MOVES = " + sb.toString());
                    assertTrue(selectable.containsAll(validClicks));
                    assertTrue(validMoves.containsAll(movesAvailable));
                }
            }
        }
    }

    @Test
    @DisplayName("Three selected pebble")
    void case3() {
        for (Vector v1 : BOARD.getBoard().keySet()) {
            if (BOARD.getOwner(v1).equals(Owner.PLAYER_BLACK)) {
                selectedSpaces.clear();
                selectedSpaces.add(v1);

                validClicks = Context.getValidClicks(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK);
                for (Vector v2 : validClicks) {
                    selectedSpaces.clear();
                    selectedSpaces.add(v1);
                    selectedSpaces.add(v2);

                    validClicks = Context.getValidClicks(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK);

                    for (Vector v3 : validClicks) {
                        selectedSpaces.clear();
                        selectedSpaces.add(v1);
                        selectedSpaces.add(v2);
                        selectedSpaces.add(v3);
                        validClicks = Context.getValidClicks(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK);
                        validMoves = translateDirectionsToVectors(Context.getValidMoves(BOARD.getBoard(), selectedSpaces, Owner.PLAYER_BLACK), selectedSpaces);
                        moveAt(selectedSpaces);
                        StringBuilder sb = new StringBuilder();
                        for (Vector k : selectedSpaces) {
                            sb.append(k);
                        }
                        LOGGER.info("SELECTED = " + sb.toString());
                        sb = new StringBuilder();
                        for (Vector v : selectable) {
                            sb.append(v);
                        }
                        LOGGER.info("SELECTABLE = " + sb.toString());
                        sb = new StringBuilder();
                        for (Vector d : movesAvailable) {
                            sb.append(d);
                        }
                        LOGGER.info("MOVES = " + sb.toString());
                        sb = new StringBuilder();
                        for (Vector k : validClicks) {
                            sb.append(k);
                        }
                        LOGGER.info("JONAS SELECTABLE = " + sb.toString());
                        sb = new StringBuilder();
                        for (Vector d : validMoves) {
                            sb.append(d);
                        }
                        LOGGER.info("JONAS MOVES = " + sb.toString());
                        assertTrue(selectable.containsAll(validClicks));
                        assertTrue(validMoves.containsAll(movesAvailable));
                    }
                }
            }
        }
    }

    private void moveAt(final List<Vector> selected) {
        selectedSpaces = selected;

        if (selected.stream().anyMatch(v -> movesAvailable.contains(v))) {
            doMove(selected);
        } else {
            selectable = updateSelectable();
            movesAvailable = updateMovesAvailable();
        }
    }

    private List<Vector> updateSelectable() {

        int size = selectedSpaces.size();

        List<Vector> selectable = initSelectable();

        switch (size) {
            case 0: // OPTION 1: None selected -> add all player cells
                return selectable;
            case 1: // OPTION 2: one selected -> Check neighbors
                return ContextCheck.getSelectableNeighbors(selectable, selectedSpaces);
            case 2: // OPTION 3: two selected -> find third selectable
                return ContextCheck.getLastSelectable(selectedSpaces,
                        activePlayer, BOARD);
            case 3: // OPTION 4: three selected -> max selectable
                return new ArrayList<>();
            default:
                return selectable;
        }
    }

    private List<Vector> updateMovesAvailable() {
        List<Vector> movesToAdd = new ArrayList<>();
        List<Vector> notOwner = new ArrayList<>();
        for (Map.Entry<Vector, Owner> v : BOARD.getBoard().entrySet()) {
            Vector k = v.getKey();
            if (!selectable.contains(k)) {
                notOwner.add(k);
            }
        }
        int numberSelected = selectedSpaces.size();

        switch (numberSelected) {
            case 0:
                break;
            case 1:
                movesToAdd = ContextCheck.getSelectableNeighbors(notOwner,
                        selectedSpaces);
                break;
            case 2:
                movesToAdd.addAll(ContextCheck.checkInLine(selectedSpaces, BOARD));
                movesToAdd.addAll(ContextCheck.checkSideStep(selectedSpaces, BOARD));
                movesToAdd.addAll(ContextCheck.checkTwoOneSumito(selectedSpaces, activePlayer, BOARD));
                break;
            case 3:
                movesToAdd.addAll(ContextCheck.checkInLine(selectedSpaces, BOARD));
                movesToAdd.addAll(ContextCheck.checkSideStep(selectedSpaces, BOARD));
                movesToAdd.addAll(ContextCheck.checkThreeOneSumito(selectedSpaces, activePlayer, BOARD));
                movesToAdd.addAll(ContextCheck.checkThreeTwoSumito(selectedSpaces, activePlayer, BOARD));
                break;
            default:
                break;
        }
        return movesToAdd;
    }

    private List<Vector> initSelectable() {

        List<Vector> addSelectable = new ArrayList<>();

        for (Entry<Vector, Owner> c : BOARD.getBoard().entrySet()) {
            if (c.getValue() == activePlayer) {
                addSelectable.add(c.getKey());
            }
        }
        return addSelectable;
    }

    private void doMove(List<Vector> moving) {
        //list already sorted by view NW -> SE
        int size = moving.size();
        Vector destination = null;
        for (Vector v : moving) {
            if (BOARD.getOwner(v) != activePlayer) {
                destination = v;
            }
        }
        Vector closest = null;
        Vector start = null;
        int max = 0;
        for (Vector v : moving) {
            int cur = new Vectors(v).getDistance(destination);
            if (new Vectors(v).getDistance(destination) == 1) {
                closest = v;
            }
            if (max < cur) {
                max = cur;
                start = v;
            }
        }
        Vector direction = null;
        if (destination != null) {
            direction = new Vectors(destination).subtractVector(closest);
        }
        //if destination owner != empty => Sumito move
        if ((size == 2 || ContextCheck.inLine(moving)) && direction != null) {
            Owner changeTo = Owner.EMPTY;
            Owner currentOwner = null;
            while (changeTo != null && currentOwner != Owner.EMPTY) {
                currentOwner = BOARD.getOwner(start);
                BOARD.setOwner(start, changeTo);
                Assertions.assertNotNull(start);
                start = start.addVector(direction);
                changeTo = currentOwner;
            }
        } else { // Side Step
            for (Vector v : moving) {
                if (v != destination) {
                    BOARD.setOwner(v, Owner.EMPTY);
                    Vector newVector = v.addVector(direction);
                    BOARD.setOwner(newVector, activePlayer);
                }
            }
        }
    }
}

class ContextCheck {

    //Suppresses default constructor, ensuring non instantiability
    private ContextCheck() {
    }

    static List<Vector> getSelectableNeighbors(List<Vector> clickableSpaces, List<Vector> selected) {
        Vector selectedVector = selected.get(0);
        List<Vector> newSelectable = new ArrayList<>();

        for (Vector v : Direction.getNeighbors(selectedVector)) {
            if (clickableSpaces.contains(v) && !selected.contains(v)) {
                newSelectable.add(v);
            }
        }
        return newSelectable;
    }

    static List<Vector> getLastSelectable(List<Vector> selectedSpaces, Owner player, AbaloneBoard board) {
        List<Vector> any = new ArrayList<>();
        Vector move1 = selectedSpaces.get(0);
        Vector move2 = selectedSpaces.get(1);
        Vector direction = new Vectors(move1).subtractVector(move2);
        for (Vector v : selectedSpaces) {
            Vector dir = v.addVector(direction);
            if (!selectedSpaces.contains(dir) && board.hasPosition(dir) && board.getOwner(dir) == player) {
                any.add(dir);
            }
            Vector opp = new Vectors(v).subtractVector(direction);
            if (!selectedSpaces.contains(opp) && board.hasPosition(opp) && board.getOwner(opp) == player) {
                any.add(opp);
            }
        }
        return any;
    }

    //done
    static List<Vector> checkInLine(List<Vector> selected, AbaloneBoard board) {
        List<Vector> any = new ArrayList<>();
        Vector move1 = selected.get(0);
        Vector move2 = selected.get(1);
        Vector direction = new Vectors(move1).subtractVector(move2);
        Vector next = new Vectors(selected.get(selected.size() - 1)).goDirection(direction);
        if (board.getOwner(next) == Owner.EMPTY) {
            any.add(next);
        }
        Vector other = new Vectors(move1).goDirection(new Vectors(direction).opposite());
        if (board.getOwner(other) == Owner.EMPTY) {
            any.add(other);
        }
        return any;
    }

    //done
    static List<Vector> checkSideStep(List<Vector> selected, AbaloneBoard board) {
        Set<Vector> any = new HashSet<>();

        Vector move1 = selected.get(0);
        Vector move2 = selected.get(1);

        Vector direction = new Vectors(move1).subtractVector(move2);

        if (direction.getX() == 0) {
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).west(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).east(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(new Vectors(v).north()).east(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(new Vectors(v).south()).west(), board));
        } else if (direction.getY() == 0) {
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).north(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).south(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(new Vectors(v).north()).east(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(new Vectors(v).south()).west(), board));
        } else if (direction.getZ() == 0) {
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).west(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).east(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).north(), board));
            any.addAll(sideStepAvailable(selected, v -> new Vectors(v).south(), board));
        }
        return new ArrayList<>(any);
    }

    private static List<Vector> sideStepAvailable(List<Vector> list, Direction d, AbaloneBoard board) {
        List<Vector> toAdd = new ArrayList<>();
        if (list.stream().allMatch(v -> board.getOwner(d.go(v)) == Owner.EMPTY)) {
            list.stream().map(d::go).forEach(toAdd::add);
        }
        return toAdd;
    }

    static List<Vector> checkTwoOneSumito(final List<Vector> selected, final Owner player, AbaloneBoard board) {
        List<Vector> any = new ArrayList<>();
        Vector move1 = selected.get(0);
        Vector move2 = selected.get(1);
        Vector direction = new Vectors(move1).subtractVector(move2);
        if (sumitoHelper(move2, 1, direction, player, board)) {
            any.add(new Vectors(move2).goDirection(direction));
        }
        if (sumitoHelper(move1, 1, new Vectors(direction).opposite(), player, board)) {
            any.add(new Vectors(move1).goDirection(new Vectors(direction).opposite()));
        }
        return any;
    }

    static List<Vector> checkThreeOneSumito(List<Vector> selected, Owner player, AbaloneBoard board) {
        List<Vector> any = new ArrayList<>();
        Vector move1 = selected.get(0);
        Vector move2 = selected.get(1);
        Vector move3 = selected.get(2);
        Vector direction = new Vectors(move1).subtractVector(move2);
        if (sumitoHelper(move3, 1, direction, player, board)) {
            any.add(new Vectors(move3).goDirection(direction));
        }
        if (sumitoHelper(move1, 1, new Vectors(direction).opposite(), player, board)) {
            any.add(new Vectors(move1).goDirection(new Vectors(direction).opposite()));
        }
        return any;
    }

    static List<Vector> checkThreeTwoSumito(List<Vector> selected, Owner player, AbaloneBoard board) {
        List<Vector> any = new ArrayList<>();
        Vector move1 = selected.get(0);
        Vector move2 = selected.get(1);
        Vector move3 = selected.get(2);
        Vector direction = new Vectors(move1).subtractVector(move2);
        if (sumitoHelper(move3, 2, direction, player, board)) {
            any.add(new Vectors(move3).goDirection(direction));
        }
        if (sumitoHelper(move1, 2, new Vectors(direction).opposite(), player, board)) {
            any.add(new Vectors(move1).goDirection(new Vectors(direction).opposite()));
        }
        return any;
    }

    private static boolean sumitoHelper(final Vector current, int numOpp, final Vector dir, final Owner player, final AbaloneBoard board) {
        Owner opponent = getOpponent(player);
        Vector next = current;

        int oppCells = numOpp;
        while (oppCells != 0) {
            oppCells--;
            next = new Vectors(next).goDirection(dir);
            Owner ownerOf = board.getOwner(next);
            if (ownerOf != opponent) {
                return false;
            }
        }
        Vector after = new Vectors(next).goDirection(dir);
        Owner ownerAfter = board.getOwner(after);
        return ownerAfter == null || ownerAfter == Owner.EMPTY;
    }

    private static Owner getOpponent(Owner current) {
        Owner opponent;
        if (current == Owner.PLAYER_BLACK) {
            opponent = Owner.PLAYER_WHITE;
        } else {
            opponent = Owner.PLAYER_BLACK;
        }
        return opponent;

    }

    static boolean inLine(List<Vector> l) {
        Vector test = l.get(0);

        int x = test.getX();
        int y = test.getY();
        int z = test.getZ();
        return l.stream().allMatch(v -> (v.getX() == x))
                || l.stream().allMatch(v -> (v.getY() == y))
                || l.stream().allMatch(v -> (v.getZ() == z));
    }
}

class Vectors extends Vector implements Comparator<Vector> {
    private final Vector vector;

    Vectors(Vector v) {
        super(v.getX(), v.getY());
        vector = v;
    }

    @Override
    public int compare(Vector o1, Vector o2) {
        int x1 = o1.getX();
        int x2 = o2.getX();
        if (x1 > x2) {
            return 1;
        } else if (x1 < x2) {
            return -1;
        } else {
            int y1 = o1.getY();
            int y2 = o2.getY();
            return Integer.compare(y1, y2);
        }
    }

    private int getLength(final Vector hex) {
        return (int) ((Math.abs(hex.getX()) + Math.abs(hex.getY())
                + Math.abs(hex.getZ())) / 2.0);
    }

    int getDistance(final Vector b) {
        return getLength(subtractVector(b));
    }

    // Vector Subtraction
    Vector subtractVector(final Vector b) {
        return new Vector(vector.getX() - b.getX(), vector.getY() - b.getY());
    }

    Vector goDirection(Vector c) {
        return new Vector(vector.getX() - c.getX(), vector.getY() - c.getY());
    }

    Vector south() {
        return new Vector(vector.getX(), vector.getY() + 1);
    }

    Vector north() {
        return new Vector(vector.getX(), vector.getY() - 1);
    }

    Vector east() {
        return new Vector(vector.getX() + 1, vector.getY());
    }

    Vector west() {
        return new Vector(vector.getX() - 1, vector.getY());
    }

    Vector opposite() {
        return new Vector(-vector.getX(), -vector.getY());
    }

}