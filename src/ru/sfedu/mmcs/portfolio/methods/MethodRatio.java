package ru.sfedu.mmcs.portfolio.methods;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.RealMatrix;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public class MethodRatio extends MethodValue {

	public MethodRatio(DataLoader data, Portfolio portfolio) {
		super(data, portfolio);
		RealMatrix x0 = _Vi.multiply(_M).scalarMultiply(1.0 / _M.transpose().multiply(_Vi).multiply(_M).getEntry(0, 0));
		x0 = x0.scalarMultiply(1.0 / x0.transpose().multiply(_E).getEntry(0, 0));
		
		double value = _M.transpose().multiply(x0).getEntry(0, 0);
		double risk = x0.transpose().multiply(_V).multiply(x0).getEntry(0, 0);

		SortedMap<String, Double> X = new TreeMap<String, Double>();
		for(int i = _names.length - 1; i >= 0; i--)
			X.put(_names[i], x0.getEntry(i, 0));
		
		_portfolio = new Portfolio(X, new Vector2D(value, risk));
	}

	@Override
	public Portfolio calculcatePortfolio(Vector2D point) {
		return _portfolio;
	}

	@Override
	public String getName() {
		return "Отношение риск/доходность";
	}

}
