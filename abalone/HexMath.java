package abalone;

import java.util.ArrayList;
import java.util.List;

public class HexMath {
    public enum Direction {
        NW(HEX_DIRECTIONS[1]),
        NE(HEX_DIRECTIONS[0]),
        E(HEX_DIRECTIONS[5]),
        SE(HEX_DIRECTIONS[4]),
        SW(HEX_DIRECTIONS[3]),
        W(HEX_DIRECTIONS[2]);
        private Coord direction;
        
        Direction(Coord dir) {
            direction = dir;
        }
    
        public Coord getDirction() {
            return direction;
        }
    };
    
    private static final Coord[] HEX_DIRECTIONS = {
            new Coord(1,0,-1), new Coord(1,-1,0), new Coord(0,-1,1),
            new Coord(-1,0,1), new Coord(-1,1,0), new Coord(0,1,-1)
    };
    
    public static Coord direction(Coord a, Coord b) {
        Coord dir = hexSubtract(a,b);
        Coord goDirection = null;
        int x = dir.getX();
        int y = dir.getY();
        if(y == 0) {
            if (x < 0) {
                goDirection = HEX_DIRECTIONS[3];// west
            } else {
                goDirection = HEX_DIRECTIONS[0]; //east
            }
        } else if (y < 0) { //North
            if (x <= 0) {
                goDirection = HEX_DIRECTIONS[2]; // Northwest
            } else if (x > 0) {
                goDirection = HEX_DIRECTIONS[1]; //northeast
            }
        } else if (y > 0) {
            if (x >= 0) {
                goDirection = HEX_DIRECTIONS[5]; //southeast
            } else if (x < 0) {
                goDirection = HEX_DIRECTIONS[4]; //southwest
            }
        }
        return goDirection;
    }
    
    //Vector Addition
    public static Coord hexAdd(Coord a, Coord b) {
        return new Coord(a.getX() + b.getX(), a.getY() + b.getY(), a.getZ() + b.getZ());
    }
    
    //Vector Subtraction
    public static Coord hexSubtract(Coord a, Coord b) {
        return new Coord(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
    }
    
    //Scalar Multiplication
    public Coord hexMultiply(Coord a, int k) {
        return new Coord(a.getX() * k, a.getY() * k, a.getZ() * k);
    }
    
    public static int hexLength(Coord hex) {
        return (int) ((int) (Math.abs(hex.getX()) + Math.abs(hex.getY()) + Math.abs(hex.getZ())) / 2.0);
    }
    
    public static int hexDistance(Coord a, Coord b) {
        return hexLength(hexSubtract(a,b));
    }
    
    //direction can be 0-5
    public static Coord hexDirection(int direction) {
        if (0 <= direction || direction < 6) {
            return HEX_DIRECTIONS[direction];
        } else {
            return null;
        }        
    }
    
    public static List<Coord> hexNeighbor(Coord hex) {
        List<Coord> neighbors = new ArrayList<>();
        for (int i = 0; i <= 5; i++) {
            neighbors.add(hexAdd(hex, hexDirection(i)));
        }
        return neighbors;
    }
    
}
