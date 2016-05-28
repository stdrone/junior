package ru.sfedu.mmcs.portfolio.sources;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;

public class SourcePrices {
	private ArrayList<String> _actives;
	private ArrayList<Date> _dates;
	private TreeMap<Date,TreeMap<String,Double[]>> _prices;
	
	public void add(String active, Date date, Double price, Double price_new) {
		if(!_prices.containsKey(date))
			_prices.put(date, new TreeMap<String,Double[]>());
		TreeMap<String,Double[]> data = _prices.get(date);
		if(!_dates.contains(date))
			_dates.add(date);
		int index = _actives.indexOf(active);
		if(index < 0)
		{
			_actives.add(active);
			index = _actives.indexOf(active);
		}
		data.put(active, new Double[] {price,price_new});
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
		return _dates.get(active);
	}
}
