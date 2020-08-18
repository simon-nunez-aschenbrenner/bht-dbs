package birdflu;

import java.util.HashMap;
import java.util.logging.Logger;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.chart.util.Rotation;
import org.jfree.chart.util.SortOrder;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
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

public class PieChart implements BirdFluChart {
	
	public static final int KEY_COLUMN = 1;
	public static final int VALUE_COLUMN = 2;
	
	protected JFreeChart chart;
	protected String filename;
	protected int threshold;
	
	private PieChart(String filename, HashMap<String, Integer> source, String title,
			int threshold) {
		
		this.filename = filename;
		this.threshold = threshold;
		chart = initChart(source, title);
	}
	
	private JFreeChart initChart(HashMap<String, Integer> source, String title) {
		
		PieDataset dataset = initDataset(source);
		
		PiePlot plot = new PiePlot(dataset);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
		plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0)); 
		plot.setDirection(Rotation.CLOCKWISE);
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        new Theme().apply(chart);
        return chart;
	}
	
	private PieDataset initDataset(HashMap<String, Integer> source) {
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		int other = 0;
		
		for(String key : source.keySet()) {
			Integer value = source.get(key);
			if(value > threshold) {
				dataset.setValue("\"" + key + "\"", value);
			} else {
				other += value;
			}
		}
		if(other > 0) { dataset.setValue("Andere Suchbegriffe", other); }
		
		dataset.sortByValues(SortOrder.DESCENDING);
		
		return dataset;
		
	}
	
	public static PieChart createPieChart(String filename, Query query, String title,
			int threshold) {
		
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
			return new PieChart(filename, source, title, threshold);
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
