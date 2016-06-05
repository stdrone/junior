package ru.sfedu.mmcs.portfolio.swing.chart.data;

import java.util.LinkedList;
import org.jfree.data.xy.AbstractXYDataset;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.frontier.Frontier.FrontierData;

public class DataSetFrontier extends AbstractXYDataset {

	private static final long serialVersionUID = 3136129879221026365L;
	
	private FrontierData[] _frontier;
		
	public DataSetFrontier(LinkedList<FrontierData> frontier) {
		_frontier = new FrontierData[frontier.size()];
		frontier.toArray(_frontier);
	}
	
	@Override
	public int getItemCount(int series) {
		return _frontier[series].size();
	}

	@Override
	public Number getX(int series, int item) {
		return _frontier[series].get(item).getValue();
	}

	@Override
	public Number getY(int series, int item) {
		return _frontier[series].get(item).getRisk();
	}

	@Override
	public int getSeriesCount() {
		return _frontier.length;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getSeriesKey(int series) {
		if(_frontier.length > 0)
			return "Граница " + _frontier[series].getName();
		return "";
	}
	
	public Portfolio getPortfolio(int series, int item)
	{
		return _frontier[series].get(item);
	}
}
