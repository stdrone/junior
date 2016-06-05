package ru.sfedu.mmcs.portfolio.frontier;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class EdgeValue extends Edge {

	private double _a0, _a1, _a2, _min, _max, _step;
	private int _size = 11;
	private Portfolio _portfolio;
	private String _name;
	
	public EdgeValue(double min, double max, double a0, double a1, double a2) {
		_min = min;
		_max = max;
		_step = (max - min) / (_size - 1);
		_a0 = a0;
		_a1 = a1;
		_a2 = a2;
	}
	
	@Override
	public int size() {
		return _size;
	}

	@Override
	public double getM(int item) {
		return _min + _step * item;
	}

	@Override
	public double getV(int item) {
		double m = getM(item);
		return calcV(m);
	}
	
	public double calcV(double m) {
		return _a0 + m*_a1 + m*m*_a2;
	}
	
	public void setPortfolio(Portfolio portfolio) {
		_name = StringUtils.join(portfolio._names,",");
		_portfolio = portfolio;
	}

	public static class Portfolio {
		private List<String> _names = new ArrayList<String>();
		private List<Double> _alpha = new ArrayList<Double>(), _beta = new ArrayList<Double>();
		private Edge _edge;
		
		public Portfolio(Edge edge) {
			_edge = edge;
		}
		
		public void add(String name, double alpha, double beta) {
			_names.add(name);
			_alpha.add(alpha);
			_beta.add(beta);
		}

		public ru.sfedu.mmcs.portfolio.Portfolio calcPortfolio(double value) {
			TreeMap<String, Double> X = new TreeMap<String,Double>();
			double y = _edge.calcV(value);
			for(int j = _names.size() - 1; j >= 0; --j)
				X.put(_names.get(j), _alpha.get(j) * value + _beta.get(j));
			return new ru.sfedu.mmcs.portfolio.Portfolio(X, new Vector2D(value, y)); 
		}
	}

	@Override
	public boolean contains(double value) {
		return _min <= value && value < _max;
	}

	@Override
	public ru.sfedu.mmcs.portfolio.Portfolio calcPortfolio(double value) {
			return _portfolio.calcPortfolio(value);
	}

	@Override
	public String getName() {
		return _name;
	}
}
