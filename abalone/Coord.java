package abalone;

import java.util.Objects;

/**
 * Coordinate class to save x and y positions.
 */
public class Coord {

    /**
     * The x-position.
     */
    private final int x;

    /**
     * The y-position.
     */
    private final int y;
    
    /**
     * The y-position.
     */
    private final int z;

    /**
     * Axial Coordinate constructor.
     *
     * @param posX
     *            The x-position saved by this object.
     * @param posY
     *            The y-position saved by this object.
     */
    public Coord(final int posX, final int posY) {
        this(posX, posY,-posX-posY);
    }
    
    /**
     * Cuboidal Coordinate constructor.
     *
     * @param posX
     *            The x-position saved by this object.
     * @param posY
     *            The y-position saved by this object.
     */
    public Coord(final int posX, final int posY, final int posZ) {
        x = Objects.requireNonNull(posX);
        y = Objects.requireNonNull(posY);
        z = Objects.requireNonNull(posZ);
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
        return z;
    }
    
}
