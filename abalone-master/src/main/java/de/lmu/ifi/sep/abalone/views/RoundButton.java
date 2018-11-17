package de.lmu.ifi.sep.abalone.views;

import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard.Owner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 * Each button corresponds to a field on the game board and renders whether the
 * space is occupied by a player, empty, or selected by the user.
 */
public class RoundButton extends JButton implements Comparable<RoundButton> {

    /**
     * Identifies the semantics of being serializable and allows sender and
     * receiver to verify that they have compatible versions of this object in
     * respect to serialization.
     */
    private static final long serialVersionUID = -678970048927599810L;

    // Paint options

    /**
     * 3D coloring to represent a black player piece on the button.
     */
    private static final Color[] BLACK_PLAYER = {
            new Color(0, 0, 0),                         // 0: Main color
            new Color(0.075f, 0.075f, 0.075f, 0.4f), // 1: Shadow at top 1
            new Color(0.0f, 0.0f, 0.0f, 0.0f),       // 2: Shadow at top 2
            new Color(1.0f, 1.0f, 1.0f, 0.0f),       // 3: Highlight at bottom 1
            new Color(1.0f, 1.0f, 1.0f, 0.4f),       // 4: Highlight at bottom 2
            new Color(20, 20, 20, 127),              // 5: Dark Edge 1
            new Color(0.0f, 0.0f, 0.0f, 0.8f),       // 6: Dark Edge 2
            new Color(99, 99, 99, 255),              // 7: Highlight inner bottom 1
            new Color(103, 103, 103, 0),             // 8: Highlight inner bottom 2
            new Color(103, 103, 103, 0),             // 9: Oval Highlight top 1
            new Color(103, 103, 103, 0)              // 10: Oval Highlight top 2
    };

    /**
     * 3D coloring to represent a white player piece on the button.
     */
    private static final Color[] WHITE_PLAYER = {
            new Color(237, 237, 237),                   // 0: Main color
            new Color(0.4f, 0.4f, 0.4f, 0.4f),       // 1: Shadow at top 1
            new Color(0.725f, 0.725f, 0.725f, 0.0f), // 2: Shadow at top 2
            new Color(1.0f, 1.0f, 1.0f, 0.0f),       // 3: Highlight at bottom 1
            new Color(1.0f, 1.0f, 1.0f, 0.4f),       // 4: Highlight at bottom 2
            new Color(153, 145, 148, 127),           // 5: Dark Edge 1
            new Color(0.498f, 0.498f, 0.498f, 0.8f), // 6: Dark Edge 2
            new Color(240, 240, 240, 255),           // 7: Highlight inner bottom 1
            new Color(255, 255, 255, 0),             // 8: Highlight inner bottom 2
            new Color(255, 255, 255, 0),             // 9: Oval highlight top 1
            new Color(255, 255, 255, 0)              // 10: Oval highlight top 2
    };

    /**
     * 3D coloring to represent an empty space on the button.
     */
    private static final Color[] EMPTY_SPACE = {
            new Color(102, 51, 0),                 // 0: Main color
            new Color(189, 95, 0, 200),         // 1: Highlight at top 1
            new Color(189, 95, 0, 200),         // 2: Highlight at top 2
            new Color(102, 51, 0, 200),         // 3: Highlight at bottom 1
            new Color(1.0f, 1.0f, 1.0f, 0f),    // 4: Highlight at bottom 2
            new Color(153, 145, 148, 127),      // 5: Lightens Edge 1
            new Color(0f, 0f, 0f, 0.8f),        // 6: Darkens Edge 2
            new Color(102, 51, 0, 50),          // 7: Glazes inner bottom 1
            new Color(0, 0, 0, 50),             // 8: Darkens inner bottom 2
            new Color(102, 51, 0, 0),           // 9: Oval glaze top 1
            new Color(134, 75, 18, 100)         // 10: Oval highlight top 2
    };

