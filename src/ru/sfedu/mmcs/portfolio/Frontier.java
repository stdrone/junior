package ru.sfedu.mmcs.portfolio;

import java.util.LinkedList;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public abstract class Frontier {
	protected final FrontierDataLoader _data;
	
	@SuppressWarnings("serial")
	protected class FrontierDataLoader extends DataLoader {

		public FrontierDataLoader(DataLoader data) {
			super(data);
		}
		
		public String getName(int active) {
			return _names[active];
		}
		public int getN() {
			return _n;
		}
		public int getP() {
			return _p;
		}
		public double[][] getV() {
			return _V;
		}
		public double[][] getA() {
			return _A;
		}
		public double[] getM() {
			return _M;
		}
		public double[] getB(int index) {
			if(index == 1) return _B1;
			if(index == 2) return _B2;
			return null;
		}
	}
	
	@SuppressWarnings("serial")
	public class FrontierData extends TreeMap<Double,Portfolio> {
		private String _name;
		private Integer _size;
		protected FrontierData(String name)
		{
			_name = name;
		}
		
		public Portfolio get(int index) {
			Double key = (Double) this.keySet().toArray()[index];
			return this.get(key);
		}
		
		public String getName()
		{
			return _name;
		}
		public Integer getActiveCount()
		{
			return _size;
		}
		@Override
		public Portfolio put(Double key,Portfolio value) {
			_size = value.getActives().size();
			return super.put(key, value);
		}
	};
	private final LinkedList<FrontierData> _results = new LinkedList<FrontierData>();
	private final TreeMap<String, FrontierData> _optimalPoints = new TreeMap<String, FrontierData>();

	public Frontier(DataLoader data) {
		if(data != null)
		{
			_data = new FrontierDataLoader(data);
			calculate();
		}
		else
			_data = null;
	}

	protected abstract void calculate();
	
	protected void addSeries(String name)
	{
		_results.add(new FrontierData(name));
	}
	
	protected void addPortfolio(String method, Portfolio portfolio)
	{
		_results.getLast().put(portfolio.getValue(), portfolio);
	}
	
	public abstract Portfolio calcPortfolio(Vector2D var);
	
	public void addOptimalPoint(String name, Portfolio portfolio)
	{
		FrontierData data = new FrontierData(name);
		data.put(portfolio.getValue(), portfolio);
		_optimalPoints.put(String.format("%s (%s)", name, portfolio.getName()) , data);
	}
	
	public LinkedList<FrontierData> getFrontier() {
		return _results;
	}
	
	public TreeMap<String, FrontierData> getOptimalPoints() {
		return _optimalPoints;
	}
}
