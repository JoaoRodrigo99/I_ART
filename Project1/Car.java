import java.util.ArrayList;

public class Car {

  private Junction junction;
  private String path;
  private ArrayList<SubPath> path2 = new ArrayList<SubPath>();
  private int time = 0;

  public Car(Junction j) {
    this.setJunction(j);
    this.setPath("");
  }

  public int numberJunctions() {
    return getPath().length();
  }

  public void reduceTime(int time2Reduce) {
    setTime(getTime() - time2Reduce);
  }

  public int getTime() {
    return time;
  }

  public void setTime(int time2Set) {
    time = time2Set;
  }

  public Junction getJunction() {
    return this.junction;
  }

  public void setJunction(Junction junc2Set) {
    junction = junc2Set;
  }

  public String getPath() {
    return path;
  }

  public void setPath(ArrayList<SubPath> auxP) {
    getPath2().clear();
    setPath2(auxP);
  }

  public void setPath(String path) {
    this.path = path;
  }

  public ArrayList<SubPath> getPath2() {
    return path2;
  }

  public void setPath2(ArrayList<SubPath> path2) {
    this.path2 = path2;
  }
}