    /**
     * 3D coloring to represent a user selected button.
     */
    private static final Color[] BUTTON_SELECTED = {
            Color.MAGENTA,                                        // 0: Main color
            new Color(0.6f, 0.4f, 0.6f, 0.4f),       // 1: Shadow at top 1
            new Color(0.725f, 0.725f, 0.725f, 0.0f), // 2: Shadow at top 2
            new Color(0.4f, 0.4f, 0.4f, 0.0f),       // 3: Highlight at bottom 1
            new Color(0.4f, 0.4f, 0.4f, 0.4f),       // 4: Highlight at bottom 2
            new Color(20, 20, 20, 50),               // 5: Dark Edge 1
            new Color(0.0f, 0.0f, 0.0f, 0.5f),       // 6: Dark Edge 2
            new Color(240, 240, 240, 150),           // 7: Highlight inner bottom 1
            new Color(255, 153, 255, 0),             // 8: Highlight inner bottom 2
            new Color(255, 153, 255, 0),             // 9: Oval highlight top 1
            new Color(255, 153, 255, 0)              // 10: Oval highlight top 2
    };

    /**
     * Highlights the outside of the button when the mouse is over the current
     * button and a valid selection determined by the controller.
     */
    private static final Color MOUSE_OVER_HIGHLIGHT = Color.magenta;

    /**
     * Border of button when it is enabled and mouse is not in it (contains
     * method returns false), or not enabled.
     */
    private static final Color BUTTON_BORDER = new Color(255, 153, 255, 0);

    /**
     * Allows button that initiated drag N drop operation to receive which
     * button the operation ended in (drop acceptor).
     */
    private static RoundButton lastEntered;

    /**
     * Flag that tracks whether a drag motion is currently occurring and is
     * {@code false} when no drag is occurring and {@code true} when it is.
     */
    private static boolean drag = false;

    /**
     * Reference to this round button.
     */
    private final RoundButton BUTTON;

    /**
     * Enables this class to interact with the AbaloneView controller class.
     */
    private final AbaloneView ROOT;

    /**
     * The drag and drop panel that visually represents the selected buttons.
     */
    private final DraggableJPanel draggable;

    /**
     * Position of this button on the AbaloneBoard.
     */
    private final Vector position;

    // Mouse listener helpers
    /**
     * Owner of the field that this button represents. Can be Empty or owned by
     * a player.
     */
    private Owner owner;

    /**
     * Mouse Adapter for the mouse listeners.
     */
    private MouseAdapter adapter;

    /**
     * If the button is enabled, tracks if the user's mouse is over this
     * component. If the mouse is over this component {@code true}, otherwise
     * {@code false}.
     */
    private boolean mouseOver = false;

    /**
     * A flag to track whether this button is available to accept a drop of the
     * operation. This cannot be an active player cell.
     */
    private boolean dropAcceptor = false;

    /**
     * A flag to track whether this button is selectable by the user.
     */
    private boolean enabled = false;

    /**
     * Returns whether this button is currently selected. If selected returns
     * {@code true}, otherwise false;
     */
    private boolean selected = false;

