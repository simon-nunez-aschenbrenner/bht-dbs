package birdflu;

import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PieLabelLinkStyle;

public class Theme extends StandardChartTheme {
	
	public Theme() {
		super("Bird Flu");
		setTitlePaint(Colors.TEXT);
        setSubtitlePaint(Colors.TEXT);
        setLegendBackgroundPaint(Colors.BACKGROUND);
        setLegendItemPaint(Colors.TEXT);
        setChartBackgroundPaint(Colors.BACKGROUND);
        setPlotBackgroundPaint(Colors.BACKGROUND);
        setPlotOutlinePaint(Colors.BACKGROUND);
        setBaselinePaint(Colors.LINE);
        setLabelLinkStyle(PieLabelLinkStyle.STANDARD);
        setLabelLinkPaint(Colors.LINE);
        setTickLabelPaint(Colors.TEXT);
        setAxisLabelPaint(Colors.TEXT);
        setShadowVisible(false);
        setItemLabelPaint(Colors.TEXT);
        setDrawingSupplier(new DefaultDrawingSupplier(
                Colors.FILL_SEQUENCE, Colors.FILL_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
        setGridBandPaint(Colors.LINE);
	}
}
