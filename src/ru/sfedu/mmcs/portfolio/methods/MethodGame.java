package ru.sfedu.mmcs.portfolio.methods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public class MethodGame extends MethodValue {
	
	public MethodGame(DataLoader data, Portfolio portfolio) {
		super(data, portfolio);
		double[][] aData = data.getAnalyzedData();
		int[] idx = data.getPortfolioBind(portfolio);
		int n = idx.length;
		double[] dMax = aData[0].clone();
		double[] dMin = aData[0].clone();
		for(int i = 1; i < aData.length; i++)
			for(int j = 0; j < n; j++)
			{
				if(dMax[j] < aData[i][idx[j]])
					dMax[j] = aData[i][idx[j]];
				if(dMin[j] > aData[i][idx[j]])
					dMin[j] = aData[i][idx[j]];
			}
		double[] C = new double[n + 1];
		Arrays.fill(C, 0.0);
		C[n] = 1.0;
		LinearObjectiveFunction goalObjective = new LinearObjectiveFunction(C, 0);
		Collection<LinearConstraint> constraints = new ArrayList<LinearConstraint>();
		for(int i = 0; i < n; i++)
		{
			double[] A = new double[n + 1];
			for(int j = 0; j < n; j++)
				A[j] = (i == j) ? 0 : dMax[i] - dMin[j];
			A[n] = -1.0;
			constraints.add(new LinearConstraint(A,Relationship.LEQ,0));
		}
		double[] A = new double[n + 1];
		Arrays.fill(A, 1.0);
		A[n] = 0.0;
		constraints.add(new LinearConstraint(A,Relationship.EQ,1));
		
		SimplexSolver optimizer = new SimplexSolver(1e-10);
		
		PointValuePair solution = optimizer.optimize(new MaxEval(100),
				goalObjective, GoalType.MINIMIZE,
				new LinearConstraintSet(constraints),
				new NonNegativeConstraint(true));
		RealMatrix x0 = new Array2DRowRealMatrix(Arrays.copyOfRange(solution.getPointRef(), 0, n));
		
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
		return "Матричная игра";
	}

}
