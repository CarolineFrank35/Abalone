package de.lmu.ifi.sep.abalone.logic;

import de.lmu.ifi.sep.abalone.models.AbaloneBoard;

import java.io.Serializable;

public class Player implements Serializable {

    private final AbaloneBoard.Owner owner;
    private final boolean isHost;
    private final boolean localPlayer;
    private int piecesToWin;

    public Player(AbaloneBoard.Owner owner, boolean isHost, int piecesToWin, boolean localPlayer) {
        this.owner = owner;
        this.isHost = isHost;
        this.piecesToWin = piecesToWin;
        this.localPlayer = localPlayer;
    }

    public AbaloneBoard.Owner getOwner() {
        return owner;
    }

    public boolean isHost() {
        return isHost;
    }

    int getPiecesToWin() {
        return piecesToWin;
    }

    void didScore() {
        this.piecesToWin--;
    }

    boolean isLocalPlayer() {
        return localPlayer;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Player)) {
            return false;
        }
        Player otherPlayer = (Player) other;

        return otherPlayer.getOwner() == this.getOwner() && otherPlayer.isHost == this.isHost
                && otherPlayer.localPlayer == this.localPlayer
                && otherPlayer.piecesToWin == this.piecesToWin;
    }

}
