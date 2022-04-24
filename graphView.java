import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.FileDataSet;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;
import com.panayotis.gnuplot.terminal.ImageTerminal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

// Compile with: javac -cp JavaPlot.jar graphView.java
// Run with: java -cp JavaPlot.jar:. graphView
public class graphView {
  public static Style getRandom(Style[] array) {
    int rnd = new Random().nextInt(array.length);
    return array[rnd];
  }

  public static void plotGraph(ArrayList<String> filenames, boolean isSeparated)
      throws IOException {
    NamedPlotColor[] colors = {
      NamedPlotColor.DARK_BLUE,
      NamedPlotColor.DARK_RED,
      NamedPlotColor.GOLD,
      NamedPlotColor.FOREST_GREEN,
      NamedPlotColor.SKYBLUE
    };
    JavaPlot plots = new JavaPlot();
    plots.setTitle("Fleet Paths");
    plots.getAxis("x").setLabel("X axis", "Arial", 10);
    plots.getAxis("y").setLabel("Y axis", "Arial", 10);
    plots.getAxis("x").setBoundaries(44, 56);
    plots.getAxis("y").setBoundaries(1.95, 3.2);
    plots.setKey(JavaPlot.Key.OUTSIDE);

    if (isSeparated) {
      File dataSetFile = new File(filenames.get(0));
      FileDataSet dataSet = new FileDataSet(dataSetFile);

      AbstractPlot plot = new DataSetPlot(dataSet);
      plot.setTitle("Junctions");

      PlotStyle plotStyle = plot.getPlotStyle();
      plotStyle.setStyle(Style.LABELS);

      plots.addPlot(plot);

      int counter = 0;
      filenames.remove(0);
      for (String filename : filenames) {
        dataSetFile = new File(filename);
        dataSet = new FileDataSet(dataSetFile);

        plot = new DataSetPlot(dataSet);
        String title = "Car " + counter + " path";
        plot.setTitle(title);

        PlotStyle pltstl = plot.getPlotStyle();
        pltstl.setStyle(Style.LINES);
        pltstl.setLineWidth(filenames.size() * 2 - counter * 2);
        pltstl.setLineType(colors[counter]);

        plots.addPlot(plot);
        plots.plot();
        plots.getPlots().remove(1);
        ++counter;
      }
    } else {
      int counter = 0;
      for (String filename : filenames) {
        File dataSetFile = new File(filename);
        FileDataSet dataSet = new FileDataSet(dataSetFile);

        AbstractPlot plot = new DataSetPlot(dataSet);
        String title = filename.equals("graph.dat") ? "Junctions" : "Car " + counter + " path";
        plot.setTitle(title);

        PlotStyle pltstl = plot.getPlotStyle();
        pltstl.setStyle(Style.VECTORS);

        pltstl.setLineType(colors[counter]);
        pltstl.setLineWidth(filenames.size() - counter);

        plots.addPlot(plot);
        ++counter;
      }

      PlotStyle plotStyle = ((AbstractPlot) plots.getPlots().get(0)).getPlotStyle();
      plotStyle.setStyle(Style.POINTS);
      plotStyle.setPointSize(2);

      plots.plot();
    }
  }

  public static void plotIteration(String filename) throws IOException {
    ImageTerminal png = new ImageTerminal();
    File file = new File("imgs/" + filename + ".png");
    try {
      file.createNewFile();
      png.processOutput(new FileInputStream(file));
    } catch (IOException ex) {
      System.err.print(ex);
    }

    JavaPlot p = new JavaPlot();
    p.setPersist(false);
    p.setTerminal(png);

    File dataSetFile = new File("imgs/" + filename + ".dat");
    FileDataSet dataSet = new FileDataSet(dataSetFile);

    p.addPlot(dataSet);
    PlotStyle plotStyle = ((AbstractPlot) p.getPlots().get(0)).getPlotStyle();
    plotStyle.setStyle(Style.LINESPOINTS);
    plotStyle.setLineType(NamedPlotColor.BLACK);

    double[][] nextStreet = {
      {
        Double.parseDouble(dataSet.get(dataSet.size() - 2).get(0)),
        Double.parseDouble(dataSet.get(dataSet.size() - 2).get(1))
      },
      {
        Double.parseDouble(dataSet.get(dataSet.size() - 1).get(0)),
        Double.parseDouble(dataSet.get(dataSet.size() - 1).get(1))
      }
    };

    DataSetPlot next = new DataSetPlot(nextStreet);
    p.addPlot(next);
    PlotStyle plotStyle2 = ((AbstractPlot) p.getPlots().get(1)).getPlotStyle();
    plotStyle2.setStyle(Style.LINESPOINTS);
    plotStyle2.setLineType(NamedPlotColor.RED);

    p.plot();

    try {
      ImageIO.write(png.getImage(), "png", file);
    } catch (IOException ex) {
      System.err.print(ex);
    }
  }

  public static void main(String[] args) throws IOException {
    ArrayList<String> plots = new ArrayList<>();
    plots.add("graph.dat");
    plots.add("path0.dat");
    plots.add("path1.dat");
    plots.add("path2.dat");
    plots.add("path3.dat");
    plotGraph(plots, false);

    plotGraph(plots, true);
    for (int i = 0; i < 10; i++) {
      plotIteration("car-0-" + i);
    }
  }
}
