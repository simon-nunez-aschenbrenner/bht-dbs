package jfreechart;

import java.sql.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.statistics.HistogramDataset;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author luisr
 */
public class DataBaseTest {

    public DataBaseTest() {
    }

    public static void main(String[] args) throws IOException {

        String query = "select to_char(querydata.querytime, 'mm.dd'), count(distinct querydata.querytime) from aoldata.querydata where query like '%bird flu%' group by to_char(querydata.querytime, 'mm.dd') order by 1";
        ResultSet result = null;
        double[] array = new double[92];

        try {

            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:rispdb1", "s900762", "student");
            Statement stmt = con.createStatement();
            result = stmt.executeQuery(query);

            int i = 0;
            while (result.next()) {
                array[i] = result.getInt(2);
                i++;
                if (i >= array.length) {
                    System.err.println("Array too small");
                    break;
                }
            }
            result.close();
            stmt.close();

        } catch (SQLException e) {
            e.getMessage();
            e.getSQLState();
            e.getErrorCode();
        }

        for (double i : array) {
            System.out.println(i);
        }

        var dataset = new HistogramDataset();
        dataset.addSeries("key", array, 50);

        JFreeChart histogram = ChartFactory.createHistogram("Normal distribution",
                "Suchvolumen für Suchbegriffe", "x der Suche", dataset);

        ChartUtils.saveChartAsPNG(new File("histogram.png"), histogram, 450, 400);

    }

}
