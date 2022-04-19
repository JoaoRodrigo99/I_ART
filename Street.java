public class Street {


    private Junction junction1, junction2;
    private int distance;
    private int time;
    private int direction;
    private boolean visited;

    public Street(Junction junction1, Junction junction2, int direction, int time, int distance) {
        this.junction1 = junction1;
        this.junction2 = junction2;
        this.direction = direction;
        this.time = time;
        this.distance = distance;

    }

    public void setVisited() {
        visited = true;
    }

    public boolean isVisited() {
        return visited;
    }

    public int getDirection() {
        return direction;
    }

    public int getTime() {
        return time;
    }

    public int getDistance() {
        return distance;
    }

    public Junction getJunction(int n) {
        switch (n) {
            case 1:
                return junction1;
            case 2:
                return junction2;
            default:
                System.out.println("Error getting juntion (Street)");
                return null;
        }
    }

    public void print() {
        System.out.println("junction1 : " + junction1);
        System.out.println("junction2 : " + junction2);
        System.out.println("distance : " + distance);
        System.out.println("time : " + time);
        System.out.println("direction : " + direction);

    }
}
