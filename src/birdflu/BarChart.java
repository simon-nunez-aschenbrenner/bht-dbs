package birdflu;

import java.util.HashMap;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarChart implements BirdFluChart {

	public static final int KEY_COLUMN = 1;
	public static final int VALUE_COLUMN = 2;
	
	protected JFreeChart chart;
	protected String filename;
	protected int threshold;
	
	private BarChart(String filename, HashMap<String, Integer> source, String title,
			String categoryAxisLabel, String valueAxisLabel, PlotOrientation orientation,
			int threshold) {
		
		this.filename = filename;
		this.threshold = threshold;
		chart = initChart(source, title, categoryAxisLabel, valueAxisLabel, orientation);
	}
	
	private JFreeChart initChart(HashMap<String, Integer> source, String title,
			String categoryAxisLabel, String valueAxisLabel, PlotOrientation orientation) {
		
		CategoryDataset dataset = initDataset(source);
		
		JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel,
				valueAxisLabel, dataset, orientation, false, false, false);
        ChartPlotter.getTheme().apply(chart);
        
        return chart;
	}
	
	private CategoryDataset initDataset(HashMap<String, Integer> source) {
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(String key : source.keySet()) {
			Integer value = source.get(key);
			if(value > threshold) {
				dataset.addValue(value, key, "");
			}
		}
				
		return dataset;
		
	}
	
	public static BarChart createBarChart(String filename, Query query, String title,
			String categoryAxisLabel, String valueAxisLabel, boolean horizontal,
			int threshold) {
		
		HashMap<String, Integer> source = null;
		PlotOrientation orientation =
				(horizontal) ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
		
		try {
			source = query.getMap(KEY_COLUMN, VALUE_COLUMN);
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning("Could not create BarChart for "
					+ filename + "\nNoDataException: " + e.getMessage());
		} finally {
			query.close();
		}
		
		if(source != null) {
			return new BarChart(filename, source, title, categoryAxisLabel, valueAxisLabel,
					orientation, threshold);
		} else {
			Logger.getLogger("Chart Logger").warning
			("Could not create BarChart for " + filename);
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
