import com.panayotis.gnuplot.JavaPlot;
import com.panayotis.gnuplot.dataset.DataSet;
import com.panayotis.gnuplot.dataset.FileDataSet;

import java.io.File;
import java.io.IOException;

// Compile with: javac -cp JavaPlot.jar graphView.java
// Run with: java -cp JavaPlot.jar:. graphView
public class graphView {
    public static void main(String[] args) throws IOException {

        JavaPlot p = new JavaPlot();

        File dataSetFile = new File("graph.dat");
        FileDataSet dataSet = new FileDataSet(dataSetFile);
        p.addPlot(dataSet);

        p.plot();

    }
}
