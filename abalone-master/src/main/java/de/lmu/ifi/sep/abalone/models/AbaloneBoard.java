package de.lmu.ifi.sep.abalone.models;

import de.lmu.ifi.sep.abalone.components.EntryObserver;
import de.lmu.ifi.sep.abalone.components.ObservableMap;
import de.lmu.ifi.sep.abalone.components.Vector;

import java.util.Map;

/**
 * Model in the MVC architecture, responsible for holding internal game
 * structure, position as {@code Vector} to {@code Owner} Mapping.
 */
public class AbaloneBoard {

    /**
     * Structure holding board data. Implements observer pattern.
     */
    private final ObservableMap<Vector, Owner> BOARD;
    /**
     * Maximum number of rows and columns of this board.
     */
    private final int BOARD_SIZE;

    /**
     * Constructor of Abalone Board with a Observable Map as a data structure.
     *
     * @param BOARD_SIZE Maximum rows and columns of this board.
     */
    public AbaloneBoard(final int BOARD_SIZE) {
        this.BOARD_SIZE = BOARD_SIZE;
        BOARD = initializeBoard(BOARD_SIZE);
    }

    public static String getOwnerName(Owner o) {
        String out = "";
        switch (o) {
            case EMPTY:
                out = "empty";
                break;
            case PLAYER_WHITE:
                out = "white";
                break;
            case PLAYER_BLACK:
                out = "black";
                break;
        }
        return out;
    }

    /**
     * Utility method to initialize game board with BOARD_SIZE.
     *
     * @param size Maximum rows and columns of this board.
     * @return board of this class.
     */
    private ObservableMap<Vector, Owner> initializeBoard(final int size) {
        ObservableMap<Vector, Owner> board = new ObservableMap<>();
        int radius = size / 2;
        int cond1 = radius - 2;
        Owner o;
        for (int y = -radius; y <= radius; y++) {
            int x1 = Math.max(-radius, -y - radius);
            int x2 = Math.min(radius, -y + radius);
            for (int x = x1; x <= x2; x++) {
                boolean cond2 = x > -1 && x < radius - 1;
                boolean cond3 = x < 1 && x > -radius + 1;
                if (y < -cond1 || (y == -cond1 && cond2)) {
                    o = Owner.PLAYER_WHITE;
                } else if (y > cond1 || (y == cond1 && cond3)) {
                    o = Owner.PLAYER_BLACK;
                } else {
                    o = Owner.EMPTY;
                }
                board.put(new Vector(x, y), o);
            }
        }
        return board;
    }

    /**
     * Accessor method to this board.
     *
     * @return this board
     */
    public Map<Vector, Owner> getBoard() {
        return BOARD;
    }

    public void setBoard(ObservableMap<Vector, Owner> board) {
        for (Map.Entry<Vector, Owner> entry : board.entrySet()) {
            this.BOARD.replace(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Checks if coordinate exists on the board.
     *
     * @return true if board has position, otherwise false.
     */
    public boolean hasPosition(Vector c) {
        boolean containsKey = false;
        if (BOARD.containsKey(c)) {
            containsKey = true;
        }
        return containsKey;
    }

    /**
     * Accessor for this board's maximum row and column size.
     *
     * @return maximum row and column size as {@code int}.
     */
    public int getSize() {
        return BOARD_SIZE;
    }

    /**
     * Method to change the current owner of a position on this board.
     *
     * @param c        Vector that is related to a position on this board.
     * @param changeTo Owner that the position of the board is being changed to.
     */
    public synchronized void setOwner(Vector c, Owner changeTo) {
        if (BOARD != null && BOARD.containsKey(c)) {
            BOARD.replace(c, changeTo);
        }
    }

    /**
     * Method to return current {@code Owner} associated with {@code Vector}.
     *
     * @param c Position as {@code Vector} on this board.
     * @return Owner of the position on this board.
     */
    public synchronized Owner getOwner(Vector c) {
        Owner owner = null;
        if (BOARD.containsKey(c)) {
            owner = BOARD.get(c);
        }
        return owner;
    }

    /**
     * Adds observers that are interested in receiving information regarding
     * changes to entries held by this map.
     *
     * @param obs Observer that is interested in receiving changes of entries held
     *            by the map.
     */
    public void addObserver(EntryObserver<Vector, Owner> obs) {
        BOARD.addObserver(obs);
    }

    /**
     * Enumeration of possible owners of the spaces on the board.
     */
    public enum Owner {
        EMPTY,
        PLAYER_WHITE,
        PLAYER_BLACK

    }

}
