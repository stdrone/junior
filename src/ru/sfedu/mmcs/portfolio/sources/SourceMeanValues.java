package ru.sfedu.mmcs.portfolio.sources;

import java.io.Serializable;

public class SourceMeanValues implements Serializable{
	private static final long serialVersionUID = -207728236820395480L;
	private double[] _data;
	
	public SourceMeanValues(double[] data) {
		_data = data;
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
}
