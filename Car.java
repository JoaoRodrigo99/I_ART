import java.util.ArrayList;

public class Car {

  public Junction junction;
  public String path;
  public ArrayList<SubPath> path2 = new ArrayList<SubPath>();
  public int time = 0;

  public Car(Junction j) {
    this.junction = j;
    this.path = "";
  }

  public int numberJunctions() {
    return path.length();
  }

  public void reduceTime(int time2Reduce) {
    time -= time2Reduce;
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time2Set) {
    time = time2Set;
  }

  public void setPath(ArrayList<SubPath> auxP) {
    path2.clear();
    path2 = auxP;
  }

  public Junction getJunction() {
    return this.junction;
  }

  public void setJunction(Junction junc2Set) {
    junction = junc2Set;
  }
}
