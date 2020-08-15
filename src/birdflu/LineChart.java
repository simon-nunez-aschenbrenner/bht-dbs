package birdflu;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Class for generating line charts for the semester project "Bird Flu"
 * The source data has to cover all 92 days.
 * 
 * Modul Datenbanksysteme, Dozent Aljoscha Marcel Everding, SS2020
 * 
 * @author Simon Aschenbrenner, Luis Rieke, Büsra Bagci, Paul Gronemeyer
 * 
 * Charts are generated using:
 * 
 * JFreeChart : a free chart library for the Java(tm) platform
 * 
 * (C) Copyright 2000-2017, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.]
 */
public class LineChart implements BirdFluChart {
	
	public static final int COLUMN = 2;
		
	protected JFreeChart chart;
	protected String filename;
	
	private LineChart(String filename, LinkedList<Integer> source, String title,
			String xAxisLabel, String yAxisLabel) {
		
		this.filename = filename;
		chart = initChart(source, title, xAxisLabel, yAxisLabel);
	}
	
	private JFreeChart initChart(LinkedList<Integer> source, String title, String xAxis,
			String yAxis) {
		
		XYDataset dataset = initDataset(source);
		
		DateAxis timeAxis = new DateAxis(xAxis);
        timeAxis.setLowerMargin(0.01);
        timeAxis.setUpperMargin(0.01);
        timeAxis.setDateFormatOverride(new SimpleDateFormat("dd.MM"));

		NumberAxis valueAxis = new NumberAxis(yAxis);
		valueAxis.setAutoRangeIncludesZero(false);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);        
        plot.setRenderer(renderer);
        
        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        new Theme().apply(chart);

        return chart;
	}
	
	private XYDataset initDataset(LinkedList<Integer> source) {
		XYSeries series = new XYSeries(filename);
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		Integer index = 1;
		while(!source.isEmpty()) {
			long date = getDateInMillis(index);
			if(date > 0) {
				series.add(date, source.removeFirst());
				index ++;
			} else {
				Logger.getLogger("Chart Logger").warning
				("Error while creating dataset for " + filename + "(@ Index " + index + ")");
				return null;
			}	
		}
        dataset.addSeries(series);
        return dataset;
	}
	
	private long getDateInMillis(int index) {
		if(index < 32) {
			return new GregorianCalendar(2006, Calendar.MARCH, index).getTimeInMillis();
		}
		else if(index < 62) {
			return new GregorianCalendar(2006, Calendar.APRIL, index-31).getTimeInMillis();
		}
		else if(index < 93) {
			return new GregorianCalendar(2006, Calendar.MAY, index-61).getTimeInMillis();
		} else {
			return 0;
		}
	}
	
	public static LineChart createLineChart
		(String filename, Query query, String title, String xAxis, String yAxis) {
		
		LinkedList<Integer> source = null;
		
		try {
			source = query.getList(COLUMN);
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning("Could not create LineChart for "
					+ filename + "\nNoDataException: " + e.getMessage());
		} finally {
			query.close();
		}
		
		if(source != null) {
			return new LineChart(filename, source, title, xAxis, yAxis);
		} else {
			Logger.getLogger("Chart Logger").warning
			("Could not create LineChart for " + filename);
			return null;
		}
	}
	
	public static LineChart createLineChartPCM
		(String filename, Query dividendQuery, Query divisorQuery, String title,
				String xAxis, String yAxis) {
		
		LinkedList<Integer> dividends = null;
		LinkedList<Integer> divisors = null;
		
		try {
			dividends = dividendQuery.getList(COLUMN);
			divisors  = divisorQuery.getList(COLUMN);
		} catch (NoDataException e) {
			Logger.getLogger("Chart Logger").warning("Could not create LineChart for "
					+ filename + "\nNoDataException: " + e.getMessage());
		} finally {
			dividendQuery.close();
			divisorQuery.close();
		}
		
		if(dividends == null || divisors == null) {
			Logger.getLogger("Chart Logger").warning
			("Could not create LineChartPCM for " + filename);
			return null;
		}
		if(dividends.size() != divisors.size()) {
			Logger.getLogger("Chart Logger").warning
			("Could not create LineChartPCM for " + filename);
			return null;
		}
			
		LinkedList<Integer> quotients = new LinkedList<Integer>();
		int size = dividends.size();
		for(int i = 0; i < size; i++) {
			double fraction =
					(double) dividends.removeFirst() / (double) divisors.removeFirst();
			quotients.add(Integer.valueOf((int)(fraction*100000)));
		}
 		return new LineChart(filename, quotients, title, xAxis, yAxis);
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