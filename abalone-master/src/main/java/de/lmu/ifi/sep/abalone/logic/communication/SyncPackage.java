package de.lmu.ifi.sep.abalone.logic.communication;

import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.logic.Player;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class SyncPackage implements Serializable {

    private final LinkedHashMap<Vector, AbaloneBoard.Owner> board;
    private final Player playerOne;
    private final Player playerTwo;

    public SyncPackage(LinkedHashMap<Vector, AbaloneBoard.Owner> board, Player playerOne, Player playerTwo) {
        this.board = board;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    public LinkedHashMap<Vector, AbaloneBoard.Owner> getBoard() {
        return board;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }
}
