public class Car {

    public Junction junction;
    public String path;

    public Car(Junction j) {
        this.junction = j;
        this.path = "";
    }

    public int numberJunctions() {
        return path.length();
    }

}