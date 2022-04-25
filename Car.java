import java.util.ArrayList; 
public class Car {

    public Junction junction;
    public ArrayList<SubPath> path = new ArrayList<SubPath>();
    public int time = 0;

    public Car(Junction j) {
        this.junction = j;
        this.path = new ArrayList<SubPath>();
    }

    public Car(Junction j, ArrayList<SubPath> path2, int time2) {
        this.junction = j;
        this.path = path2;
        this.time = time2;

    }

    @SuppressWarnings("unchecked")
    public Car copy()   {
        Car newC = new Car(junction);
        newC.path = (ArrayList<SubPath>) path.clone();
        newC.time = this.time;

        return newC;
    }

    public int getDist(ArrayList<Junction>junctionsAux, ArrayList<Street>streetsAux)    {
        int prevJ = -1;
        int dist = 0;
        for (SubPath sp : this.path) {
            if (prevJ == -1) prevJ = sp.getJunction();
            else {
                for (Street s : streetsAux) {
                    if ((junctionsAux.indexOf(s.getJunction(1)) == prevJ) && (junctionsAux.indexOf((s.getJunction(2))) == sp.getJunction()))
                        dist += s.getDistance();
                    else if ((junctionsAux.indexOf(s.getJunction(2)) == prevJ) && (junctionsAux.indexOf((s.getJunction(1))) == sp.getJunction()) && s.getDirection() == 2)
                    dist += s.getDistance();
                }
                prevJ = sp.getJunction();
            }

        }
        return dist;
    }

    public ArrayList<SubPath> getPath(){
        return path;
    }

    public int numberJunctions() {
        return path.size();
    }

    public void setTime(int time2Set){
        time = time2Set;
    }

    public void reduceTime(int time2Reduce){
        time -= time2Reduce;
    }

    public int getTime(){
        return time;
    }

    public void setPath(ArrayList<SubPath> auxP){
        path.clear();
        path = auxP;
    }

    public void setJunction(Junction junc2Set){
        junction = junc2Set;
    }

    public Junction getJunction(){
        return this.junction;
    }
}