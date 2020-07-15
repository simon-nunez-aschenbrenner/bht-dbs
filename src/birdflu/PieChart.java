package birdflu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class PieChart implements BirdFluChart {
	
	public static final int KEY_COLUMN = 1;
	public static final int VALUE_COLUMN = 2;
	
	protected JFreeChart chart;
	protected String filename;
	
	private PieChart(String filename, HashMap<String, Integer> source, String title) {
		
		this.filename = filename;
		chart = initChart(source, title);
	}
	
	private JFreeChart initChart(HashMap<String, Integer> source, String title) {
		
		PieDataset dataset = initDataset(source);
		
		PiePlot plot = new PiePlot(dataset);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator());
		plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
		
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        ChartPlotter.getTheme().apply(chart);
        
        return chart;
	}
	
	private PieDataset initDataset(HashMap<String, Integer> source) {
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		
		for(String key : source.keySet()) {
			dataset.setValue(key, source.get(key));
		}
		
		return dataset;
		
	}
	
	public static PieChart createPieChart(String filename, Query query, String title) {
		
		HashMap<String, Integer> source = null;
		
		try {
			source = query.getMap(KEY_COLUMN, VALUE_COLUMN);
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning("Could not create PieChart for "
					+ filename + "\nNoDataException: " + e.getMessage());
		} finally {
			query.close();
		}
		
		if(source != null) {
			return new PieChart(filename, source, title);
		} else {
			Logger.getLogger("Chart Logger").warning
			("Could not create PieChart for " + filename);
			return null;
		}
	}
	
	@Override
	public JFreeChart getChart() {
		return chart;
	}

	@Override
	public String getFilename() {
		return filename;
	}

}
