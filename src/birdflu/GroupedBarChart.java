package birdflu;

import java.util.ArrayList;
import java.util.Comparator;
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
 * Class for generating grouped bar charts for the semester project "Bird Flu"
 * 
 * @author Simon Aschenbrenner, Luis Rieke, Paul Gronemeyer, Büsra Bagci
 *
 * Charts are generated using:
 * JFreeChart : a free chart library for the Java(tm) platform
 * (C) Copyright 2000-2017, by Object Refinery Limited and Contributors.
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.]
 */

public class GroupedBarChart implements BirdFluChart {

	public static final int KEY_COLUMN = 1;
	public static final int VALUE_COLUMN = 2;
	
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
	
	class DatasetComparator implements Comparator<ArrayList<Object>> {
		public int compare(ArrayList<Object> a1, ArrayList<Object> a2) {
			Double d1 = (Double) a1.get(1);
			Double d2 = (Double) a2.get(1);
			return d1.compareTo(d2);
		}
	}
	
	private CategoryDataset initDataset(HashMap<String, ArrayList<Double>> source) {
		
//		Dataset lässt sich nicht sortieren, daher Umwandlung der HashMap in eine verschachtelte
//		ArrayList mit anschließender Sortierung nach value (Index 1)
		
		ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
		for(String key : source.keySet()) {
			Integer mainValue = Integer.valueOf(
					(int) (source.get(key).get(0)*100));
			if(threshold < 1 || mainValue > threshold || source.get(key).get(1) > 0) {
				ArrayList<Object> element = new ArrayList<Object>();
				element.add(key); // Index 0
				element.addAll(source.get(key)); // Indizes 1, 2
				list.add(element);
			}
		}

		list.sort(new DatasetComparator());
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(ArrayList<Object> element : list) {
			dataset.addValue((Double) element.get(1), "Suchen", (String) element.get(0));
			dataset.addValue((Double) element.get(2), "Infizierte", (String) element.get(0));
		}
		return dataset;
	}
	
	public static GroupedBarChart createGroupedBarChart(String filename, String c1r,
			String c1d, String c2r, String c2d, String title, String categoryAxisLabel,
			String valueAxisLabel, boolean horizontal, int threshold) {
		
		HashMap<String, ArrayList<Double>> source = new HashMap<String, ArrayList<Double>>();
		PlotOrientation orientation =
				(horizontal) ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
		Query c1rQuery = new Query(c1r);
		Query c1dQuery = new Query(c1d);
		Query c2rQuery = new Query(c2r);
		Query c2dQuery = new Query(c2d);
		
		try {
			HashMap<String, Integer> c1dat = c1dQuery.getMap(KEY_COLUMN, VALUE_COLUMN);
			HashMap<String, Integer> c2dat = c2dQuery.getMap(KEY_COLUMN, VALUE_COLUMN);
			Double c1ref = c1rQuery.getList(KEY_COLUMN).element().doubleValue();
			Double c2ref = c2rQuery.getList(KEY_COLUMN).element().doubleValue();

			for(String key : c1dat.keySet()) {
				ArrayList<Double> valueList = new ArrayList<Double>(3);
				valueList.add(0, (Double) c1dat.get(key).doubleValue() / c1ref);
				source.put(key, valueList);
			}
			for(String key : c2dat.keySet()) {
				source.get(key).add(1, (Double) c2dat.get(key).doubleValue() / c2ref);
			}
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning("Could not create BarChart for "
					+ filename + "\nNoDataException: " + e.getMessage());
		} finally {
			c1rQuery.close();
			c1dQuery.close();
			c2rQuery.close();
			c2dQuery.close();
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
