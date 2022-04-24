import com.panayotis.gnuplot.GNUPlot;
import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.FileDataSet;
import com.panayotis.gnuplot.plot.AbstractPlot;
import com.panayotis.gnuplot.plot.DataSetPlot;
import com.panayotis.gnuplot.plot.Plot;
import com.panayotis.gnuplot.style.NamedPlotColor;
import com.panayotis.gnuplot.style.PlotStyle;
import com.panayotis.gnuplot.style.Style;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

// Compile with: javac -cp JavaPlot.jar graphView.java
// Run with: java -cp JavaPlot.jar:. graphView
public class graphView {
    public static Style getRandom(Style[] array) {
        int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

    public static void plotGraph(ArrayList<String> filenames) throws IOException {
        JavaPlot plots = new JavaPlot();
        plots.set("key", "rmargin");
        plots.set("key title","'Paths'");
        plots.set("size", "square");
        plots.set("xlabel", "'x'");
        plots.set("ylabel", "'y'");
        plots.set("title", "'Graph map'");

        int counter = 0;
        for (String filename: filenames) {
            File dataSetFile = new File(filename);
            FileDataSet dataSet = new FileDataSet(dataSetFile);

            AbstractPlot plot = new DataSetPlot(dataSet);
            String title = filename.equals("graph.dat") ? "full graph" :
                    "car " + counter + " path";
            plot.setTitle(title);

            PlotStyle pltstl = plot.getPlotStyle();
            pltstl.setStyle(Style.LINESPOINTS);
            pltstl.setPointType((int) Math.ceil(Math.random() * filenames.size()));
            pltstl.setPointSize(1);
            pltstl.setLineWidth(1);

            plots.addPlot(plot);
            ++counter;
        }

        plots.plot();
    }

    public static void main(String[] args) throws IOException {
        ArrayList<String> plots = new ArrayList<>();
        plots.add("graph.dat");
        plots.add("path0.dat");
        plots.add("path1.dat");
        plots.add("path2.dat");
        plots.add("path3.dat");

        plotGraph(plots);
    }
}
