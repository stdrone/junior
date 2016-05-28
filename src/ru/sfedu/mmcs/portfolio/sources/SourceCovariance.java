package ru.sfedu.mmcs.portfolio.sources;

import java.io.Serializable;

public class SourceCovariance implements Serializable {
	private static final long serialVersionUID = -7300156323854314818L;
	private double[][] _data;
	private String[] _names;
	
	public SourceCovariance(double[][] data, String[] names) {
		_data = data;
		_names = names;
	}
	
	public double get(int i, int j)
	{
		return _data[i][j];
	}
	
	public void set(int i, int j, double d)
	{
		_data[i][j] = d;
		_data[j][i] = d;
	}
	
	public int getCountVariables()
	{
		return _data.length;
	}
	
	public String getName(int i) {
		return _names[i];
	}
}
