package ru.sfedu.mmcs.portfolio;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
	
	public TreeMap<Date, Map<String,Double>> getFutureValue(Portfolio portfolio, Date dateFrom, Date dateTo) {
		TreeMap<Date, Map<String,Double>> result = new TreeMap<Date, Map<String,Double>>(); 
		TreeMap<Date, Double[]> data = _data.getFutureData(dateFrom,dateTo);
		
		int[] idx = _data.getPortfolioBind(portfolio);
		double[] aX = new double[idx.length];
		for(int i = idx.length - 1; i >= 0; i--)
			aX[i] = portfolio.getActives().get(_data.getName(idx[i]));
		
		for(Entry<Date, Double[]> row : data.entrySet())
		{
			TreeMap<String,Double> dRow = new TreeMap<String, Double>();
			for(int i = idx.length - 1; i >= 0; i--)
				dRow.put(_data.getName(idx[i]), row.getValue()[idx[i]]);
			
			result.put(row.getKey(), dRow);
		}
		return result;
	}
}
