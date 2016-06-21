package ru.sfedu.mmcs.portfolio.swing.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;
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
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.util.ShapeUtilities;

import ru.sfedu.mmcs.portfolio.AnalyzerData;
import ru.sfedu.mmcs.portfolio.Portfolio;
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

        NumberAxis axisActives = new NumberAxis("Активы в портфеле");
        axisActives.setRange(0.0, 1.0);
        axisActives.setAutoRange(false);
        chartPanel._plot.setRangeAxis(1,axisActives);
        chartPanel._plot.mapDatasetToRangeAxis(1,1);
        chartPanel._plot.mapDatasetToRangeAxis(2,0);
        
        chartPanel._plot.setRenderer(0, new StandardXYItemRenderer());
        
        chartPanel._plot.setRenderer(1, new StandardXYItemRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(1)).setAutoPopulateSeriesStroke(false);
        chartPanel._plot.getRenderer(1).setBaseStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, new float[] {2.0f,1.0f,0.5f,1.0f}, 0.0f));
        
        chartPanel._plot.setRenderer(2, new XYLineAndShapeRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(2)).setAutoPopulateSeriesShape(false);
        ((AbstractRenderer) chartPanel._plot.getRenderer(2)).setAutoPopulateSeriesPaint(false);
        chartPanel._plot.getRenderer(2).setBaseShape(ShapeUtilities.createDiagonalCross(3, 1));
        chartPanel._plot.getRenderer(2).setBasePaint(Color.BLACK);
        chartPanel._plot.getRenderer(2).setBaseSeriesVisibleInLegend(false);

        chartPanel._plot.setRenderer(3, new XYLineAndShapeRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(3)).setAutoPopulateSeriesShape(false);
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
		private Portfolio _data;

		@Override
		public void chartMouseClicked(ChartMouseEvent e) {
			if(e.getTrigger().getButton() == MouseEvent.BUTTON1) {
				if(_data != null) 
					JFrontierChart.this.Events.fireEntityClick(new EventEntityClick(JFrontierChart.this, _data));
				else if(JFrontierChart.this._frontier != null) {
					Point2D p = JFrontierChart.this.translateScreenToJava2D(e.getTrigger().getPoint());
					Rectangle2D plotArea = JFrontierChart.this.getScreenDataArea();
					XYPlot plot = JFrontierChart.this._plot;
					double chartX = plot.getDomainAxis().java2DToValue(p.getX(), plotArea, plot.getDomainAxisEdge());
					Portfolio portfolio = JFrontierChart.this._frontier.calcPortfolio(new Vector2D(chartX, 0));
					if(portfolio != null)
						JFrontierChart.this.Events.fireEntityClick(
								new EventEntityClick(JFrontierChart.this, portfolio)
							);
				}
			}
		}

		@Override
		public void chartMouseMoved(ChartMouseEvent e) {
			DefaultXYDataset dataPoint = new DefaultXYDataset();
			_data = null;
			if(e.getEntity() instanceof XYItemEntity)
			{
				XYItemEntity ce = (XYItemEntity) e.getEntity();
				
				if(ce.getDataset() instanceof DataSetOptimal)
				{
					double x = (double) ce.getDataset().getX(ce.getSeriesIndex(),  ce.getItem());
					double y = (double) ce.getDataset().getY(ce.getSeriesIndex(),  ce.getItem());
					
					if(ce.getDataset() instanceof DataSetOptimal)
						_data = ((DataSetOptimal)ce.getDataset()).getPortfolio(ce.getSeriesIndex(),  ce.getItem());
					
					dataPoint.addSeries("", new double[][] {{x},{y}});
				}
			}
			JFrontierChart.this._plot.setDataset(2, dataPoint);
		}
	}
	
	public final EntityClickSource Events = new EntityClickSource();
}
