package abalone;

//Helper class to convert from hex coordinates to screen coord
public class Orientation {
    public static Orientation orientation;
    private double f0;
    private double f1;
    private double f2;
    private double f3;
    private double b0,b1,b2,b3;
    //with flat orienation the corners are at 0,60,120,180,240,300
    //with pointy they are at 30, 90, 150, 210, 270, 330
    private double startAngle; //multiples of 60 
    
    private Orientation(double f0, double f1, double f2, double f3,
            double b0, double b1, double b2, double b3, 
            double startAngle) {
        this.f0 = f0;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.b0 = b0;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.startAngle = startAngle;
    }
    
    public static Orientation pointy() {
        if (orientation == null) {
            return new Orientation(Math.sqrt(3.0), Math.sqrt(3.0) / 2.0, 0.0, 3.0 / 2.0,
                    Math.sqrt(3.0) / 3.0, -1.0 / 3.0 , 0.0 , 2.0 / 3.0,
                    0.5); // start angel is 0.5 for pointy
        } else {
            return orientation;
        }
        
    }
    
    public static Orientation flat() {
        if (orientation == null) {
            return new Orientation(3.0 / 2.0, 0.0, Math.sqrt(3.0) / 2.0, Math.sqrt(3.0),
                2.0 / 3.0, 0.0 , -1.0 / 3.0, Math.sqrt(3.0) / 3.0,
                0.0); // start angel is 0.0 for flat
        } else {
            return orientation;
        }
    }
    
    public double getAngle() {
        return startAngle;
    }
    
    public double getb0() {
        return b0;
    }
    
    public double getb1() {
        return b1;
    }
    
    public double getb2() {
        return b2;
    }
    
    public double getb3() {
        return b3;
    }

    public double getf0() {
        return f0;
    }
    
    public double getf1() {
        return f1;
    }
    
    public double getf2() {
        return f2;
    }
    
    public double getf3() {
        return f3;
    }
    
}
