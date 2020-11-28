package edu.bu.met622.output;

import edu.bu.met622.resources.ApplicationConfig;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**********************************************************************************************************************
 * Graph runtime results for each database query
 *
 * @author Michael Lewis
 * @version November 25, 2020 - Kickoff
 *********************************************************************************************************************/
public class Graph extends JFrame {

    private String dataSource;                                  // The filepath to read from

    /**
     * Initialize a new Graph instance and assign a default CSV file to use for the dataset
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new Grapher
     */
    public Graph() {
        dataSource = ApplicationConfig.RUNTIME_LOG;
    }

    /**
     * Initialize a new Graph instance and assign a default CSV file to use for the dataset
     *
     * @param dataSource The absolute path to a CSV file that contains the dataset to be graphed
     * @throws OutOfMemoryError Indicates insufficient memory for this new Grapher
     */
    public Graph(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Create a line chart. The x-axis is represented by intervals and the y-axis is represented by milliseconds. Each
     * point on the chart represents the time it took to query a database for a specific keyword
     */
    public void build() {

        DefaultCategoryDataset dataset = createDataSet();

        JFreeChart chart = ChartFactory.createLineChart(
                ApplicationConfig.CHART_TITLE,
                ApplicationConfig.X_AXIS,
                ApplicationConfig.Y_AXIS,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                false,
                false);

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    /**
     * Helper method that gets the dataset to be graphed from a CSV file on disk
     *
     * @return A dataset of x and y values to be plotted. The x-axis represents the intervals and the y-axis represents
     *         the total runtime of a database query in milliseconds
     */
    private DefaultCategoryDataset createDataSet() {
        DefaultCategoryDataset dataset = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(dataSource)))) {

            dataset = new DefaultCategoryDataset();             // The dataset that will contain x and y values
            String line;                                        // The current line being read
            String databaseType;                                // Previously stored database type
            double runtime;                                     // Previously stored runtime from database queries
            double interval = 1;                                // Used as the x-axis label

            while ((line = reader.readLine()) != null) {

                int count = 0;                                  // Every interval has four entries, so track how many entries we've seen so far
                while (line != null && count < 4) {

                    databaseType = line.split(ApplicationConfig.COMMA_DELIMITER)[0];
                    runtime = Double.parseDouble(line.split(ApplicationConfig.COMMA_DELIMITER)[2]);
                    dataset.addValue(runtime, databaseType, String.valueOf(interval));

                    /* All entries in this interval have been read. Thus, the next line is the beginning of the next
                       set of entries and the outer loop takes care of moving to the next set of entries */
                    if (count != 3) {
                        line = reader.readLine();
                    }
                    ++count;
                }

                ++interval;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataset;
    }
}
