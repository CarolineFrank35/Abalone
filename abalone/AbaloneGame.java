package abalone;

import java.util.Iterator;
import java.util.Observable;

import abalone.HexCell.Owner;

//observe whether the game has been won
//size = 7 -> balls = 11 -> spaces = 37
//size = 9 -> balls = 14 -> spaces = 61
//size = 11 -> balls = 17 
//Controller
public class AbaloneGame extends Observable {
    
    private int size;
    private AbaloneBoard board;
    private HexCell.Owner activePlayer = HexCell.Owner.EMPTY;
    private boolean onGoing = false;
    private static String player1;
    private static String player2;
    private static int piecesToWin;
    private Move move;
    
    public AbaloneGame(final int s, String name1, String name2) {
        player1 = name1;
        player2 = name2;
        size = s;
        piecesToWin = 2 * size / 3;
        board = new AbaloneBoard(s);
        onGoing = true;
        activePlayer = HexCell.Owner.PLAYER_BLACK;
        //move = new Move(board, activePlayer);
    }
        
    //todo
    private boolean validateMove(Coord coord, boolean add) {
        boolean isValid = false;
        HexCell cell = board.getCell(coord); 
        if (add && move.getMoves().isEmpty()) { // first move
            if (move.validMove().contains(coord)) {
                move.addMove(coord);    
                isValid = true;
            }
        } else if (add) {
            if (move.isValid(coord)) {
                if (cell.getOwner() == activePlayer && move.getMoves().size() < 3) {
                    move.addMove(coord);
                    isValid = true;
                } else if (cell.getOwner() != activePlayer) {
                    //do move                
                }
            }
        } else if (!add) {
            if (move.getMoves().contains(coord)) {
                move.removeMove(coord);
                isValid = true;
            }
        }
     return isValid;
    }
    
    public void nextPlayer() {
        if (activePlayer.name().equals("PLAYER_BLACK")) {
            activePlayer = Owner.PLAYER_WHITE;
            move = new Move(board, activePlayer);
        } else if (activePlayer.name().equals("PLAYER_WHITE")) {
            activePlayer = Owner.PLAYER_BLACK;
            move = new Move(board, activePlayer);
        }
    }
    
    public AbaloneBoard getBoard() {
        return board;
    }

    public int getSize() {
        return size;
    }

    public boolean winningMove() {
        boolean won = false;
        int winningNumber = (int) (2 * size / 3.0);
        if (true) { // to do
            won = true;
            setChanged();
            notifyObservers();
        }
        return won;
    }
    
    public Iterator<HexCell> getIterator() {        
        return board.getGrid().iterator();
    }

    public HexCell.Owner getActivePlayer() {
        return activePlayer;
    }
    
    
}
