package ru.sfedu.mmcs.portfolio.swing.chart;

import java.awt.geom.Ellipse2D;
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
import ru.sfedu.mmcs.portfolio.swing.chart.data.DataSetActives;
import ru.sfedu.mmcs.portfolio.swing.chart.data.DataSetFrontier;
import ru.sfedu.mmcs.portfolio.swing.chart.data.DataSetOptimal;

public class JFrontierChart extends ChartPanel {

	private static final long serialVersionUID = -4326820921442119966L;

	private JFrontierChart(JFreeChart chart) {
		super(chart);
	}

	private XYPlot _plot;

	public void refresh(AnalyzerData data)
	{
		if(data.getResult() != null)
		{
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
        
        chartPanel._plot.setRenderer(0, new XYLineAndShapeRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(0)).setAutoPopulateSeriesShape(false);
        chartPanel._plot.getRenderer(0).setBaseShape(new Ellipse2D.Double(-1, -1, 2, 2));
        
        chartPanel._plot.setRenderer(1, new StandardXYItemRenderer());
        
        chartPanel._plot.setRenderer(2, new XYLineAndShapeRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(2)).setAutoPopulateSeriesShape(false);
        chartPanel._plot.getRenderer(2).setBaseShape(ShapeUtilities.createDiagonalCross(3, 1));
        chartPanel._plot.getRenderer(2).setBaseSeriesVisibleInLegend(false);

        chartPanel._plot.setRenderer(3, new XYLineAndShapeRenderer());
        ((AbstractRenderer) chartPanel._plot.getRenderer(3)).setAutoPopulateSeriesShape(false);
        chartPanel._plot.getRenderer(3).setBaseShape(new Ellipse2D.Double(-3, -3, 6, 6));

        
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

		Portfolio _data;
		@Override
		public void chartMouseClicked(ChartMouseEvent e) {
			if(e.getEntity() instanceof XYItemEntity)
			{
				if(_data != null)
					JFrontierChart.this.Events.fireEntityClick(
							new EventEntityClick(JFrontierChart.this, _data)
						);
			}
		}

		@Override
		public void chartMouseMoved(ChartMouseEvent e) {
			// https://stackoverflow.com/questions/1512112/jfreechart-get-mouse-coordinates
			DefaultXYDataset dataPoint = new DefaultXYDataset();
			_data = null;
			if(e.getEntity() instanceof XYItemEntity)
			{
				XYItemEntity ce = (XYItemEntity) e.getEntity();
				
				if(ce.getDataset() instanceof DataSetFrontier || 
						ce.getDataset() instanceof DataSetOptimal)
				{
					double x = (double) ce.getDataset().getX(ce.getSeriesIndex(),  ce.getItem());
					double y = (double) ce.getDataset().getY(ce.getSeriesIndex(),  ce.getItem());
					
					if(ce.getDataset() instanceof DataSetOptimal)
						_data = ((DataSetOptimal)ce.getDataset()).getPortfolio(ce.getSeriesIndex(),  ce.getItem());
					if(ce.getDataset() instanceof DataSetFrontier)
						_data = ((DataSetFrontier)ce.getDataset()).getPortfolio(ce.getSeriesIndex(),  ce.getItem());
					
					dataPoint.addSeries("", new double[][] {{x},{y}});
				}
			}
			JFrontierChart.this._plot.setDataset(2, dataPoint);
		}}
	
	public final EntityClickSource Events = new EntityClickSource();
}
