package ru.sfedu.mmcs.portfolio;

import java.util.SortedMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Portfolio {
	protected SortedMap<String,Double> _X;
	protected String[] _names; 
	protected Vector2D _var;
	
	public Portfolio(SortedMap<String,Double> X, Vector2D var) {
		_X = X;
		_var = var;
		_names = new String[X.size()];
		X.keySet().toArray(_names);
	}
	
	public SortedMap<String,Double> getActives()
	{
		return _X;
	}
	
	public Double getPartOf(String name)
	{
		return _X.get(name);
	}
	
	public Double getRisk()
	{
		return _var.getY();
	}
	
	public Double getValue()
	{
		return _var.getX();
	}
	
	public String getName() {
		String[] names = _names.clone();
		for(int i = names.length - 1; i >= 0; i--)
			names[i] = StringUtils.substring(names[i], 0, 3);
		return StringUtils.join(names,", ");
	}
}
