import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class graphView {
  public static void main(String[] args) throws IOException {
    String[] s = new String[4 * 10 + 1];
    int counter = 0;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 10; j++) {
        StringBuilder iter = new StringBuilder();
        iter.append("gnuplot -e \"DATAFILE='paths/car");
        iter.append(i);
        iter.append("/iteration-");
        iter.append(j);
        iter.append(".dat'; OUTFILE='imgs/car");
        iter.append(i);
        iter.append("-");
        iter.append(j);
        iter.append(".png'\" plot.plt");
        s[counter] = iter.toString();
        ++counter;
      }
    }
    s[s.length-1] = "gnuplot -e \"DATAFILE='paths/graph.dat'; OUTFILE='imgs/grafo.png'\" plot.plt";

    for (String  a: s) {
      System.out.println(a);
    }

    ProcessBuilder processBuilder = new ProcessBuilder();

    try {
      for (String  a: s) {
        processBuilder.command("bash", "-c", a);
        Process process = processBuilder.start();

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
          System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("\nExited with error code : " + exitCode);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
