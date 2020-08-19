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
 * Class for generating bar charts for the semester project "Bird Flu"
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

public class BarChart implements BirdFluChart {

	public static final String REF_LABEL = "VOGELGRIPPE";
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
	
	private CategoryDataset initDataset(HashMap<String, Integer> source) {
		
//		Dataset lässt sich nicht sortieren, daher Umwandlung der HashMap in eine verschachtelte
//		ArrayList mit anschließender Sortierung nach value (Index 1)
		
		ArrayList<ArrayList<Object>> list = new ArrayList<ArrayList<Object>>();
		for(String key : source.keySet()) {
			Integer value = source.get(key);
			if(value > threshold) {
				ArrayList<Object> element = new ArrayList<Object>();
				element.add(key); // Index 0
				element.add(value); // Index 1
				list.add(element);
			}
		}
		list.sort((ArrayList<Object> a, ArrayList<Object> b)
				-> (Integer) b.get(1) - (Integer) a.get(1));
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for(ArrayList<Object> element : list) {
			dataset.addValue((Integer) element.get(1), (String) element.get(0), "");
		}
		return dataset;
	}
	
	public static BarChart createBarChart1(String filename, String bars, String title,
			String categoryAxisLabel, String valueAxisLabel, boolean horizontal,
			int threshold) {
		
		HashMap<String, Integer> source = null;
		PlotOrientation orientation =
				(horizontal) ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
		Query query = new Query(bars);
		
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
			("Could not create BarChart for " + filename + "\nSource map not complete");
			return null;
		}
	}
	
	public static BarChart createBarChart2(String filename, String refBar, String datBars,
			String title, String categoryAxisLabel, String valueAxisLabel, boolean horizontal,
			int threshold) {
		
		HashMap<String, Integer> source = null;
		PlotOrientation orientation =
				(horizontal) ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL;
		Query refQuery = new Query(refBar);
		Query datQuery = new Query(datBars);
		
		try {
			source = datQuery.getMap(KEY_COLUMN, VALUE_COLUMN);
			source.put(REF_LABEL, refQuery.getList(KEY_COLUMN).element());
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning("Could not create BarChart for "
					+ filename + "\nNoDataException: " + e.getMessage());
		} finally {
			datQuery.close();
			refQuery.close();
		}
		
		if(source != null) {
			return new BarChart(filename, source, title, categoryAxisLabel, valueAxisLabel,
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
