package ru.sfedu.mmcs.portfolio.swing.chart.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.jfree.data.xy.AbstractXYDataset;

import ru.sfedu.mmcs.portfolio.frontier.Edge;

public class DataSetActives extends AbstractXYDataset {

	private static final long serialVersionUID = 3136129879221026365L;
	
	private TreeMap<Integer,ArrayList<Pair<Double,Double>>> _data;
	private ArrayList<String> _names;
		
	public DataSetActives(LinkedList<Edge> frontier) {
		TreeMap<String,TreeMap<Double,Double>> data = new TreeMap<String,TreeMap<Double,Double>>();
		_data = new TreeMap<Integer,ArrayList<Pair<Double,Double>>>();
		_names = new ArrayList<String>();
		
		for(Edge d : frontier)
		{
			for(int i = 0; i < d.size(); ++i)
			{
				for(Entry<String, Double> a : d.calcPortfolio(d.getM(i)).getActives().entrySet())
				{
					if(!data.containsKey(a.getKey()))
						data.put(a.getKey(), new TreeMap<Double,Double>());
					data.get(a.getKey()).put(d.getM(i), a.getValue());
				}
			}
		}
		_names.addAll(data.keySet());
		for(String n : _names)
		{
			ArrayList<Pair<Double,Double>> array = new ArrayList<Pair<Double,Double>>();
			_data.put(_names.indexOf(n), array);
			for(Entry<Double, Double> d : data.get(n).entrySet())
				array.add(new Pair<Double,Double>(d.getKey(), d.getValue()));
		}
	}
	
	@Override
	public int getItemCount(int series) {
		return _data.get(series).size();
	}

	@Override
	public Number getX(int series, int item) {
		return _data.get(series).get(item).getKey();
	}

	@Override
	public Number getY(int series, int item) {
		return _data.get(series).get(item).getValue();
	}

	@Override
	public int getSeriesCount() {
		return _data.size();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comparable getSeriesKey(int series) {
		return StringUtils.substring(_names.get(series),0,3);
	}
}
