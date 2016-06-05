package ru.sfedu.mmcs.portfolio.swing.chart.data;

import org.jfree.data.xy.AbstractXYDataset;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.frontier.Frontier;
import ru.sfedu.mmcs.portfolio.frontier.Frontier.FrontierData;

public class DataSetOptimal extends AbstractXYDataset {

	private static final long serialVersionUID = 3136129879221026365L;
	
	private FrontierData[] _data;
	private String[] _names;
		
	public DataSetOptimal(Frontier frontier) {
		_data = frontier.getOptimalPoints().values().toArray(new FrontierData[0]);
		_names = frontier.getOptimalPoints().keySet().toArray(new String[0]);
	}
	
	@Override
	public int getItemCount(int series) {
		return 1;
	}

	@Override
	public Number getX(int series, int item) {
		return _data[series].get(0).getValue();
	}

	@Override
	public Number getY(int series, int item) {
		return _data[series].get(0).getRisk();
	}

	@Override
	public int getSeriesCount() {
		return _data.length;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getSeriesKey(int series) {
		return _names[series];
	}
	
	public Portfolio getPortfolio(int series, int item)
	{
		return _data[series].get(0);
	}
}
