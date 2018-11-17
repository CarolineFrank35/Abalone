package de.lmu.ifi.sep.abalone.views;

import de.lmu.ifi.sep.abalone.components.GameObserver;
import de.lmu.ifi.sep.abalone.components.Vector;
import de.lmu.ifi.sep.abalone.logic.AbaloneGame;
import de.lmu.ifi.sep.abalone.logic.communication.Move;
import de.lmu.ifi.sep.abalone.models.AbaloneBoard;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

/**
 * GUI of Abalone Game and the controller of all View interactions.
 * <p>
 * Implements observer pattern to observe {@code AbaloneBoard} class for
 * changes to game spaces and relays all information to corresponding buttons.
 * <\p>
 * <p>
 * This class also implements drag and drop functionality by registering as a
 * {@code GameObserver} and receiving information such as turn changes,
 * available spaces and available moves. At each notification manages button
 * responses such as enabling/disabling responses and becoming a dropAcceptor.
 */
public class AbaloneView extends JPanel {

    /**
     * Identifies the semantics of being serializable and allows sender and
     * receiver to verify that they have compatible versions of this object in
     * respect to serialization.
     */
    private static final long serialVersionUID = -5062393989853912065L;

    /* Cursor Options */
    /**
     * Default cursor when no drag and drop operation is underway.
     */
    private static final Cursor DEFAULT = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

    /**
     * Cursor during drag and drop operation.
     */
    private static final Cursor DROP = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

    /**
     * Cursor during drag and drop operation signifying that button under
     * cursor is not a drop acceptor.
     */
    private static final Cursor NOT_DROP = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    /* Reference variables */
    /**
     * Outline of the game board shaped as a hexagon.
     */
    private Polygon hexagon;

    /**
     * Instance of AbaloneBoard which is the model holding game pieces.
     */
    private final AbaloneBoard board;

    /**
     * Instance of AbaloneGame which is the controller for the game logic.
     */
    private final AbaloneGame abaloneGame;

    /**
     * Mapping of all buttons on GUI Abalone board with their corresponding
     * Vector coordinates on the AbaloneBoard.
     */
    private HashMap<Vector, RoundButton> allButtons;

    /**
     * TreeSet of currently selected buttons in sorted order.
     */
    private Set<RoundButton> selectedButtons = new TreeSet<>();

    /**
     * Constructor for this JPanel containing the AbaloneGame GUI.
     *
     * @param game AbaloneGame which is the controller in the MVC architecture.
     */
    public AbaloneView(final AbaloneGame game) {
        super(null);
        this.setOpaque(false);
        this.setCursor(DEFAULT);
        abaloneGame = game;
        board = game.getBoard();
        initGameView();
        addObservers();
    }

    /**
     * Utility method to keep track of User selected RoundButtons on the game
     * field using a TreeSet as the data structure. Calls the method
     * {@code sendMove}.
     *
     * @param button Button that will be added or removed.
     */
    public void addSelectedButton(RoundButton button) {
        if (selectedButtons.contains(button) && !button.isSelected()) {
            selectedButtons.remove(button);
        } else {
            selectedButtons.add(button);
        }
        sendMove();
    }

    /**
     * Utility method that allows buttons set cursor according to Drag and drop.
     *
     * @param cursor Predefined Cursor as {@code int}.
     */
    public void setCursor(int cursor) {
        if (cursor == 1) {
            this.setCursor(DROP);
        } else if (cursor == 2) {
            this.setCursor(NOT_DROP);
        } else {
            this.setCursor(DEFAULT);
        }
    }

    /**
     * Passes selected buttons to Game Logic to handle according to game rules.
     */
    private void sendMove() {
        List<Vector> toSend = new ArrayList<>();
        selectedButtons.stream().map(RoundButton::getPosition).forEach(toSend::add);
        abaloneGame.isValidMove(new Move(toSend, null));
    }

    /**
     * Called when user selects a button that is a drop acceptor either by
     * clicking the button or releasing a drag and drop motion on button.
     *
     * @param target The destination of the move.
     */
    void performMove(Vector target) {
        List<Vector> toSend = new ArrayList<>();
        selectedButtons.stream().map(RoundButton::getPosition).forEach(toSend::add);
        abaloneGame.isValidMove(new Move(toSend, target));
    }

    /**
     * Returns User selected buttons as a {@code List}, each button represents
     * a game space.
     *
     * @return {@code List} of User selected RoundButtons.
     */
    public List<RoundButton> getSelected() {
        return new ArrayList<>(selectedButtons);
    }

