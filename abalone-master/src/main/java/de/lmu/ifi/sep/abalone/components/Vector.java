package de.lmu.ifi.sep.abalone.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordinate class to save x and y positions.
 */
public class Vector implements Serializable {

    /**
     * The x-position.
     */
    private final int x;

    /**
     * The y-position.
     */
    private final int y;

    /**
     * Axial Coordinate constructor.
     *
     * @param posX The x-position saved by this object.
     * @param posY The y-position saved by this object.
     */
    public Vector(final int posX, final int posY) {
        x = posX;
        y = posY;
    }

    /**
     * Returns the {@code Direction} of Move.
     *
     * @param start  {@code Vector} where the move originates from.
     * @param target Destination of move as {@code Vector}.
     * @return Direction that the move will occur.
     */
    public static Direction getDirectionOfMove(Vector start, Vector target) {
        Vector delta = new Vector(target.x - start.x, target.y - start.y);
        for (Direction direction : Direction.values()) {
            if (delta.equals(direction.getVector())) {
                return direction;
            }
        }
        return null;
    }

    /**
     * Getter method for the x-position.
     *
     * @return x-position as an {@code int}.
     */
    public int getX() {
        return x;
    }

    /**
     * Getter method for the y-position.
     *
     * @return y-position as an {@code int}.
     */
    public int getY() {
        return y;
    }

    /**
     * Getter method for the z-position.
     *
     * @return z-position as an {@code int}.
     */
    public int getZ() {
        return -x - y;
    }

    /**
     * Checks in which direction a Vector is oriented.
     *
     * @param v The Vector which direction you need.
     * @return The Direction if v is neighbor of this.Vector. Else: null.
     */
    public Direction getDirection(Vector v) {
        Vector c = subtractFrom(v);
        for (Direction d : Direction.values()) {
            if (d.getVector().equals(c)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Moves this {@code Vector} in the {@code Direction} of passed parameter
     * direction.
     *
     * @param direction {@code Direction} the {@code Vector} will move in.
     * @return Vector in the direction.
     */
    public Vector go(Direction direction) {
        if (direction == null) {
            return new Vector(getX(), getY());
        }
        int x = getX() + direction.getVector().getX();
        int y = getY() + direction.getVector().getY();
        return new Vector(x, y);
    }

    /**
     * Moves this {@code Vector} in the opposite {@code Direction} of passed
     * parameter direction.
     *
     * @param direction {@code Direction} the {@code Vector} will move in.
     * @return Vector in the direction that was passed.
     */
    public Vector invertGo(Direction direction) {
        int x = getX() - direction.getVector().getX();
        int y = getY() - direction.getVector().getY();
        return new Vector(x, y);
    }

    /**
     * Returns the {@code List} of neighbors to this {@code Vector}.
     *
     * @return All neighbors of this {@code Vector}.
     */
    public List<Vector> getNeighbors() {
        List<Vector> neighbors = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            neighbors.add(go(dir));
        }
        return neighbors;
    }

    /**
     * Vector you want to subtract from.
     *
     * @param v Vector that will be subtracted from this Vector.
     * @return Result of vector subtraction as {@code Vector}.
     */
    public Vector subtractFrom(Vector v) {
        return new Vector(v.getX() - getX(), v.getY() - getY());
    }

    /**
     * Vector addition.
     *
     * @param b {@code Vector} that will be added to this Vector.
     * @return Result of vector addition as {@code Vector}.
     */
    public Vector addVector(Vector b) {
        int xNew = this.x + b.getX();
        int yNew = this.y + b.getY();
        return new Vector(xNew, yNew);
    }

    /**
     * Returns this {@code Vector} as a Hashcode.
     *
     * @return HashCode as {@code int}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    /**
     * Determines if this Vector is equal to {@code Object o}.
     *
     * @param o Object to be tested for equality.
     * @return {@code True} if this Vector is equal to o.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vector)) {
            return false;
        }
        Vector c = (Vector) o;
        return c.getX() == getX() && c.getY() == getY();
    }

    /**
     * Returns this {@code Vector} as a {@code String}.
     *
     * @return This {@code Vector} as a string.
     */
    public String toString() {
        return "(" + getX() + "," + getY() + ")";
    }

    public enum Direction {
        NW(new Vector(0, -1)), NE(new Vector(1, -1)),
        E(new Vector(1, 0)), SE(new Vector(0, 1)),
        SW(new Vector(-1, 1)), W(new Vector(-1, 0));

        private final Vector direction;

        Direction(Vector c) {
            direction = c;
        }

        public Vector getVector() {
            return direction;
        }
    }
}
