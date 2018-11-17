package de.lmu.ifi.sep.abalone.logic.communication;


import de.lmu.ifi.sep.abalone.components.Vector;

import java.io.Serializable;
import java.util.List;

/**
 * Data class to model a move made by a player
 */
public class Move implements Serializable {

    private final List<Vector> selected;
    private final Vector target;

    public Move(List<Vector> selected, Vector target) {
        this.selected = selected;
        this.target = target;
    }

    public List<Vector> getSelected() {
        return selected;
    }

    public Vector getTarget() {
        return target;
    }

    @Override
    public String toString() {
        return "s:[" + selected.toString() + "]::(" + target.toString() + ")";
    }
}
