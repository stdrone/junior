package ru.sfedu.mmcs.portfolio.methods;

import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.RealMatrix;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public class MethodRisk extends MethodValue {

	public MethodRisk(DataLoader data, Portfolio portfolio) {
		super(data, portfolio);
	}
	
	@Override
	public Portfolio calculcatePortfolio(Vector2D point) {
		double nu = point.getY();
		double eVe = _E.transpose().multiply(_Vi).multiply(_E).getEntry(0, 0);
		double mVm = _M.transpose().multiply(_Vi).multiply(_M).getEntry(0, 0);
		double mVe = _M.transpose().multiply(_Vi).multiply(_E).getEntry(0, 0);
		double eVm = _E.transpose().multiply(_Vi).multiply(_M).getEntry(0, 0);

		double a = eVe - nu*eVe*eVe;
		double b = (nu*eVe*eVm - mVe);
		double c = mVm - nu*eVm*eVm;
		
		RealMatrix x0;
		double d = b*b - a*c;
		double risk;
		if(d >= 0)
		{
			d = Math.sqrt(d);
			double l21 = (-b + d)/a;
			double l22 = (-b - d)/a;
			
			double l11 = (eVm - l21*eVe) / 2.0;
			double l12 = (eVm - l22*eVe) / 2.0;
			RealMatrix x01 = _Vi.multiply(_M.subtract(_E.scalarMultiply(l21)).scalarMultiply(1.0/(2*l11)));
			RealMatrix x02 = _Vi.multiply(_M.subtract(_E.scalarMultiply(l22)).scalarMultiply(1.0/(2*l12)));
			double risk1 = x01.transpose().multiply(_V).multiply(x01).getEntry(0, 0);
			double risk2 = x02.transpose().multiply(_V).multiply(x02).getEntry(0, 0);
			if((risk1 - nu) > (risk2 - nu))
			{
				x0 = x02;
				risk = risk2;
			}
			else
			{
				x0 = x01;
				risk = risk1;
			}
		}
		else
			return null;
		double value = _M.transpose().multiply(x0).getEntry(0, 0);

		SortedMap<String, Double> X = new TreeMap<String, Double>();
		for(int i = _names.length - 1; i >= 0; i--)
			X.put(_names[i], x0.getEntry(i, 0));
		
		Portfolio portfolio = new Portfolio(X, new Vector2D(value, risk));
		return portfolio;
	}
	
	@Override
	public String getName() {
		return "Ограничение по риску";
	}
}
