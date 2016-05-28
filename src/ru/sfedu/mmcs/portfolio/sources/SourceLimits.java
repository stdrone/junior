package ru.sfedu.mmcs.portfolio.sources;

import java.io.Serializable;

public class SourceLimits implements Serializable {
	private static final long serialVersionUID = 4831679121012135819L;
	private double[][] _dataVariables;
	private double[] _dataLimitRight;
	private double[] _dataLimitLeft;
	
	public SourceLimits(double[][] dataVariables, double[] dataLimitsLeft, double[] dataLimitsRight) {
		_dataVariables = dataVariables;
		_dataLimitRight = dataLimitsRight;
		_dataLimitLeft = dataLimitsLeft;
	}
	
	public double getRightLimit(int i)
	{
		return _dataLimitRight[i];
	}

	public double getLeftLimit(int i)
	{
		return _dataLimitLeft[i];
	}
	
	public double getVariable(int i, int j)
	{
		return _dataVariables[i][j];
	}
	
	public void setVariable(int limit, int variable, double d)
	{
		_dataVariables[limit][variable] = d;
	}

	public void setLimitRight(int i, double d)
	{
		_dataLimitRight[i] = d;
	}	
	
	public void setLimitLeft(int i, double d)
	{
		_dataLimitLeft[i] = d;
	}	
	
	public int getCountVariables()
	{
		return (_dataVariables.length > 0 ) ? _dataVariables[0].length : 0;
	}

	public int getCountLimits()
	{
		return _dataLimitRight.length;
	}
}
