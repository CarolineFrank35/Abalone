package abalone;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;

public class Hexagon {
    /**
     * 
     */
    private static final long serialVersionUID = 3140446492598407349L;
    private final int radius;
    private final Point center;
    private final Polygon hexagon;
    final static Color COLORHEX = new Color(102,51,0);


    public Hexagon(Point c, int r) {
        center = c;
        radius = r;
        hexagon = createHexagon();
    }
    
    public Color getColor() {
        return COLORHEX;
    }

    private Polygon createHexagon() {
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            int xval = (int) (center.x + radius
                    * Math.cos(i * 2 * Math.PI / 6D));
            int yval = (int) (center.y + radius
                    * Math.sin(i * 2 * Math.PI / 6D));
            polygon.addPoint(xval, yval);
        }
        return polygon;
    }

    public int getRadius() {
        return radius;
    }

    public Point getCenter() {
        return center;
    }

    public Polygon getHexagon() {
        return hexagon;
    }
    
}
