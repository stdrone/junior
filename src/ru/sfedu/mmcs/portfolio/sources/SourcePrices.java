package ru.sfedu.mmcs.portfolio.sources;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;

public class SourcePrices {
	private ArrayList<String> _actives = new ArrayList<String>();
	private ArrayList<Date> _dates = new ArrayList<Date>();
	private TreeMap<Date,TreeMap<String,Double[]>> _prices = new TreeMap<Date,TreeMap<String,Double[]>>();
	private TreeMap<Integer,String> _activesData = new TreeMap<Integer,String>();
	
	public SourcePrices() {
		_activesData = null;
	}
	
	public SourcePrices(TreeMap<Integer,String> actives) {
		_activesData = actives;
	}
	
	public void add(String active, Date date, Double price, Double price_new) {
		if(_activesData.containsValue(active)) {
			if(!_dates.contains(date))
				_dates.add(date);
			if(!_actives.contains(active))
				_actives.add(active);
			if(!_prices.containsKey(date))
				_prices.put(date, new TreeMap<String,Double[]>());
			
			TreeMap<String,Double[]> data = _prices.get(date);
			data.put(active, new Double[] {price,price_new});
		}
	}
	
	public Double get(int active, int date)
	{
		Double[] data = _prices.get(_dates.get(date)).get(_actives.get(active));
		return (data[1] == null) ? data[0] : data[1];
	}
	
	public void set(int active, int date, Double price)
	{
		String aName = _actives.get(active);
		Date dDate = _dates.get(date);
		Double[] data = _prices.get(dDate).get(aName); 
		data[1] = price;
		SQLiteData.setPrice(dDate, aName, data[0], data[1]);
	}
	
	public int getCountActives()
	{
		return _actives.size();
	}
	
	public int getCountDates()
	{
		return _dates.size();
	}
	
	public String getActive(int active) {
		return _actives.get(active);
	}
	
	public Date getDate(int active) {
		if(active >= _dates.size() || active < 0)
			return new Date();
		return _dates.get(active);
	}

	public TreeMap<Integer,String> getActivesData() {
		return _activesData;
	}
}
