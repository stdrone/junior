package ru.sfedu.mmcs.portfolio.loaders;
import java.util.Arrays;

public class DataLoaderManual extends DataLoader {

	private static final long serialVersionUID = 7633742236090093251L;

	public DataLoaderManual(int n)
	{
		super(n,0,new double[n][n],new double[0][n],new double[n],new double[0],new double[0], new String[n]);
		
		for (double[] row: _V)
			Arrays.fill(row, 0.0);
		Arrays.fill(_M, 0.0);
		for(int i = 1; i <= n; i++)
			_names[i-1] = "x" + String.valueOf(i);
	}
}
