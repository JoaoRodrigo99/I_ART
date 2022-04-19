import java.util.ArrayList;

public class Junction {

    private final double x;
    private final double y;
    private final ArrayList<Street> streets;

    public Junction(double x, double y) {
        streets = new ArrayList<>();
        this.x = x;
        this.y = y;
    }

    public void addStreet(Street s) {
        streets.add(s);
    }

    public ArrayList<Street> getStreets() {
        return streets;
    }

    public void print() {
        System.out.println("X : " + x);
        System.out.println("Y : " + y);
        System.out.println("StreetSIze: " + streets.size());
        for (Street s : streets)
            s.print();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


}
