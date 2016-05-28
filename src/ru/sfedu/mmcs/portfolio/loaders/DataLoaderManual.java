package ru.sfedu.mmcs.portfolio.loaders;
import java.util.Arrays;

public class DataLoaderManual extends DataLoader {

	private static final long serialVersionUID = 7633742236090093251L;

	public DataLoaderManual(int n, int p)
	{
		super(n,p,new double[n][n],new double[p][n],new double[n],new double[p],new double[p], new String[n]);
		
		for (double[] row: _A)
			Arrays.fill(row, 0.0);
		for (double[] row: _V)
			Arrays.fill(row, 0.0);
		Arrays.fill(_B1, 0.0);
		Arrays.fill(_B2, 0.0);
		Arrays.fill(_M, 0.0);
		for(int i = 1; i <= n; i++)
			_names[i-1] = "x" + String.valueOf(i);
	}
}
