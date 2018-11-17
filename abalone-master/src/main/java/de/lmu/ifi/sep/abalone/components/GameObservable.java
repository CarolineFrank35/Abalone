package de.lmu.ifi.sep.abalone.components;

import de.lmu.ifi.sep.abalone.models.AbaloneBoard;

import java.util.LinkedList;
import java.util.List;

/**
 * custom Observable for the game logic to notify subscribers of
 * changes to:
 * - valid clicks
 * - valid moves
 * - end of turn
 * events.
 * <p>
 * To be implemented by {@link de.lmu.ifi.sep.abalone.logic.AbaloneGame}
 */
public abstract class GameObservable {

    /**
     * internal list of observers to notify dn triggered events
     */
    protected final List<GameObserver> gameObservers = new LinkedList<>();

    /**
     * adds a observer to the internal list
     *
     * @param o GameObserver to add to the active subscribers
     */
    public void addObserver(GameObserver o) {
        gameObservers.add(o);
    }

    /**
     * fires whenever new information about clickable tiles
     * are available.
     */
    protected abstract void notifyValidClicks(List<Vector> validClicks);

    /**
     * fires whenever new information about valid move
     * directions are available.
     * <p>
     * NOTE: might be subjected to changes due to changes of the
     * {@link de.lmu.ifi.sep.abalone.logic.Context} class workflow
     */
    protected abstract void notifyValidMoves(List<Vector> validMoves);

    /**
     * fires whenever a turn of either players ends
     */
    protected abstract void notifyEndTurn(AbaloneBoard.Owner next);
}
