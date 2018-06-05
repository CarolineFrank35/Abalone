package abalone;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import abalone.HexCell.Owner;


//view
public class AbaloneView extends JPanel
                        implements Observer {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    final static Color BACKGROUND = Color.GRAY;

    private Hexagon hexagon;
    private static AbaloneGame game;
    private JPanel view;
    private int size;
    public static Layout layout;
    public Observer observer;
    private HexCell.Owner owner;
    private Map<Coord,JButton> bmap;
    
    public AbaloneView() {
        super(null);
        this.setBackground(BACKGROUND);
        //size = (int) (Math.random() * 10);
        //size += (size % 2 == 0 ? 1 : 0); // only odds
        size = 9;
        game = new AbaloneGame(9, "", ""); //starts game -- must change to size when done and add player1 + player2 names
        hexagon = new Hexagon(new Point(315,315), 300);
        layout = new Layout(Orientation.pointy(), 36, 300);
        this.setPreferredSize(new Dimension(600, 600));
        bmap = new HashMap<>();
        createGrid();
    }

    private JButton getButton(Coord c) {
        JButton button = null;
        if (bmap.containsKey(c)) {
            button = bmap.get(c);
        }
        return button;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(hexagon.getColor());
        g2.drawPolygon(hexagon.getHexagon());
        g2.fillPolygon(hexagon.getHexagon());
    }
    
    private void createGrid() {
        Iterator<HexCell> it = game.getIterator();
        while(it.hasNext()) {
            HexCell cell = it.next();
            JButton b = new RoundButton(cell);
            Coord c = layout.hexToPixel(layout, cell.getPosition());
            b.setBounds(c.getX(), c.getY(), 38, 38);
            this.add(b);
            bmap.put(c, b);
        }
        this.revalidate();
        this.repaint();
    }
    
    public HexCell.Owner getPlayer() {
        return game.getActivePlayer();
    }

    public static void moveAt(Coord clickCoord, boolean mousePressed) {
        Coord hexCoord = layout.pixelToHex(layout, clickCoord);
        //game.validateMove(hexCoord);
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO Auto-generated method stub
        
    }

}
