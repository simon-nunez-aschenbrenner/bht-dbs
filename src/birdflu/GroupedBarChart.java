/**
 * 
 */
package birdflu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author simonaschenbrenner
 *
 */
public class GroupedBarChart implements BirdFluChart {

	public static final int KEY_COLUMN = 1;
	public static final int VALUE_COLUMN = 2;
	public static final int MAIN_VALUE = VALUE_COLUMN;
	
	protected JFreeChart chart;
	protected String filename;
	protected int threshold;
	
	private GroupedBarChart(String filename, HashMap<String, ArrayList<Double>> source,
			String title, String categoryAxisLabel, String valueAxisLabel, PlotOrientation
			orientation, int threshold) {
		
		this.filename = filename;
		this.threshold = threshold;
		chart = initChart(source, title, categoryAxisLabel, valueAxisLabel, orientation);
	}
	
	private JFreeChart initChart(HashMap<String, ArrayList<Double>> source, String title,
			String categoryAxisLabel, String valueAxisLabel, PlotOrientation orientation) {
		
		CategoryDataset dataset = initDataset(source);
		
        CategoryAxis categoryAxis = new CategoryAxis(categoryAxisLabel);
        ValueAxis valueAxis = new NumberAxis(valueAxisLabel);

        BarRenderer renderer = new BarRenderer();
        if (orientation == PlotOrientation.HORIZONTAL) {
            ItemLabelPosition position1 = new ItemLabelPosition(
                    ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT);
            renderer.setDefaultPositiveItemLabelPosition(position1);
            ItemLabelPosition position2 = new ItemLabelPosition(
                    ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT);
            renderer.setDefaultNegativeItemLabelPosition(position2);
        } else if (orientation == PlotOrientation.VERTICAL) {
            ItemLabelPosition position1 = new ItemLabelPosition(
                    ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
            renderer.setDefaultPositiveItemLabelPosition(position1);
            ItemLabelPosition position2 = new ItemLabelPosition(
                    ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
            renderer.setDefaultNegativeItemLabelPosition(position2);
        }

        CategoryPlot plot = new CategoryPlot(dataset, categoryAxis, valueAxis,
                renderer);
        plot.setOrientation(orientation);
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);
        new Theme().apply(chart);
        return chart;
	}
	
	private CategoryDataset initDataset(HashMap<String, ArrayList<Double>> source) {
		
//		Dataset lässt sich nicht sortieren, daher Umwandlung der HashMap in eine verschachtelte
//		ArrayList mit anschließender Sortierung nach value (Index 1)
		
		ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
		for(String key : source.keySet()) {
			Integer mainValue = Integer.valueOf(
					(int) (source.get(key).get(MAIN_VALUE)*100));
			if(threshold < 1 || mainValue > threshold) {
				ArrayList<Object> element = new ArrayList<Object>();
				element.add(key); // Index 0
				element.addAll(source.get(key)); // Indizes 1-3
				list.add(element);
			}
		}
//		list.sort((ArrayList<Object> a, ArrayList<Object> b)
//				-> (int) ((Double) b.get(MAIN_VALUE) - (Double) a.get(MAIN_VALUE)));
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(ArrayList<Object> element : list) {
			dataset.addValue((Double) element.get(1), "Suchen", (String) element.get(0));
			dataset.addValue((Double) element.get(2), "Infizierte", (String) element.get(0));
			dataset.addValue((Double) element.get(3), "Tote", (String) element.get(0));
		}
		return dataset;
	}
	
	public static GroupedBarChart createGroupedBarChart(String filename, String c1r, String
			c1d, String c2r, String c2d, String c3r, String c3d, String title, String
			categoryAxisLabel, String valueAxisLabel, boolean horizontal, int threshold) {
		
		HashMap<String, ArrayList<Double>> source = new HashMap<String, ArrayList<Double>>();
		PlotOrientation orientation =
				(horizontal) ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
		Query c1rQuery = new Query(c1r);
		Query c1dQuery = new Query(c1d);
		Query c2rQuery = new Query(c2r);
		Query c2dQuery = new Query(c2d);
		Query c3rQuery = new Query(c3r);
		Query c3dQuery = new Query(c3d);
		
		try {
			HashMap<String, Integer> c1dat = c1dQuery.getMap(KEY_COLUMN, VALUE_COLUMN);
			HashMap<String, Integer> c2dat = c2dQuery.getMap(KEY_COLUMN, VALUE_COLUMN);
			HashMap<String, Integer> c3dat = c3dQuery.getMap(KEY_COLUMN, VALUE_COLUMN);

			Double c1ref = c1rQuery.getList(KEY_COLUMN).element().doubleValue();
			Double c2ref = c2rQuery.getList(KEY_COLUMN).element().doubleValue();
			Double c3ref = c3rQuery.getList(KEY_COLUMN).element().doubleValue();

			for(String key : c1dat.keySet()) {
				ArrayList<Double> valueList = new ArrayList<Double>(3);
				Double value = (c1dat.get(key) != null) ?
						c1dat.get(key).doubleValue()/c1ref : 0.0;
				valueList.add(0, value);
				source.put(key, valueList);
			}
			for(String key : c2dat.keySet()) {
				Double value = (c2dat.get(key) != null) ?
						c2dat.get(key).doubleValue()/c2ref : 0.0;
				source.get(key).add(1, value);
			}
			for(String key : c3dat.keySet()) {
				Double value = (c3dat.get(key) != null) ?
						c3dat.get(key).doubleValue()/c3ref : 0.0;
				source.get(key).add(2, value);
			}
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning("Could not create BarChart for "
					+ filename + "\nNoDataException: " + e.getMessage());
		} finally {
			c1rQuery.close();
			c1dQuery.close();
			c2rQuery.close();
			c2dQuery.close();
			c3rQuery.close();
			c3dQuery.close();
		}
		
		boolean sourceMapComplete = (source.size() > 0) ? true : false;
		if (sourceMapComplete) {
			for(String key : source.keySet()) {
				if(!sourceMapComplete) { break; }
				for(Double value : source.get(key)) {
					if(!sourceMapComplete) { break; }
					if(value == null) {
						sourceMapComplete = false;
					}
				}
			}
		}

		if(sourceMapComplete) {
			return new GroupedBarChart(filename, source, title, categoryAxisLabel, valueAxisLabel,
					orientation, threshold);
		} else {
			Logger.getLogger("Chart Logger").warning
			("Could not create BarChart for " + filename + "\nSource map not complete");
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
