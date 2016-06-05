package ru.sfedu.mmcs.portfolio.sources;

import java.util.Map.Entry;

import ru.sfedu.mmcs.portfolio.Portfolio;

import java.util.TreeMap;

public class PortfolioList {

	private String[] _portfolioNames;
	private TreeMap<Integer, String> _activeNames;
	private Portfolio[] _data;
	
	public PortfolioList(TreeMap<String, Portfolio> src) {
		int n = src.size();
		_portfolioNames = new String[n];
		_activeNames = new TreeMap<Integer, String>();
		_data = new Portfolio[n];
		int idx = 0;
		for(Entry<String, Portfolio> port : src.entrySet())
		{
			_portfolioNames[idx] = port.getKey();
			_data[idx] = port.getValue();
			for(String active : _data[idx].getActives().keySet())
			{
				if(!_activeNames.containsValue(active))
					_activeNames.put(_activeNames.size(), active);
			}
			idx ++;
		}
	}
	
	public int getCountPortfolios() {
		return _portfolioNames.length;
	}
	
	public int getCountActives() {
		return _activeNames.size();
	}
	
	public double getPartOf(int portfolio, int active) {
		return _data[portfolio].getPartOf(_activeNames.get(active));
	}
	
	public double getRisk(int portfolio) {
		return _data[portfolio].getRisk();
	}

	public double getValue(int portfolio) {
		return _data[portfolio].getValue();
	}
	
	public String getPortfolioName(int i) {
		return _portfolioNames[i];
	}

	public String getActiveName(int i) {
		return _activeNames.get(i);
	}
	
	public Portfolio getPortfolio(int i){
		return _data[i];
	}
	
}
