package abalone;

import java.util.ArrayList;
import java.util.List;

//

public class Move {
    
    private List<Coord> currentMove;
    private boolean listChanged;
    private Coord direction;
    private static List<Coord> availableMoves;
    private static AbaloneBoard board;
    private static HexCell.Owner activePlayer;
    
    public Move(AbaloneBoard b, HexCell.Owner o) {
        activePlayer = o;
        currentMove = new ArrayList<>();
        direction = null;
        availableMoves = new ArrayList<>();
        for (HexCell c : board.getGrid()) {
            if (c.getOwner() == activePlayer) {
                availableMoves.add(c.getPosition());
            }
        }
        board = b;
    }
    
    public List<Coord> validMove() {
        return availableMoves;
    }
    
    public boolean addMove(Coord coord) {
        if (currentMove.isEmpty()) { //first move
            currentMove.add(coord);
            availableMoves.add(
                    HexMath.hexNeighbor(coord).stream().filter(x -> board.getCell(x) != null)); // add all neighbors that are not null
            availableMoves.stream().filter(x -> board.getCell(x).getOwner() == activePlayer)
                                       .filter(x -> HexMath.direction)
                        availableMoves.add(board.getGrid().stream().filter(x -> HexMath.hexDistance(coord,x.getPosition()) == 1));
            return true;
        } else if (currentMove.size() == 1 && !currentMove.contains(coord)) { // second move
            Coord a = currentMove.get(0);
            if (HexMath.hexDistance(a, coord) == 1) { // next to each other
                direction = HexMath.direction(a, coord);
                availableMoves = new ArrayList<>();
                if (board.getCell(HexMath.hexAdd(coord,direction)) != null) {
                    availableMoves.add(HexMath.hexAdd(coord,direction));
                }
                currentMove.add(coord);
                return true;
            } else if (HexMath.hexDistance(a, coord) > 2) { // too far
                return false;
            } else { // distance is two add the middle man
                currentMove.add(coord);
                currentMove.add(HexMath.hexAdd(coord, HexMath.direction(coord, a)));
                validMoves = new ArrayList<>();
                validMoves.
                currentMove.add();
                }
            } else {
                
            }
            currentMove.add(coord);
            listChanged = true;
        }
        
        return listChanged;
    }
    
    public void removeMove(Coord coord) {
        if (currentMove.contains(coord)) {
            currentMove.remove(coord);
            listChanged = true;
        }
    }
    
    public List<Coord> getMoves() {
        return currentMove;
    }

    
    public boolean isValid(Coord coord) {
        for 
        HexMath.hexDistance();
        if () { //valid
            currentMove.add(coord);
            return true;
        } else {
            
        }
    }
}