    /**
     * RoundButton constructor.
     *
     * @param own    Owner of this round button at initialization.
     * @param dPanel Reference to the draggable JPanel for the drag and drop
     *               visualization.
     * @param panel  Reference to the root JPanel that is responsible for all the
     *               buttons.
     * @param pos    Position of this button on the AbaloneBoard.
     */
    public RoundButton(final Owner own, final DraggableJPanel dPanel,
                       final AbaloneView panel, final Vector pos) {
        super();
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        owner = own;
        ROOT = panel;
        draggable = dPanel;
        BUTTON = this;
        position = pos;
        initMouseAdapter();
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    /**
     * Updates the owner of this button through the observer-observable
     * architecture.
     *
     * @param own New owner of this button, can also be the same.
     */
    void updateOwner(Owner own) {
        Runnable update = () -> {
            owner = own;
            repaint();
        };
        SwingUtilities.invokeLater(update);
    }

    /**
     * Returns whether this button is enabled for the user to be selected by the
     * user.
     *
     * @return {@code true} if this button is enabled, otherwise {@code false}.
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables this button to listen to user actions.
     *
     * @param enable If {@code true} this button is enabled, otherwise
     *               {@code false}.
     */
    @Override
    public void setEnabled(boolean enable) {
        enabled = enable;
        super.setEnabled(enabled);
    }

    /**
     * Flag to set this as a valid drop location in DnD action. Signifies that
     * this is a valid move destination.
     *
     * @param drop sets the flag if this button is valid drop location.
     */
    void setDropAcceptor(boolean drop) {
        dropAcceptor = drop;
    }

    /**
     * Flag whether this button is accepting drag and drop operation.
     *
     * @return {@code True} if button is a drop acceptor, otherwise
     * {@code false}.
     */
    private boolean isDropAcceptor() {
        return dropAcceptor;
    }

    /**
     * Flag whether this button is currently selected by user.
     *
     * @return {@code True} if button is selected, otherwise {@code false}.
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets 'selected' flag.
     *
     * @param select {@code True} if button is selected, otherwise {@code false}.
     */
    @Override
    public void setSelected(boolean select) {
        selected = select;
        repaint();
    }

    /**
     * Return the position of this button in reference to the field it
     * represents on the AbaloneBoard.
     *
     * @return Position of the abalone board as a vector.
     */
    public Vector getPosition() {
        return position;
    }

    /**
     * Initializes the mouse adapter for the mouse listeners.
     */
    private void initMouseAdapter() {
        adapter = new MouseAdapter() {

            // Selects button or performs move
            @Override
            public void mouseClicked(MouseEvent me) {
                if (enabled || selected) {
                    selected = !selected;
                    setSelected(selected);
                    ROOT.addSelectedButton(BUTTON);
                } else if (dropAcceptor) {
                    ROOT.performMove(position);
                }
                repaint();
            }

            // Drag and Drop ends
            @Override
            public void mouseReleased(MouseEvent me) {
                drag = false;
                if (lastEntered != null && lastEntered.isDropAcceptor()) {
                    ROOT.performMove(lastEntered.getPosition());
                }
                draggable.setVisible(false);
                repaint();
                ROOT.setCursor(0);
                ROOT.validate();
            }

            // Mouse enters button
            @Override
            public void mouseEntered(MouseEvent me) {
                if (enabled || drag) {
                    mouseOver = contains(me.getX(), me.getY());
                }
                if (mouseOver && drag) {
                    lastEntered = BUTTON;
                    if (!dropAcceptor || selected) {
                        ROOT.setCursor(2);
                    }
                }
                repaint();
            }

            // Mouse exits button
            @Override
            public void mouseExited(MouseEvent me) {
                if (mouseOver) {
                    mouseOver = false;
                    if (drag && !dropAcceptor) {
                        ROOT.setCursor(1);
                    }
                }
                repaint();
            }

            // Drag and Drop starts
            @Override
            public void mouseDragged(MouseEvent me) {
                if (selected) {
                    draggable.sendSource(BUTTON);
                    draggable.setLocation(getX() + me.getX(),
                            getY() + me.getY());
                    if (!drag) {
                        drag = true;
                        draggable.setVisible(true);
                        ROOT.setCursor(1);
                    }
                    ROOT.validate();
                }
            }
        };
    }

    /* Bounds methods */

    /**
     * Returns the diameter of this round button.
     *
     * @return Diameter as {@code int}.
     */
    private int getDiameter() {
        return Math.min(getWidth(), getHeight());
    }

    /**
     * Method to paint this Component, called automatically when program begins,
     * when the window is refreshed and when {@code repaint} is invoked.
     *
     * @param g Graphics object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        int diameter = getDiameter();
        int radius = diameter / 2;
        Graphics2D g2 = (Graphics2D) g;
        // Button color and behavior
        if (selected) {
            getPaint(g2, BUTTON_SELECTED);
        } else {
            if (owner == Owner.EMPTY) {
                getPaint(g2, EMPTY_SPACE);
            } else if (owner == Owner.PLAYER_WHITE) {
                getPaint(g2, WHITE_PLAYER);
            } else {
                getPaint(g2, BLACK_PLAYER);
            }
        }
        g.fillOval(getWidth() / 2 - radius, getHeight() / 2 - radius,
                diameter, diameter);
        // Border color and behavior
        if (mouseOver) {
            g2.setColor(MOUSE_OVER_HIGHLIGHT);
        } else {
            g2.setColor(BUTTON_BORDER);
        }
        g2.drawOval(getWidth() / 2 - radius, getHeight() / 2 - radius,
                diameter, diameter);
        g2.setColor(BUTTON_BORDER);
    }

    /**
     * Returns the preferred size of this round button.
     *
     * @return Preferred size as {@code Dimension}.
     */
    @Override
    public Dimension getPreferredSize() {
        FontMetrics metrics = getGraphics().getFontMetrics(getFont());
        int minDiameter = 10 + Math.max(metrics.stringWidth(getText()),
                metrics.getHeight());
        return new Dimension(minDiameter, minDiameter);
    }

    /* Visualization methods */

    /**
     * Checks whether the mouse is within the area of this Button.
     *
     * @return {@code true} if mouse action is within this button, otherwise
     * {@code false}.
     */
    @Override
    public boolean contains(int x, int y) {
        double radius = getDiameter() / 2;
        return Point2D.distance(x, y, getWidth() / 2,
                getHeight() / 2) < radius;
    }

    /**
     * Enables 3D rendering of buttons to appear rounded either concave or
     * convex. Adapted from {@link "http://www.java2s.com/"}.
     *
     * @param g2    Graphics variable g is cast in the {@code paintComponent}
     *              method to enable better graphics control.
     * @param color List of colors that will be highlights and low lights for
     *              different parts of this button.
     */
    private void getPaint(Graphics2D g2, Color[] color) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color[0]);
        // Fills circle
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        Paint p;
        // Shadow at top
        p = new GradientPaint(0, 0, color[1], 0, getHeight(), color[2]);
        g2.setPaint(p);
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        // Highlight at bottom
        p = new GradientPaint(0, 0, color[3], 0, getHeight(), color[4]);
        g2.setPaint(p);
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        // Creates dark edges for 3D effect
        p = new RadialGradientPaint(
                new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0),
                getWidth() / 2.0f, new float[]{0.0f, 1.0f},
                new Color[]{color[5], color[6]});
        g2.setPaint(p);
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        // Adds oval inner highlight at the bottom
        p = new RadialGradientPaint(
                new Point2D.Double(getWidth() / 2.0, getHeight() * 1.5),
                getWidth() / 2.3f,
                new Point2D.Double(getWidth() / 2.0, getHeight() * 1.75 + 6),
                new float[]{0.0f, 0.8f}, new Color[]{color[7], color[8]},
                RadialGradientPaint.CycleMethod.NO_CYCLE,
                RadialGradientPaint.ColorSpaceType.SRGB,
                AffineTransform.getScaleInstance(1.0, 0.5));
        g2.setPaint(p);
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
        // Adds oval specular highlight at the top left
        p = new RadialGradientPaint(
                new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0),
                getWidth() / 1.4f, new Point2D.Double(45.0, 25.0),
                new float[]{0.0f, 0.5f}, new Color[]{color[9], color[10]},
                RadialGradientPaint.CycleMethod.NO_CYCLE);
        g2.setPaint(p);
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
    }

    /**
     * Enables TreeSet mapping of buttons. Compares this RoundButton with
     * specified RoundButton for order.
     */
    @Override
    public int compareTo(RoundButton o) {
        int x1 = this.position.getX();
        int y1 = this.position.getY();
        int x2 = o.getPosition().getX();
        int y2 = o.getPosition().getY();
        if (x1 == x2) {
            return Integer.compare(y1, y2);
        } else if (x1 < x2) {
            return y1 < y2 ? -1 : 1;
        } else {
            return y1 > y2 ? -1 : 1;
        }
    }

    /**
     * Generated Hashcode for button class used in HashMap in
     * {@code AbaloneView}.
     *
     * @return hashcode as {@code int}.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((position == null) ? 0 : position.hashCode());
        return result;
    }

    /**
     * Generated equals method.
     *
     * @param o Object being checked for equality.
     * @return {@code True} if Object is equal to this RoundButton.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoundButton otherButton = (RoundButton) o;
        return this.position == otherButton.position;
    }

}
