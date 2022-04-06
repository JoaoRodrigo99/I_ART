public class Street {
    

    private Junction junction1, junction2;
    private int distance;
    private int time;
    private int direction;
    private boolean visited;

    public Street(Junction j1, Junction j2, int direc, int tim, int dist) {
        this.junction1 = j1;
        this.junction2 = j2;
        this.direction = direc;
        this.time = tim;
        this.distance = dist;

    }

    public void setVisited(){
        visited = true;
    }

    public boolean isVisited(){
        return visited;
    }

    public int getDirec(){
        return direction;
    }

    public int getTime(){
        return time;
    }

    public int getDist(){
        return distance;
    }

    public Junction getJ(int n){
        if(n == 1)
            return junction1;
        else if(n == 2)
            return junction2;
        else 
            System.out.println("Error getting juntion (Street)");
            return null;
    }

    public void print() {
        System.out.println("junction1 : " + junction1);
        System.out.println("junction2 : " + junction2);
        System.out.println("distance : " + distance);
        System.out.println("time : " + time);
        System.out.println("direction : " + direction);
    
    }
}
