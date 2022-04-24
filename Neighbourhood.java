import java.util.LinkedList;

public class Neighbourhood {
  public LinkedList<LinkedList<String>> neighbourhood;
  public double distanceTravelled;
  public String candidate;
  public String givenPath;

  public Neighbourhood(String path) {
    this.givenPath = path;
    this.distanceTravelled = Double.MIN_VALUE;
    genNeighbourhood();
  }

  public void genCandidate() {}

  public void genNeighbourhood() {
    this.neighbourhood = new LinkedList<>();
  }
}
