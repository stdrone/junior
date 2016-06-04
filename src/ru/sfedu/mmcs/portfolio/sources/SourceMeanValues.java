package ru.sfedu.mmcs.portfolio.sources;

import java.io.Serializable;

public class SourceMeanValues implements Serializable{
	private static final long serialVersionUID = -207728236820395480L;
	private double[] _data;
	private String[] _names;
	
	public SourceMeanValues(double[] data, String[] names) {
		_data = data;
		_names = names;
	}
	
	public double get(int i)
	{
		return _data[i];
	}
	
	public void set(int i, double d)
	{
		_data[i] = d;
	}
	
	public int getCountVariables()
	{
		return _data.length;
	}
	
	public String getName(int i) {
		return _names[i];
	}
}
