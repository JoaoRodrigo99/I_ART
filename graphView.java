import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class graphView {
  public static void plot(String algorithm) {
    String[] s = new String[4 * 10 + 1];
    int counter = 0;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 10; j++) {
        String iter = "gnuplot -e \"DATAFILE='paths/car" + i + "/" + algorithm + "/iteration-" + j + ".dat'; OUTFILE='imgs/" + algorithm + "/car" + i + "-" + j + ".png'\" plot.plt";
        s[counter] = iter;
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
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) {
    Scanner in = new Scanner(System.in);
    System.out.println("""
            1. Simulated Annealing Random
            2. Simulated Annealing Greedy
            3. Taboo search""");

    int optionChosen = in.nextInt();

    switch (optionChosen) {
      case 1 -> plot("simulatedAnnealingRandom");
      case 2 -> plot("simulatedAnnealingGreedy");
      case 3 -> plot("tabooSearch");
    }
  }
}
