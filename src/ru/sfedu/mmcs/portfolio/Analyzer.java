package ru.sfedu.mmcs.portfolio;

import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public abstract class Analyzer {
	protected FrontierDataLoader _data;
	
	@SuppressWarnings("serial")
	protected class FrontierDataLoader extends DataLoader {

		public FrontierDataLoader(DataLoader data) {
			super(data);
		}
		
		public String getName(int active) {
			return _names[active];
		}
	}
	
	protected void loadData(DataLoader loader)
	{
		_data = new FrontierDataLoader(loader);
		calculate();
	}
	
	abstract protected void calculate(); 

	public DataLoader getLoader()
	{
		if(_data == null)
			return null;
		return new DataLoader(_data);
	}
	
	public boolean haveFuture() {
		return _data.haveFuture();
	}
	
	public TreeMap<Date, Double> getFutureValue(Portfolio portfolio, Date dateFrom, Date dateTo) {
		TreeMap<Date, Double> result = new TreeMap<Date, Double>(); 
		TreeMap<Date, Double[]> data = _data.getFutureData(dateFrom,dateTo);
		
		int[] idx = _data.getPortfolioBind(portfolio);
		double[] aX = new double[idx.length];
		for(int i = idx.length - 1; i >= 0; i--)
			aX[i] = portfolio.getActives().get(_data.getName(idx[i]));
		RealMatrix X = new Array2DRowRealMatrix(aX).transpose();
		
		for(Entry<Date, Double[]> row : data.entrySet())
		{
			double[] dRow = new double[idx.length];
			for(int i = dRow.length - 1; i >= 0; i--)
				dRow[i] = row.getValue()[idx[i]];
			RealMatrix pR = new Array2DRowRealMatrix(dRow);
			
			result.put(row.getKey(), X.multiply(pR).getEntry(0, 0));
		}
		return result;
	}
}