    /**
     * Accessor method to return button associated with {@code Vector} on the
     * game board.
     *
     * @param key {@code Vector} on this Game board.
     * @return Button associated with the vector on the game board.
     */
    public RoundButton getButton(Vector key) {
        return allButtons.get(key);
    }

    /* Visual methods and initialization */

    /**
     * Instantiates Abalone Game GUI inside of this JPanel.
     */
    private void initGameView() {
        int gameSize = board.getSize();
        int panelSize = 50 * gameSize;
        int center = (int) (53 - (gameSize * 5) + panelSize / Math.sqrt(3));
        int hexCellSize = panelSize / (int) (Math.sqrt(3) * gameSize);
        this.setPreferredSize(new Dimension(panelSize, panelSize));
        // Defines spaces layout
        Layout layout = new Layout(hexCellSize, panelSize / 2);
        // Creates board background
        hexagon = createHexagon(center, 25 + panelSize / 2);
        DraggableJPanel draggable = new DraggableJPanel(this, hexCellSize + 4);
        draggable.setVisible(false);
        this.add(draggable);
        allButtons = new HashMap<>();
        // Creates Grid with spaces as RoundButtons
        for (Entry<Vector, AbaloneBoard.Owner> e : board.getBoard().entrySet()) {
            RoundButton button = new RoundButton(e.getValue(), draggable, this, e.getKey());
            allButtons.put(e.getKey(), button);
            Vector v = layout.hexToPixel(e.getKey());
            button.setBounds(v.getX() + 30, v.getY() + 30, hexCellSize + 4, hexCellSize + 4);
            this.add(button);
        }
        this.repaint();
        this.validate();
    }

    /**
     * Adds Abalone View as a observer of the AbaloneBoard and AbaloneGame and
     * implements methods of the corresponding observers.
     */
    private void addObservers() {
        /* Board Observer */
        board.addObserver((v, o) -> allButtons.get(v).updateOwner(o));
        /* Game Observer */
        abaloneGame.addObserver(new GameObserver() {

            @Override
            public void setValidMoves(List<Vector> validMoves) {
                // Flag valid moves as drop acceptors for DnD
                Runnable valid = () -> validMoves.stream()
                        .map(v -> allButtons.get(v))
                        .forEach(b -> b.setDropAcceptor(true));
                SwingUtilities.invokeLater(valid);
            }

            @Override
            public void setValidClicks(final List<Vector> validClicks) {
                Runnable valid = () -> {
                    for (Entry<Vector, RoundButton> entry : allButtons.entrySet()) {
                        if (validClicks.contains(entry.getKey())) {
                            entry.getValue().setEnabled(true); // Selectable
                        } else {
                            entry.getValue().setEnabled(false); // Not selectable
                            entry.getValue().setDropAcceptor(false); // Selectable
                        }
                    }
                };
                SwingUtilities.invokeLater(valid);
            }

            @Override
            public void endTurn(AbaloneBoard.Owner o) {
                selectedButtons = new TreeSet<>();
                Runnable valid = () -> {
                    for (Entry<Vector, RoundButton> entry : allButtons.entrySet()) {
                        RoundButton b = entry.getValue();
                        b.setEnabled(false); // Not selectable
                        b.setSelected(false); // Not selected
                        b.setDropAcceptor(false); // Not a drop acceptor for DnD
                    }
                };
                SwingUtilities.invokeLater(valid);

            }
        });
    }

    /**
     * Method to paint this Component, called automatically when program
     * begins, when the window is refreshed and when {@code repaint} is invoked.
     *
     * @param g Graphics object, cast to a Graphics2D object.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(102, 51, 0));
        g2.drawPolygon(hexagon);
        g2.fillPolygon(hexagon);
    }

    /**
     * Creates {@code Polygon} background object shaped as a Hexagon.
     *
     * @param center Center of this Hexagon determined by panel size.
     * @param radius Radius of this Hexagon determined by panel size.
     * @return Specifications for Hexagon shape as {@code Polygon}.
     */
    private Polygon createHexagon(final int center, final int radius) {
        Polygon polygon = new Polygon();
        for (int i = 0; i < 6; i++) {
            int x = center + (int) (radius * Math.cos(i * 2 * Math.PI / 6D));
            int y = center + (int) (radius * Math.sin(i * 2 * Math.PI / 6D));
            polygon.addPoint(x, y);
        }
        return polygon;
    }

}
