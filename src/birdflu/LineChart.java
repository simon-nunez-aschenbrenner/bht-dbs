package birdflu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class LineChart {
	
	public static final String PATH = "./output/";
	
	protected JFreeChart lineChart = null;
	protected ChartPanel chartPanel;
	protected String title;
	protected String xAxis;
	protected String yAxis;
	
	public LineChart(Query query, String title, String xAxis, String yAxis) {
		
		this.title = title;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		
		try {
			lineChart = createChart(createDataset(query.getList(2)));
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning(e.getMessage());
		}
	}
	
	private XYDataset createDataset(LinkedList<Integer> source) {
		
		XYSeries series = new XYSeries(title);
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		Integer index = 0;
		while(!source.isEmpty()) {
			series.add(index, source.removeFirst());
			index ++;
		}
        dataset.addSeries(series);
        return dataset;
	}
	
	private JFreeChart createChart(XYDataset dataset) {
		JFreeChart chart = ChartFactory.createXYLineChart(
				title, xAxis, yAxis, dataset, PlotOrientation.VERTICAL, true, true, false);
		
		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));

		plot.setRenderer(renderer);
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinesVisible(true);
		plot.setRangeGridlinePaint(Color.BLACK);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.BLACK);

		chart.getLegend().setFrame(BlockBorder.NONE);
		chart.setTitle(new TextTitle(title, new Font("Serif", java.awt.Font.BOLD, 18)));
		
        return chart;
	}
	
	public void save() {
		if(lineChart != null) {
			try {
				ChartUtils.saveChartAsPNG(new File(PATH + title + ".png"), lineChart, 450, 400);
			} catch (IOException e) {
				Logger.getLogger("Chart Logger").warning(title + e.getMessage());
			}
		}
	}
}
