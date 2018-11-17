package de.lmu.ifi.sep.abalone.views;

import de.lmu.ifi.sep.abalone.components.Vector;

/**
 * Utility class to enable complete separation of model and view layout by
 * acting as the intermediary between the two coordinate systems.
 * <p>
 * </p> The {@code AbaloneBoard} saves coordinates on the model as Skew
 * coordinates shifting the y-axis. View uses the Cartesian coordinate system
 * with orthogonal (x,y) vectors. The {@code AbaloneView} class is the
 * responsible party for implementing this class and converting coordinates.
 * <p>
 * </p> This approach enabled circumventing offset mapping, allows dynamic
 * resizing, and flexibility in regards of implementation of algorithms without
 * consideration of side effects.
 * <p>
 * </p> Adapted from:
 *
 * @see <a href="www.redblobgames.com">www.redblobgames.com</a>
 */
public class Layout {

    /**
     * First constant to reverse the sheer transformation.
     */
    private static final double F0 = Math.sqrt(3.0);

    /**
     * Second constant to reverse the sheer transformation.
     */
    private static final double F1 = Math.sqrt(3.0) / 2.0;

    /**
     * Third constant to reverse the sheer transformation.
     */
    private static final double F2 = 0.0;

    /**
     * Fourth constant to reverse the sheer transformation.
     */
    private static final double F3 = 3.0 / 2.0;

    /**
     * First constant to perform the sheer transformation.
     */
    private static final double B0 = Math.sqrt(3.0) / 3.0;

    /**
     * Second constant to perform the sheer transformation.
     */
    private static final double B1 = -1.0 / 3.0;

    /**
     * Third constant to perform the sheer transformation.
     */
    private static final double B2 = 0;

    /**
     * Fourth constant to perform the sheer transformation.
     */
    private static final double B3 = 2.0 / 3.0;

    /**
     * Preferred size of the spaces in the view.
     */
    private final int HEX_SIZE;

    /**
     * Center of the JPanel that will display the view.
     */
    private final int ORIGIN;

    /**
     * Layout of this game constructor.
     *
     * @param s   Size of the fields on the game board. Between 25 - 38 in this
     *            instance.
     * @param org Origin to control where to set the GUI game board.
     */
    public Layout(int s, int org) {
        HEX_SIZE = s;
        ORIGIN = org;
    }

    /**
     * Function to transform Hexagonal Coordinates to Cartesian.
     * Concatenates the current {@code Vector} with a shearing
     * transform represented by the Matrix below.
     * [   F0   F1  ]   [ x ]
     * [   F2   F3  ] * [ y ] * HEX_SIZE
     *
     * @param h Hexagonal Coordinate prior to transform.
     * @return {@code Vector} as Cartesian Coordinate.
     */
    public Vector hexToPixel(Vector h) {
        double x = (F0 * h.getX() + F1 * h.getY()) * HEX_SIZE;
        double y = (F2 * h.getX() + F3 * h.getY()) * HEX_SIZE;
        return new Vector((int) (x + ORIGIN), (int) (y + ORIGIN));
    }

    /**
     * Function to transform GUI Cartesian Coordinates to Hexagonal taking into
     * account that the user may not have pressed in center of button.
     * Concatenates the current {@code Vector} with reverse of the
     * shearing performed by {@code hexToPixel} method represented by
     * the Matrix below.
     * [   B0   B1  ]   [ x ]
     * [   B2   B3  ] * [ y ] * HEX_SIZE
     *
     * @param p Cartesian Coordinate (from GUI) prior to transform.
     * @return {@code Vector} as Hexagonal Coordinate.
     */
    public Vector pixelToHex(Vector p) {
        Vector pt = new Vector((p.getX() - ORIGIN) / HEX_SIZE,
                (p.getY() - ORIGIN) / HEX_SIZE);
        double q = (B0 * pt.getX() + B1 * pt.getY());
        double r = (B2 * pt.getX() + B3 * pt.getY());
        // can't guarantee click is in center
        // need to convert from fractional double to int
        int qRound = (int) Math.round(q);
        int rRound = (int) Math.round(r);
        int sRound = (int) Math.round(-q - r);
        double qdiff = (int) Math.abs(qRound - q);
        double rdiff = (int) Math.abs(rRound - r);
        double sdiff = (int) Math.abs(sRound - (-q - r));
        if (qdiff > rdiff && qdiff > sdiff) {
            qRound = -rRound - sRound;
        } else if (rdiff > sdiff) {
            rRound = -qRound - sRound;
        }
        return new Vector(qRound, rRound);
    }

    /**
     * Accessor to current game field sizes.
     *
     * @return size of individual game cells.
     */
    public int getSize() {
        return HEX_SIZE;
    }

    /**
     * This game view center point.
     *
     * @return This game view center as {@code int}.
     */
    public int getOrigin() {
        return ORIGIN;
    }

}
