package abalone;

public class Layout {
    private final Orientation orientation; //angel
    private final Coord size;
    private final Coord origin;
    
    /**
     * 
     * @param o
     * @param s
     * @param org
     */
    public Layout(Orientation o, int s, int org) {
        orientation = o;
        size = new Coord(s,s);
        origin = new Coord(org,org);
    }
    
    //matrix multiplication (pointy top equation)
    // | x |          | sqrt(3)   sqrt(3)/2 |   | q|
    // | y | = size * | 0              3/2  | * | r|
    public Coord hexToPixel(Layout l, Coord h) {
        Orientation o = getOrientation();
        double x = (o.getf0() * h.getX() + o.getf1() * h.getY()) * size.getX();
        double y = (o.getf2() * h.getX() + o.getf3() * h.getY()) * size.getY();
        return new Coord((int) (x + origin.getX()),(int) (y + origin.getY()));
    }
    
    //this is matrix multiplication (pointy top equation)
    // | q |   |  b0     b1 |   | x|
    // | r | = |  b2     b3 | * | y| / size
    public Coord pixelToHex(Layout l, Coord p) {
        Orientation o = getOrientation();
        Coord pt = new Coord(p.getX() - origin.getX() / size.getX(),
                p.getY() - origin.getY() / size.getY());
        double q = o.getb0() * pt.getX() + o.getb1() * pt.getY();
        double r = o.getb2() * pt.getX() + o.getb3() * pt.getY();
        // since we can;t guarentee the click will be in the center
        // need to convert from fractional double to int
        int qRound = (int) Math.round(q);
        int rRound = (int) Math.round(r);
        int sRound = (int) Math.round(-q-r);
        double qdiff = (int) Math.abs(qRound - q);
        double rdiff = (int) Math.abs(rRound - r);
        double sdiff = (int) Math.abs(sRound - (-q-r));
        if (qdiff > rdiff && qdiff > sdiff) {
            qRound = -rRound - sRound;
        } else if (rdiff > sdiff) {
            rRound = -qRound - sRound;
        } else {
            sRound = -qRound - rRound;
        }
        return new Coord(qRound, rRound, sRound);
    }
    
    public Orientation getOrientation() {
        return orientation;
    }
    
    
    public Coord getSize() {
        return size;
    }
    
    public Coord getOrigin() {
        return origin;
    }

}
