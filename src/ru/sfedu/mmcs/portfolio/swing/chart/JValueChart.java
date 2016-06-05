package ru.sfedu.mmcs.portfolio.swing.chart;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.time.DateUtils;
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
		TreeMap<Date, Map<String,Double>> future = data.getFutureValue(portfolio, dateFrom, dateTo);
		final TimeSeries fact = new TimeSeries( "Факт" );
		final TimeSeries wait = new TimeSeries( "Ожидание" );
		
		double wValue = 1, dwValue = portfolio.getValue(), fValue = 1;
		Day day = new Day(DateUtils.addDays(future.firstKey(), -1));
		fact.add(day, fValue);
		wait.add(day, wValue);
		double dfValue, Xj, Yj;
		for(Entry<Date, Map<String,Double>> f : future.entrySet())
		{
			dfValue = 0.0;
			for(Entry<String, Double> partOf : portfolio.getActives().entrySet()) {
				Xj = partOf.getValue();
				Yj = f.getValue().get(partOf.getKey());
				dfValue += Xj*Yj;
			}
			fValue = fValue*(1+dfValue);
			wValue = wValue * (1 + dwValue);
			day = new Day(f.getKey());
			fact.add(day, fValue);
			wait.add(day, wValue);
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
