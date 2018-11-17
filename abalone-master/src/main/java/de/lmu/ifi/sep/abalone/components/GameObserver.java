package de.lmu.ifi.sep.abalone.components;

import de.lmu.ifi.sep.abalone.models.AbaloneBoard;

import java.util.List;

public interface GameObserver {

    void setValidMoves(List<Vector> validMoves);

    void setValidClicks(List<Vector> validClicks);

    void endTurn(AbaloneBoard.Owner next);
}
