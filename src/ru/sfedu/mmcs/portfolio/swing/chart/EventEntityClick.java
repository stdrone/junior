package ru.sfedu.mmcs.portfolio.swing.chart;

import ru.sfedu.mmcs.portfolio.Portfolio;

public class EventEntityClick {
	private Portfolio _portfolio;
	private Object _object;

	public EventEntityClick(Object o, Portfolio portfolio) {
		_portfolio = portfolio;
		_object = o;
	};
	
	public Portfolio getPortfolio()
	{
		return _portfolio;			
	}
	
	public Object getObject() {
		return _object;
	}
}