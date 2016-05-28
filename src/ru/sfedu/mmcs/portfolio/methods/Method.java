package ru.sfedu.mmcs.portfolio.methods;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public abstract class Method {
	final protected FrontierDataLoader _data;
	protected Portfolio _portfolio;
	
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
	
	public Method(DataLoader data, Portfolio portfolio) {
		_data = new FrontierDataLoader(data);
		_portfolio = portfolio;
	}	
	public abstract Portfolio calculcatePortfolio(Vector2D point);
	
	public Portfolio getPortfolio() {
		return _portfolio;
	}
	
	public abstract String getName();
}
