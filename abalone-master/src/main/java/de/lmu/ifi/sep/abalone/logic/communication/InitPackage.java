package de.lmu.ifi.sep.abalone.logic.communication;

import de.lmu.ifi.sep.abalone.models.AbaloneBoard;

import java.io.Serializable;

public class InitPackage implements Serializable {
    private final int GAME_SIZE;
    private final AbaloneBoard.Owner YOUR_COLOR;

    public InitPackage(int gameSize, AbaloneBoard.Owner yourColor) {
        this.GAME_SIZE = gameSize;
        this.YOUR_COLOR = yourColor;
    }

    public int getGameSize() {
        return GAME_SIZE;
    }

    public AbaloneBoard.Owner getYourColor() {
        return YOUR_COLOR;
    }
}
