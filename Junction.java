import java.util.ArrayList;

public class Junction {

    private double x, y;
    private ArrayList<Street> streets;

    public Junction(double coordx, double coordy)   {
        streets = new ArrayList<Street>();
        streets.clear();
        this.x = coordx;
        this.y = coordy;
    }

    public void addStreet(Street s)  {
        streets.add(s);
    }

    public ArrayList<Street> getStreets(){
        return streets;
    }

    public void print() {
        System.out.println("X : " + x);
        System.out.println("Y : " + y);
        System.out.println("StreetSIze: " + streets.size());
        for(Street s : streets)
            s.print();
    }

    public double getX()    {
        return x;
    }

    public double getY()    {
        return y;
    }


    
}
