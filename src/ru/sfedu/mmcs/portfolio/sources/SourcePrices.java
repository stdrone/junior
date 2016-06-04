package ru.sfedu.mmcs.portfolio.sources;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;

public class SourcePrices {
	private ArrayList<String> _actives = new ArrayList<String>();
	private TreeMap<Date,TreeMap<String,Double[]>> _prices = new TreeMap<Date,TreeMap<String,Double[]>>();
	private TreeMap<Integer,String> _activesData = new TreeMap<Integer,String>();
	private Date _dateFrom;
	private long _size;
	
	public SourcePrices() {
		_activesData = null;
	}
	
	public SourcePrices(TreeMap<Integer,String> actives, Date dateFrom, Date dateTo) {
		_activesData = actives;
		for(String active : _activesData.values()) {
			_actives.add(active);
		}
		if(dateFrom != null && dateTo != null) {
			_dateFrom = dateFrom;
			_size = 1 + TimeUnit.DAYS.convert(dateTo.getTime() - dateFrom.getTime(), TimeUnit.MILLISECONDS);
		}
	}
	
	public void add(String active, Date date, Double price, Double price_new) {
		if(_activesData.containsValue(active)) {
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
		Double[] data = _prices.get(getDate(date)).get(_actives.get(active));
		if(data == null) return null;
		return (data[1] == null) ? data[0] : data[1];
	}
	
	public void set(int active, int date, Double price)
	{
		String aName = _actives.get(active);
		Date dDate = getDate(date);
		Double[] data = _prices.get(dDate).get(aName);
		if(data != null)
			data[1] = price;
		else
			data = new Double[]{ null, price }; 
		SQLiteData.setPrice(dDate, aName, data[0], data[1]);
		add(aName, dDate, data[0], data[1]);
	}
	
	public int getCountActives()
	{
		return _actives.size();
	}
	
	public long getCountDates()
	{
		return _size;
	}
	
	public String getActive(int active) {
		return _actives.get(active);
	}
	
	public Date getDate(int date) {
		if(Math.abs(date) >= _size)
			return new Date(0);
		if(date < 0 && -date <= _size)
			return DateUtils.addDays(_dateFrom, (int)_size + date);
		return DateUtils.addDays(_dateFrom, date);
	}

	public TreeMap<Integer,String> getActivesData() {
		return _activesData;
	}
}
