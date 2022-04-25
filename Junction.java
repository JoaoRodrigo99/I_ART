import java.util.ArrayList;

public class Junction {

    private final double x;
    private final double y;
    private ArrayList<Street> streets;

    public Junction(double x, double y) {
        streets = new ArrayList<>();
        this.x = x;
        this.y = y;
    }

    public Junction(double x, double y, ArrayList<Street> sts) {
        streets = new ArrayList<>();
        this.x = x;
        this.y = y;
        streets = sts;
    }

    @SuppressWarnings("unchecked")
    public Junction copy(){
        Junction newJ = new Junction(x, y);
        newJ.streets = (ArrayList<Street>) streets.clone();

        return newJ;
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
