package de.lmu.ifi.sep.abalone.logic;

import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.components.Vector.Direction;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard.Owner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Context {

    /**
     * Checks if a selection of pebbles is movable in any direction.
     *
     * @param currentSelected List of currently selected pebbles
     * @param board           Reference to current game board as {@code map}
     * @param activePlayer    Currently active player selecting pebbles
     * @return {@code true} if movable, otherwise {@code false}
     */
    private static boolean checkPotentiallyMovable(final Map<Vector, Owner> board,
                                                   final List<Vector> currentSelected,
                                                   final Owner activePlayer) {

        for (Direction d : Direction.values()) {
            if (isValidMove(board, currentSelected, d, activePlayer)) {
                return true;
            }
        }
        if (currentSelected.size() == 0) {
            for (Entry<Vector, Owner> pebble : board.entrySet()) {
                List<Vector> toCheck = new ArrayList<>();
                toCheck.add(pebble.getKey());
                for (Direction direction : Vector.Direction.values()) {
                    if (potentiallyMovableInDirection
                            (board, toCheck, activePlayer, direction)) {
                        return true;
                    }
                }
            }
        } else if (currentSelected.size() == 1) {
            List<Vector> neighbors = currentSelected.get(0).getNeighbors();
            for (Vector v : neighbors) {
                List<Vector> toCheck = new ArrayList<>();
                toCheck.add(currentSelected.get(0));
                toCheck.add(v);
                for (Direction direction : Direction.values()) {
                    if (potentiallyMovableInDirection
                            (board, toCheck, activePlayer, direction)) {
                        return true;
                    }
                }
            }
        } else if (currentSelected.size() == 2) {
            Direction one = currentSelected.get(0).getDirection(currentSelected.get(1));
            Direction two = currentSelected.get(1).getDirection(currentSelected.get(0));
            List<Vector> toCheck = new ArrayList<>(currentSelected);
            toCheck.add(currentSelected.get(1).go(one));
            for (Direction direction : Vector.Direction.values()) {
                if (potentiallyMovableInDirection(board, toCheck, activePlayer, direction)) {
                    return true;
                }
            }
            toCheck.clear();
            toCheck.addAll(currentSelected);
            toCheck.add(currentSelected.get(0).go(two));
            for (Direction direction : Vector.Direction.values()) {
                if (potentiallyMovableInDirection(board, toCheck, activePlayer, direction)) {
                    return true;
                }
            }
        } else if (currentSelected.size() == 3) {
            for (Direction direction : Vector.Direction.values()) {
                if (potentiallyMovableInDirection
                        (board, currentSelected, activePlayer, direction)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calculates possible player strength in one direction from currently selected
     * pebbles and compares with next space strength
     *
     * @param board           The board
     * @param currentSelected The current selected pebbles
     * @param activePlayer    The active player
     * @param direction       The direction you want to check
     * @return {@code true} if movable, otherwise {@code false}
     */
    private static boolean
    potentiallyMovableInDirection(final Map<Vector, Owner> board,
                                  final List<Vector> currentSelected,
                                  final Owner activePlayer,
                                  final Direction direction) {
        if (currentSelected == null) {
            return false;
        }
        if (currentSelected.isEmpty() || currentSelected.size() > 3
                || !(isInLine(currentSelected))) {
            return false;
        }
        if (isInDirection(currentSelected, direction)) {
            int strength = currentSelected.size();
            Vector leadingPebble = findLeadingPebbleFromList(currentSelected, direction);
            assert leadingPebble != null;
            Vector next = leadingPebble.go(direction);
            while (strength <= 3) {
                if (!board.containsKey(next)) {
                    strength = 0;
                    break;
                } else if (board.get(next) == board.get(leadingPebble)) {
                    strength++;
                    next = next.go(direction);
                } else {
                    break;
                }
            }
            return nextSpaceStrength(board, next.invertGo(direction), direction, activePlayer)
                    < strength;
        } else {
            for (Vector v : currentSelected) {
                if (!(board.get(v.go(direction)) == Owner.EMPTY)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Calculates the directions in that the selected pebbles are movable
     *
     * @param board           The board
     * @param currentSelected The selected pebbles
     * @param activePlayer    The active player
     * @return The Directions in that the selection is movable
     */
    public static List<Direction> getValidMoves(final Map<Vector, Owner> board,
                                                final List<Vector> currentSelected,
                                                final Owner activePlayer) {
        List<Direction> validMoves = new ArrayList<>();
        for (Direction direction : Vector.Direction.values()) {
            if (isValidMove(board, currentSelected, direction, activePlayer)
                    && !validMoves.contains(direction)) {
                validMoves.add(direction);
            }
        }
        return validMoves;
    }

    /**
     * Calculates the pebbles that are potentially movable in addition to the ones
     * already selected.
     *
     * @param currentSelected The pebbles that are already selected.
     * @param board           The board
     * @param activePlayer    The active player
     * @return Clickable pebbles
     */
    public static List<Vector> getValidClicks(final Map<Vector, Owner> board,
                                              final List<Vector> currentSelected,
                                              final Owner activePlayer) {
        List<Vector> validClicks = new ArrayList<>();
        if (currentSelected.size() == 0) {
            for (Entry<Vector, Owner> pebble : board.entrySet()) {
                if (pebble.getValue() == activePlayer) {
                    List<Vector> toCheck = new ArrayList<>();
                    toCheck.add(pebble.getKey());
                    if (checkPotentiallyMovable(board, toCheck, activePlayer)
                            && !validClicks.contains(pebble.getKey())) {
                        validClicks.add(pebble.getKey());
                    }
                }
            }
        } else if (currentSelected.size() == 1) {
            List<Vector> neighbors = currentSelected.get(0).getNeighbors();
            for (Vector v : neighbors) {
                if (board.containsKey(v)) {
                    if (board.get(v) == board.get(currentSelected.get(0))) {
                        List<Vector> toCheck = new ArrayList<>(currentSelected);
                        toCheck.add(v);
                        if (checkPotentiallyMovable(board, toCheck, activePlayer)
                                && !validClicks.contains(v)) {
                            validClicks.add(v);
                        }
                    }
                }
            }
        } else if (currentSelected.size() == 2) {
            if (!isInLine(currentSelected)) {
                for (Vector v : currentSelected.get(0).getNeighbors()) {
                    for (Vector f : currentSelected.get(1).getNeighbors()) {
                        if (v.equals(f) && !validClicks.contains(f)) {
                            validClicks.add(f);
                            return validClicks;
                        }
                    }
                }
            }
            Direction direction1 =
                    currentSelected.get(0).getDirection(currentSelected.get(1));
            Direction direction2 =
                    currentSelected.get(1).getDirection(currentSelected.get(0));
            Vector one = currentSelected.get(1).go(direction1);
            Vector two = currentSelected.get(0).go(direction2);
            List<Vector> toCheck = new ArrayList<>(currentSelected);
            toCheck.add(one);
            if (board.containsKey(one)) {
                for (Direction direction : Vector.Direction.values()) {
                    if (isValidMove(board, toCheck, direction, activePlayer)
                            && !validClicks.contains(one)) {
                        validClicks.add(one);
                    }
                }
            }
            toCheck = new ArrayList<>(currentSelected);
            toCheck.add(two);
            if (board.containsKey(two)) {
                for (Direction direction : Vector.Direction.values()) {
                    if (isValidMove(board, toCheck, direction, activePlayer)
                            && !validClicks.contains(two)) {
                        validClicks.add(two);
                    }
                }
            }
        }
        return validClicks;
    }

    /**
     * Checks if a list of Vectors is movable in given direction.
     *
     * @param currentSelected List of selected {@code Vectors} being checked.
     * @param board           The board.
     * @param direction       The direction you wish to check.
     * @param activePlayer    The player whose turn it is.
     * @return True if movable; False if not movable.
     */
    static boolean isValidMove(final Map<Vector, Owner> board,
                               final List<Vector> currentSelected,
                               final Direction direction,
                               final Owner activePlayer) {
        if (currentSelected.size() > 3) {
            return false;// Max three pebbles are movable
        }
        if (!(isInLine(currentSelected))) {
            return false;// Can only move if pebbles that are in line
        }
        for (Vector v : currentSelected) {
            if (!(board.get(v).equals(activePlayer))) {
                return false;
            }
        }
        if ((currentSelected.size() > 1) && (isInDirection(currentSelected, direction))) {
            Vector leadingPebble = findLeadingPebbleFromList(currentSelected, direction);
            if (leadingPebble != null) {
                return (currentSelected.size() > nextSpaceStrength(
                        board, leadingPebble, direction, activePlayer));
            }
        }
        for (Vector v : currentSelected) {
            if (!board.containsKey(v.go(direction))) {
                return false;
            } else if (!(board.get(v.go(direction)) == Owner.EMPTY)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates opponents strength, from active player, in one direction.
     *
     * @param board        The board.
     * @param pebble       The Vector after which to check.
     * @param direction    The DirectionHelper in which to check.
     * @param activePlayer The Owner whose turn it is.
     * @return strength as {@code int} if valid move, otherwise returns
     * {@code INTEGER.MAX_VALUE}.
     */
    public static int nextSpaceStrength(final Map<Vector, Owner> board,
                                        final Vector pebble,
                                        final Direction direction,
                                        final Owner activePlayer) {
        int strength = 0;
        Vector next = pebble.go(direction);
        if (board.containsKey(next)) {
            if (board.get(next) == activePlayer) {
                return Integer.MAX_VALUE;
            } else if (board.get(next) == Owner.EMPTY) {
                return strength;
            } else {
                Owner opponent = board.get(next);
                while (board.containsKey(next) && (board.get(next) == opponent)) {
                    strength++;
                    next = next.go(direction);
                }
                if ((board.get(next) == activePlayer)) {
                    return Integer.MAX_VALUE;
                }
                return strength;
            }
        } else {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Searches for the leading pebble in List<Vector> in given direction.
     *
     * @param currentSelected The list you want to check.
     * @param direction       The direction you want to check.
     * @return Returns null if list is empty, or pebbles are not connected in given
     * direction in a straight line.
     * @implNote By implementation of this method, must determine pebbles in
     * currentSelected are facing in given direction!
     */
    private static Vector findLeadingPebbleFromList(final List<Vector> currentSelected,
                                                    final Direction direction) {
        if (!(isInDirection(currentSelected, direction))) {
            return null;
        } else {
            for (Vector v : currentSelected) {
                if (!currentSelected.contains(v.go(direction))) {
                    return v;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a List<Vector> is connected in a straight line in a specific
     * direction.
     *
     * @param toCheck   The list you want to check
     * @param direction The direction you want to check
     * @return True, if toCheck is connected in a straight line in given direction
     */
    public static boolean isInDirection(final List<Vector> toCheck,
                                        final Direction direction) {
        if (toCheck == null || direction == null) {
            return false;
        }
        if (toCheck.size() == 0) {
            return true;
        }
        int maxOneNotInToCheck = 0;
        for (Vector v : toCheck) {
            if (!(toCheck.contains(v.go(direction)))) {
                maxOneNotInToCheck++;
            }
        }
        return maxOneNotInToCheck == 1;
    }

    /**
     * Checks if a list of Vectors are connected in a straight line.
     *
     * @param toCheck The list you want to check.
     * @return If a List<Vector> is connected in a straight line.
     */
    public static boolean isInLine(final List<Vector> toCheck) {
        if (toCheck == null) {
            return false;
        }
        if (toCheck.size() == 0 || toCheck.size() == 1) {
            return true;
        }
        Direction direction = null;
        for (Direction d : Vector.Direction.values()) {
            if (toCheck.contains(toCheck.get(0).go(d))) {
                direction = d;
            }
        }
        return direction != null && isInDirection(toCheck, direction);
    }
}