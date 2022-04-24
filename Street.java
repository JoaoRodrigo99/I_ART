import java.util.ArrayList;

public class Street {
    public class visitInfo {
        private final int time;
        private final int carID;
        public visitInfo(int carID, int time) {
            super();
            this.carID = carID;
            this.time = time;
        }

        public int getTime() {
            return time;
        }

        public int getCarID() {
            return carID;
        }
    }

    private final Junction junction1;
    private final Junction junction2;
    private final int distance;
    private final int time;
    private final int direction;
    private boolean visited;
    private final ArrayList<visitInfo> visitedTimes = new ArrayList<>();

    public Street(Junction junction1, Junction junction2, int direction, int time, int distance) {
        this.junction1 = junction1;
        this.junction2 = junction2;
        this.direction = direction;
        this.time = time;
        this.distance = distance;

    }

    public void setVisited(int carID, int timeOfVisit) {
        visited = true;
        visitedTimes.add(new visitInfo(carID, timeOfVisit));
    }

    public void unVisit() {
        visited = false;
        visitedTimes.clear();
    }

    public ArrayList<visitInfo> getVisitedTimes() {
        return visitedTimes;
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
