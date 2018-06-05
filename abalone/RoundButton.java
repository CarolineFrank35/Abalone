package abalone;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import abalone.HexCell.Owner;
 
public class RoundButton extends JButton
                        implements Observer {
 
    /**
     * 
     */
    private static final long serialVersionUID = -678970048927599810L;

    final static Color COLORBLACKPLAYER = Color.BLACK;
    final static Color COLORWHITEPLAYER = Color.WHITE;
    final static Color COLOREMPTY = Color.GRAY;
    final static Color MOUSEOVERHIGHLIGHT = Color.MAGENTA;
    final static Color BUTTONBORDER = Color.BLACK;
    final static Color BUTTONPRESSED = Color.LIGHT_GRAY;

    private boolean mouseOver = false;
    private boolean mousePressed = false;
    private Owner owner;
    private HexCell cell;

    public RoundButton(HexCell c){
        super();
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        cell = c;
        owner = cell.getOwner();
        cell.addObserver((o,arg) -> {
                arg = cell.getOwner();
                if (owner != arg) {
                    owner = (Owner) arg;
                    this.repaint();
                }
            });
        
        MouseAdapter mouseListener = new MouseAdapter(){
            
            @Override
            public void mousePressed(MouseEvent me){
                if(contains(me.getX(), me.getY())){
                    mousePressed = !mousePressed;
                    repaint();
                    Coord coord = new Coord(me.getX(), me.getY());
                    AbaloneView.moveAt(coord, mousePressed);                  
                }
            }
            /*
            @Override
            public void mouseReleased(MouseEvent me){
                mousePressed = false;
                repaint();
            } */
            
            @Override
            public void mouseExited(MouseEvent me){
                mouseOver = false;
                repaint();
            }
            
            @Override
            public void mouseMoved(MouseEvent me){
                mouseOver = contains(me.getX(), me.getY());
                repaint();
            }   
        };

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);      
    }
    
    private int getDiameter(){
        int diameter = Math.min(getWidth(), getHeight());
        return diameter;
    }
    
    @Override
    public Dimension getPreferredSize(){
        FontMetrics metrics = getGraphics().getFontMetrics(getFont());
        int minDiameter = 10 + Math.max(metrics.stringWidth(getText()), metrics.getHeight());
        return new Dimension(minDiameter, minDiameter);
    }
    
    @Override
    public boolean contains(int x, int y){
        int radius = getDiameter()/2;
        return Point2D.distance(x, y, getWidth()/2, getHeight()/2) < radius;
    }
    
    @Override
    public void paintComponent(Graphics g){
        
        int diameter = getDiameter();
        int radius = diameter/2;
        
        if(mousePressed){
            g.setColor(BUTTONPRESSED);
        } else {
            if (owner == HexCell.Owner.EMPTY) {
                g.setColor(COLOREMPTY);
            } else if (owner == HexCell.Owner.PLAYER_WHITE) {
                g.setColor(COLORWHITEPLAYER);
            } else {
                g.setColor(COLORBLACKPLAYER);
            }            
        }
        g.fillOval(getWidth()/2 - radius, getHeight()/2 - radius, diameter, diameter);
        
        if(mouseOver){
            g.setColor(MOUSEOVERHIGHLIGHT);
        }
        else{
            g.setColor(BUTTONBORDER);
        }
        g.drawOval(getWidth()/2 - radius, getHeight()/2 - radius, diameter, diameter);
        
        g.setColor(BUTTONBORDER);
        g.setFont(getFont());
        FontMetrics metrics = g.getFontMetrics(getFont());
        int stringWidth = metrics.stringWidth(getText());
        int stringHeight = metrics.getHeight();
        g.drawString(getText(), getWidth()/2 - stringWidth/2, getHeight()/2 + stringHeight/4);
    }
    
    public void update(Observable o, Object arg) {
        
    }

}