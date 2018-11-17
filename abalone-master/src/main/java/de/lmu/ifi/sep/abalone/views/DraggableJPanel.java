package de.lmu.ifi.sep.abalone.views;

import de.lmu.ifi.sep.abalone.components.Vector;

import javax.swing.*;
import java.awt.*;

/**
 * A JPanel that listens to mouse movements during drag and drop operations.
 */
public class DraggableJPanel extends JPanel {

    /**
     * Identifies the semantics of being serializable and allows sender and
     * receiver to verify that they have compatible versions of this object in
     * respect to serialization.
     */
    private static final long serialVersionUID = -1947254335981986497L;

    /**
     * Reference variable to the parent of this Panel.
     */
    private final AbaloneView ROOT;

    /**
     * The size the buttons will have on the field.
     */
    private final int ovalSize;

    /**
     * Source of the drag motion.
     */
    private RoundButton source;

    /**
     * Offset in x direction corresponding to button out of the selectedButtons
     * list that started the drag and drop motion.
     */
    private int offsetX = 0;
    /**
     * Offset in y direction corresponding to button out of the selectedButtons
     * list that started the drag and drop motion.
     */
    private int offsetY = 0;

    /**
     * Constructor for a draggable JPanel.
     *
     * @param main  Reference to this panels parent panel.
     * @param oSize Size of the RoundButtons.
     */
    DraggableJPanel(final AbaloneView main, final int oSize) {
        super(null, true); //No layout manager
        setOpaque(false);
        setBounds(0, 0, 200, 200);
        ROOT = main;
        ovalSize = oSize;
        repaint();
    }

    /**
     * Method to paint this Component, called automatically when program
     * begins, when the window is refreshed and when {@code repaint} is invoked.
     *
     * @param g Graphics object
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(255, 255, 0, 100));
        buildPanel(g);
    }

    /**
     * Sets up the panel to show the drag N Drop visual
     *
     * @param g Reference to this object's graphic
     */
    private void buildPanel(Graphics g) {
        int size = ROOT.getSelected().size();
        offsetX = 0;
        offsetY = 0;
        if (size == 1) {
            g.fillOval(5, 5, ovalSize, ovalSize);
        } else if (size > 1) {
            Vector b1 = ROOT.getSelected().get(0).getPosition();
            Vector b2 = ROOT.getSelected().get(1).getPosition();
            int y1 = b1.getY();
            int y2 = b2.getY();
            int x1 = b1.getX();
            int x2 = b2.getX();
            if (x1 == x2) { // NW to SE
                g.fillOval(5, 5, ovalSize, ovalSize);
                g.fillOval(34, 56, ovalSize, ovalSize);
                if (ROOT.getSelected().get(1).equals(source)) {
                    offsetX = -34;
                    offsetY = -56;
                }
                if (size == 3) {
                    g.fillOval(64, 107, ovalSize, ovalSize);
                    if (ROOT.getSelected().get(2).equals(source)) {
                        offsetX = -64;
                        offsetY = -120;
                    }
                }
            } else if (y1 == y2) { // W to E
                g.fillOval(5, 5, ovalSize, ovalSize);
                g.fillOval(64, 5, ovalSize, ovalSize);
                if (ROOT.getSelected().get(1).equals(source)) {
                    offsetX = -60;
                }
                if (ROOT.getSelected().size() == 3) {
                    g.fillOval(123, 5, ovalSize, ovalSize);
                    if (ROOT.getSelected().get(2).equals(source)) {
                        offsetX = -130;
                    }
                }
            } else { // SW to NE
                offsetX = -64;
                g.fillOval(64, 5, ovalSize, ovalSize);
                g.fillOval(35, 56, ovalSize, ovalSize);
                if (ROOT.getSelected().get(1).equals(source)) {
                    offsetX = -35;
                    offsetY = -50;
                }
                if (size == 3) {
                    g.fillOval(5, 107, ovalSize, ovalSize);
                    if (ROOT.getSelected().get(2).equals(source)) {
                        offsetX = 0;
                        offsetY = -107;
                    }
                }
            }
        }
        validate();
    }

    /**
     * Receives button that started drag and drop.
     *
     * @param button RoundButton that started the drag and drop motion.
     */
    void sendSource(RoundButton button) {
        source = button;
    }

    /**
     * Called when a dragged mouse changes locations.
     *
     * @param x The x-coordinate where the user's mouse is during a drag action.
     * @param y The y-coordinate where the user's mouse is during a drag action.
     */
    @Override
    public void setLocation(final int x, final int y) {
        super.setLocation(x + offsetX, y + offsetY);
        validate();
    }

}
