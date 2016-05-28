package ru.sfedu.mmcs.portfolio.swing.chart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import ru.sfedu.mmcs.portfolio.AnalyzerPortfolio;
import ru.sfedu.mmcs.portfolio.Portfolio;

public class JValueChart extends ChartPanel {

	private static final long serialVersionUID = -4326820921442119966L;

	private JValueChart(JFreeChart chart) {
		super(chart);
	}

	private XYPlot _plot;

	public void refresh(AnalyzerPortfolio data, Portfolio portfolio, Date dateFrom, Date dateTo)
	{
		TreeMap<Date, Double> future = data.getFutureValue(portfolio, dateFrom, dateTo);
		final TimeSeries fact = new TimeSeries( "Факт" );
		final TimeSeries wait = new TimeSeries( "Ожидание" );
		
		double value = 1, dValue = portfolio.getValue(), fValue = 1;
		for(Entry<Date, Double> f : future.entrySet())
		{
			value = value * (1 + dValue);
			fValue = fValue * (1 + f.getValue());
			fact.add(new Day(f.getKey()), fValue);
			wait.add(new Day(f.getKey()), value);
		}
		
		_plot.setDataset(new TimeSeriesCollection(fact));
		_plot.setDataset(1, new TimeSeriesCollection(wait));
	}
	
	public static JValueChart createValueChartPanel(String title, AnalyzerPortfolio data, Portfolio portfolio, Date dateFrom, Date dateTo) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Время", "Стоимость", null, true, true, true); 
		
		JValueChart chartPanel = new JValueChart(chart);
		chartPanel.setMouseWheelEnabled(true);
		
        chartPanel.setPopupMenu(chartPanel.createPopupMenu(false, true, true, true));
        
        chartPanel._plot = chart.getXYPlot();
        chartPanel._plot.setRenderer(1, new XYLineAndShapeRenderer());
        ((XYLineAndShapeRenderer) chartPanel._plot.getRenderer(1)).setBaseShapesVisible(false);
        DateAxis axis = (DateAxis) chartPanel._plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd.MM.yyyy"));

        chartPanel.refresh(data, portfolio, dateFrom, dateTo);

		return chartPanel;
	}
}
