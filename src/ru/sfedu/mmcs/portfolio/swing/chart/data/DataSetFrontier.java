package ru.sfedu.mmcs.portfolio.swing.chart.data;

import java.util.LinkedList;
import org.jfree.data.xy.AbstractXYDataset;

import ru.sfedu.mmcs.portfolio.frontier.Edge;

public class DataSetFrontier extends AbstractXYDataset {

	private static final long serialVersionUID = 3136129879221026365L;
	
	private Edge[] _frontier;
		
	public DataSetFrontier(LinkedList<Edge> frontier) {
		_frontier = new Edge[frontier.size()];
		frontier.toArray(_frontier);
	}
	
	@Override
	public int getItemCount(int series) {
		return _frontier[series].size();
	}

	@Override
	public Number getX(int series, int item) {
		return _frontier[series].getM(item);
	}

	@Override
	public Number getY(int series, int item) {
		return _frontier[series].getV(item);
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
}
