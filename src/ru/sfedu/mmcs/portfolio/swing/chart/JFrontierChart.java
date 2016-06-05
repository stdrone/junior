package ru.sfedu.mmcs.portfolio.swing.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.util.ShapeUtilities;

import ru.sfedu.mmcs.portfolio.AnalyzerData;
import ru.sfedu.mmcs.portfolio.frontier.Frontier;
import ru.sfedu.mmcs.portfolio.swing.chart.data.DataSetActives;
import ru.sfedu.mmcs.portfolio.swing.chart.data.DataSetFrontier;
import ru.sfedu.mmcs.portfolio.swing.chart.data.DataSetOptimal;

public class JFrontierChart extends ChartPanel {

	private static final long serialVersionUID = -4326820921442119966L;

	private JFrontierChart(JFreeChart chart) {
		super(chart);
	}

	private XYPlot _plot;
	private Frontier _frontier;

	public void refresh(AnalyzerData data)
	{
		if(data.getResult() != null)
		{
			_frontier = data.getResult(); 
			DataSetFrontier frontierDataset = new DataSetFrontier(data.getResult().getFrontier());
			DataSetActives portfolioDataset = new DataSetActives(data.getResult().getFrontier());
			DataSetOptimal optimalDataset = new DataSetOptimal(data.getResult());
			_plot.setDataset(frontierDataset);
			_plot.setDataset(1, portfolioDataset);
			_plot.setDataset(3, optimalDataset);
	        _plot.getDomainAxis(0).setAutoRange(true);
		    _plot.getRangeAxis(0).setAutoRange(true);
		}
	}
	
	public static JFrontierChart createFrontierChartPanel(String title, AnalyzerData data) {
		JFreeChart chart = ChartFactory.createXYLineChart(title, "μ", "V(μ)", null, PlotOrientation.VERTICAL, true, false, false);
		
		JFrontierChart chartPanel = new JFrontierChart(chart);
		chartPanel.setMouseWheelEnabled(true);

        chartPanel.setPopupMenu(chartPanel.createPopupMenu(false, true, true, true));
        
        chartPanel._plot = chart.getXYPlot();
        chartPanel._plot.addChangeListener(chartPanel.new FixAxisListner());
        chartPanel.addChartMouseListener(chartPanel.new ChartClick());

        NumberAxis axisActives = new NumberAxis("Активы");
        axisActives.setRange(0.0, 1.0);
        axisActives.setAutoRange(false);
        chartPanel._plot.setRangeAxis(1,axisActives);
        chartPanel._plot.mapDatasetToRangeAxis(1,1);
        chartPanel._plot.mapDatasetToRangeAxis(2,0);
        
        chartPanel._plot.setRenderer(0, new StandardXYItemRenderer());
        //((AbstractRenderer) chartPanel._plot.getRenderer(0)).setAutoPopulateSeriesShape(false);
        //chartPanel._plot.getRenderer(0).setBaseShape(new Ellipse2D.Double(-1, -1, 2, 2));
        
        chartPanel._plot.setRenderer(1, new StandardXYItemRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(1)).setAutoPopulateSeriesStroke(false);
        chartPanel._plot.getRenderer(1).setBaseStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[] {2.0f,1.0f,0.5f,1.0f}, 0.0f));
        
        chartPanel._plot.setRenderer(2, new XYLineAndShapeRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(2)).setAutoPopulateSeriesShape(false);
        chartPanel._plot.getRenderer(2).setBaseShape(ShapeUtilities.createDiagonalCross(3, 1));
        chartPanel._plot.getRenderer(2).setBaseSeriesVisibleInLegend(false);

        chartPanel._plot.setRenderer(3, new XYLineAndShapeRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(3)).setAutoPopulateSeriesShape(false);
        ((AbstractRenderer) chartPanel._plot.getRenderer(3)).setAutoPopulateSeriesPaint(false);
        chartPanel._plot.getRenderer(3).setBasePaint(Color.BLACK);
        chartPanel._plot.getRenderer(3).setBaseShape(new Ellipse2D.Double(-3, -3, 6, 6));

        chartPanel.setHorizontalAxisTrace(true);
        chartPanel.refresh(data);

		return chartPanel;
	}
	
	private class FixAxisListner implements PlotChangeListener{
    	private boolean _changed = false;
		@Override
		public void plotChanged(PlotChangeEvent arg0) {
			if(!_changed)
			{
				_changed = true;
				((XYPlot)arg0.getPlot()).getRangeAxis(1).setRange(0.0,1.0);			
			}
			_changed = false;
		}
		
	};
	
	private class ChartClick implements ChartMouseListener{
		@Override
		public void chartMouseClicked(ChartMouseEvent e) {
			Point2D p = JFrontierChart.this.translateScreenToJava2D(e.getTrigger().getPoint());
			Rectangle2D plotArea = JFrontierChart.this.getScreenDataArea();
			XYPlot plot = (XYPlot) e.getChart().getPlot(); // your plot
			double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
			//double chartY = plot.getRangeAxis().java2DToValue(p.getY(), plotArea, plot.getRangeAxisEdge());
			if(JFrontierChart.this._frontier != null)
				JFrontierChart.this.Events.fireEntityClick(
						new EventEntityClick(JFrontierChart.this, JFrontierChart.this._frontier.calcPortfolio(new Vector2D(chartX, 0)))
					);
		}

		@Override
		public void chartMouseMoved(ChartMouseEvent e) {
		}
	}
	
	public final EntityClickSource Events = new EntityClickSource();
}
