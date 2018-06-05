package abalone;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

//Model
public class AbaloneBoard {

    private Map<Coord,HexCell> board;
    private final int size;
    //HexCell[][] boardAsArray;
    
    //Hex
    public AbaloneBoard(final int s) {
        size = s;
        board = initialize();
        //boardAsArray = initializeAsArray();
    }

    private Map<Coord,HexCell> initialize() {       
        Map<Coord,HexCell> grid = new LinkedHashMap<>();
        int radius = size / 2;
        int pred1 = radius - 2;
        HexCell.Owner o = null;
        for (int y = -radius; y <= radius; y++) {
            int x1 = Math.max(-radius, -y - radius);
            int x2 = Math.min(radius, -y + radius);
            for (int x = x1; x <= x2; x++) {
                boolean pred2 = x > -1 && x < radius - 1;
                boolean pred3 = x < 1 && x > -radius + 1;
                if (y < -pred1 || (y == -pred1 && pred2)) {
                    o = HexCell.Owner.PLAYER_WHITE;
                } else if (y > pred1 || (y == pred1 && pred2)) {
                    o = HexCell.Owner.PLAYER_BLACK;                 
                } else {
                    o = HexCell.Owner.EMPTY; 
                }
                grid.put(new Coord(x,y,-y-x), new HexCell(x,y,-y-x, o));
            }
        }
        return grid;
    } 
    
    public Collection<HexCell> getGrid() {
        return board.values();
    }
    
    public int getSize() {
        return size;
    }
    
    public HexCell getCell(Coord c) {
        HexCell cell = null;
        if (board.containsKey(c)) {
            cell = board.get(c);
        }
        return cell;
    }
    
    /* depreciated
    private HexCell[][] initializeAsArray() {
        HexCell[][] game = new HexCell[size][]; // creating a jagged array
        int half = (int) Math.ceil(size / 2.0);
        for (int y = 0; y < half; y ++) {
            for (int x = half; x <= size; x++) {
                game[y] = new HexCell[x];
                for (int q = 0; q < x; q++) {
                    game[y][q] = new HexCell(q, y, -q-y, null);
                } //creates rows 0 to 4
            }
        }
        for (int y = half; y < size; y++) {
            for (int x = size - 1; x >= half; x--) {
                game[y] = new HexCell[x];
                for (int q = 0; q < x; q++) {
                    game[y][q] = new HexCell(q, y, - q - y, null);
                }
            }
        }
        return game;
    }
    
    public HexCell[][] getGameBoard() {
        return boardAsArray;
    }
    */

}
