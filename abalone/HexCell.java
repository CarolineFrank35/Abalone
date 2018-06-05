package abalone;

import java.util.Observable;

//Model
public class HexCell extends Observable {
    
    public enum Owner {
        EMPTY,
        PLAYER_WHITE, 
        PLAYER_BLACK
        };

    private final Coord coord;
    private Owner owner;

    public HexCell(int x, int y, Owner o) {
        coord = new Coord(x, y);
        owner = o;
    }
    
    public HexCell(int x, int y, int z, Owner o) {
        coord = new Coord(x,y,z);
        owner = o;
    }
    
    public Coord getPosition() {
        return this.coord;
    }
    
    public boolean isEmpty() {
        return owner.name().equals("EMPTY");
    }
    
    public Owner getOwner() {
        return owner;
    }
    
    public void setOwner(Owner changeTo) {
        owner = changeTo;
        setChanged();
        notifyObservers();
    }
    
    @Override
    public boolean equals(Object o) {
        if(o.getClass() == HexCell.class) {
            return this.getPosition().equals(((HexCell) o).getPosition());
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return "(" + coord.getX() + "," + coord.getY() + ")";
    }

}
