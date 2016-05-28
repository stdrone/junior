package ru.sfedu.mmcs.portfolio.methods;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public class MethodValue extends Method{
	protected RealMatrix _V, _Vi, _M, _E;
	protected String[] _names;
	
	public MethodValue(DataLoader data, Portfolio portfolio) {
		super(data, portfolio);
		extractSubMatrix();
	}
	
	private void extractSubMatrix() {
		int[] idx = _data.getPortfolioBind(_portfolio);

		_names = new String[idx.length];
		double[][] V =  new double[idx.length][idx.length];
		double[] M = new double[idx.length];
		double[] E = new double[idx.length];
		for(int i = 0; i < idx.length; i ++)
		{
			for(int j = 0; j < idx.length; j ++)
			{
				V[i][j] = _data.getV()[idx[i]][idx[j]];
			}
			_names[i] = _data.getName(idx[i]);
			M[i] = _data.getM()[idx[i]];
			E[i] = 1;
		}
		_V = new Array2DRowRealMatrix(V);
		_Vi = new LUDecomposition(_V).getSolver().getInverse();
		_M = new Array2DRowRealMatrix(M);
		_E = new Array2DRowRealMatrix(E);
	}

	@Override
	public Portfolio calculcatePortfolio(Vector2D point) {
		double mu = point.getX();
		double eVe = _E.transpose().multiply(_Vi).multiply(_E).getEntry(0, 0);
		double mVm = _M.transpose().multiply(_Vi).multiply(_M).getEntry(0, 0);
		double mVe = _M.transpose().multiply(_Vi).multiply(_E).getEntry(0, 0);
		double eVm = _E.transpose().multiply(_Vi).multiply(_M).getEntry(0, 0);
		double sub = mVm*eVe-eVm*eVm;
		double lambda1 = 2*(mu*eVe - mVe) / sub;
		double lambda2 = 2*(mVm - mu*mVe) / sub;
		RealMatrix x0 = _Vi.scalarMultiply(0.5).multiply(_M.scalarMultiply(lambda1).add(_E.scalarMultiply(lambda2)));
		
		double value = _M.transpose().multiply(x0).getEntry(0, 0);
		double risk = x0.transpose().multiply(_V).multiply(x0).getEntry(0, 0);

		SortedMap<String, Double> X = new TreeMap<String, Double>();
		for(int i = _names.length - 1; i >= 0; i--)
			X.put(_names[i], x0.getEntry(i, 0));
		
		Portfolio portfolio = new Portfolio(X, new Vector2D(value, risk));
		return portfolio;
	}

	@Override
	public String getName() {
		return "Ограничение по доходности";
	}

}
