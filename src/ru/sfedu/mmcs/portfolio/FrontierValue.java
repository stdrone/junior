package ru.sfedu.mmcs.portfolio;

import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.DiagonalMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.apache.commons.math3.stat.StatUtils;

import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public class FrontierValue extends Frontier {

	public FrontierValue(DataLoader data) {
		super(data);
	}

	private RealMatrix _m, _matrixLeft, _matrixRight;
	private double _minM, _maxM;
	private HashMap<Integer, String> _varList;
	private double [][] _solve;
	private int[] _weakColumns;
	private double[] _muBorders;
	

	final protected void calculate()
	{
		_matrixLeft = genMatrix();
		_matrixRight = genMatrixRight();
		int n = _matrixLeft.getRowDimension();
		_m = new Array2DRowRealMatrix(n, n);
		_m.setSubMatrix(_matrixLeft.getSubMatrix(0, n - 1, 0, _data.getN() - 1).getData(), 0, 0);
		if(_data.getP() > 0)
			_m.setSubMatrix(_matrixLeft.getSubMatrix(0, n - 1, 2*_data.getN(), 2*_data.getN() + 2*_data.getP() - 1).getData(), 0, _data.getN());
		_m.setSubMatrix(_matrixLeft.getSubMatrix(0, n - 1, 2*_data.getN() + 4*_data.getP(), 2*_data.getN() + 4*_data.getP() + 1).getData(), 0, _data.getN()+2*_data.getP());
		
		_minM = StatUtils.min(_data.getM());
		_maxM = StatUtils.max(_data.getM());
		
		_weakColumns = new int[] {0, 0};
		_muBorders = new double[2];
		
		_varList = new HashMap<Integer, String>();
		for(Integer i = 0; i < _data.getN(); i++)
			_varList.put(i + 1, _data.getName(i));
		for(Integer i = _data.getN(); i < _data.getP(); i++)
			_varList.put(i + 1, "");

		while(solveEquation() && calcBoundries())
		{
			calcResults();
			changeWeakColumn();
		};
	}
	
	private boolean solveEquation()
	{
		DecompositionSolver solver = new LUDecomposition(_m).getSolver();
		try
		{
			_solve = solver.solve(_matrixRight).getData();
		}
		catch(SingularMatrixException ex)
		{
			return false;
		}
		return true;
	}
	
	private Portfolio calcPortfolio(Vector2D var) {
		TreeMap<String, Double> X = new TreeMap<String,Double>();
		for(int j = _solve.length - 1; j >= 0; j--)
			if(_varList.containsKey(j + 1) && _varList.get(j + 1) != "")
				X.put(_varList.get(j + 1), _solve[j][0] * var.getX() + _solve[j][1]);
		return new Portfolio(X, var);
	}
	
	private void addData(Vector2D var) {
		addPortfolio("", calcPortfolio(var));
	}
	
	private void addData(double[] a, double minM, double maxM)
	{
		addSeries(String.join(", ", (String[])_varList.values().toArray(new String[0])));
		
		double high = - a[1] / (2 * a[2]); 
		if(high > minM && high <= maxM)
			addOptimalPoint("Вершина", calcPortfolio(new Vector2D(high, a[0] + high*a[1] + high*high*a[2])));
		high = - 2 * a[0] / a[1];
		if(high > minM && high <= maxM)
			addOptimalPoint("Отношение Шарпа", calcPortfolio(new Vector2D(high, a[0] + high*a[1] + high*high*a[2])));
		high = Math.sqrt(a[0]/a[2]);
		if(high > minM && high <= maxM)
			addOptimalPoint("Мод.отн.Шарпа", calcPortfolio(new Vector2D(high, a[0] + high*a[1] + high*high*a[2])));
		
		double step = (maxM - minM) / 100;
		for(double m = minM; m <= maxM; m += step)
		{
			addData(new Vector2D(m, a[0] + m*a[1] + m*m*a[2]));
		}
	}
	
	private void calcResults() {
		double[] a = new double[3];
		RealMatrix solve = new Array2DRowRealMatrix(_solve);
		RealMatrix a1 = solve.getSubMatrix(0, _data.getN() - 1, 1, 1);
		RealMatrix a2 = solve.getSubMatrix(0, _data.getN() - 1, 0, 0);
		RealMatrix V = new Array2DRowRealMatrix(_data.getV());
		a[0] = a1.transpose().multiply(V).multiply(a1).getData()[0][0];
		a[1] = 2*a1.transpose().multiply(V).multiply(a2).getData()[0][0];
		a[2] = a2.transpose().multiply(V).multiply(a2).getData()[0][0];
		
		double minM = -a[1] / (2*a[2]);
		if((minM > _muBorders[1]) || (minM < _muBorders[0]))
			addData(a, _muBorders[0], _muBorders[1]);
		else if(minM > _muBorders[0])
		{
			addData(a, _muBorders[0], minM);
			_muBorders[0] = minM;
			addData(a, minM, _muBorders[1]);
		}
	}

	private boolean calcBoundries()
	{
		double[] tmp = new double[2];
		double[] muBordersOld = _muBorders; 
		_muBorders = new double[] {_minM, _maxM};
		_weakColumns[0] = 0;
		_weakColumns[1] = 0;
		for(int i = 0; i < _solve.length-2; i++)
		{
			if(_solve[i][0] > 0)
			{
				tmp[0] = -_solve[i][1] / _solve[i][0];
				tmp[1] = _maxM;
			}
			else if(_solve[i][0] < 0)
			{
				tmp[0] = _minM;
				tmp[1] = -_solve[i][1] / _solve[i][0];
			}
			else
			{
				tmp[0] = _minM;
				tmp[1] = _maxM;
			}
			if(_muBorders[0] < tmp[0])
			{
				_muBorders[0] = tmp[0];
				_weakColumns[0] = i;
			}
			if(_muBorders[1] > tmp[1] && _varList.containsKey(i+1))
			{
				_muBorders[1] = tmp[1];
				_weakColumns[1] = i;
			}
		}
		return (muBordersOld[0] <= _muBorders[1]) && (muBordersOld[1] < _muBorders[1]);
	}
	
	private void changeWeakColumn()
	{
		RealMatrix mr = genMatrix();
		if(_weakColumns[1] < _data.getN())
		{
			mr = mr.getColumnMatrix(_data.getN() + _weakColumns[1]);
		}
		else if(_weakColumns[1] < _data.getN() + 2*_data.getP())
		{
			mr = mr.getColumnMatrix(_data.getN() + 2 * _data.getP() + _weakColumns[1]);
		}
		_m.setColumnMatrix(_weakColumns[1], mr);
		_varList.remove(_weakColumns[1] + 1);
	}
	
	private RealMatrix genMatrixRight() {
		RealMatrix matrix = new Array2DRowRealMatrix(_data.getN() + 2*_data.getP() + 2, 2);
		
		matrix.setEntry(_data.getN() + 2*_data.getP(), 1, 1);
		matrix.setEntry(_data.getN() + 2*_data.getP() + 1, 0, 1);
		for(int i = 0; i < _data.getP(); i++)
		{
			matrix.setEntry(_data.getN() + i, 1, _data.getB(1)[i]);
			matrix.setEntry(_data.getN() + _data.getP() + i, 1, _data.getB(2)[i]);
		}
		
		return matrix;
	}
	
	private RealMatrix genMatrix()
	{
		RealMatrix matrix = new Array2DRowRealMatrix(_data.getN() + 2*_data.getP() + 2, 2*_data.getN() + 4*_data.getP() + 2);
		
		matrix.scalarMultiply(0);
		
		// X
		RealMatrix tmpM = (new Array2DRowRealMatrix(_data.getV(), true)).scalarMultiply(2);
		matrix.setSubMatrix(tmpM.getData(), 0, 0);
		
		// A A
		if(_data.getA().length > 0)
		{
			matrix.setSubMatrix(_data.getA(), _data.getN(), 0);
			matrix.setSubMatrix(_data.getA(), _data.getN() + _data.getP(), 0);
		}
		
		// e
		double[] tmp = new double[_data.getN()];
		Arrays.fill(tmp, 1);
		matrix.setSubMatrix(new double[][] {tmp}, _data.getN() + 2*_data.getP(), 0);
		
		// m
		matrix.setSubMatrix(new double[][] {_data.getM()}, _data.getN() + 2*_data.getP() + 1, 0);
		
		/* ======================================== */
		// v
		tmp = new double[_data.getN()];
		Arrays.fill(tmp, -1);
		tmpM = new DiagonalMatrix(tmp);
		matrix.setSubMatrix(tmpM.getData(), 0, _data.getN());

		if(_data.getA().length > 0)
		{
			// y
			tmp = new double[_data.getP()];
			Arrays.fill(tmp, -1);
			tmpM = new DiagonalMatrix(tmp);
			matrix.setSubMatrix(tmpM.getData(), _data.getN(), 2*_data.getN());
			tmpM = (DiagonalMatrix) tmpM.scalarMultiply(-1);
			matrix.setSubMatrix(tmpM.getData(), _data.getN()  + _data.getP() , 2*_data.getN() + _data.getP());
		
			// AT
			tmpM = (new Array2DRowRealMatrix(_data.getA())).transpose();
			matrix.setSubMatrix(tmpM.getData(), 0, 2*_data.getN() + 3*_data.getP());
			tmpM = tmpM.scalarMultiply(-1);
			matrix.setSubMatrix(tmpM.getData(), 0, 2*_data.getN() + 2*_data.getP());
		}

		// eT
		tmpM = (new Array2DRowRealMatrix(_data.getM()));
		matrix.setSubMatrix(tmpM.getData(), 0, 2*_data.getN() + 4*_data.getP());

		// mT
		tmp = new double[_data.getN()];
		Arrays.fill(tmp, 1);
		tmpM = (new Array2DRowRealMatrix(tmp));
		matrix.setSubMatrix(tmpM.getData(), 0, 2*_data.getN() + 4*_data.getP() + 1);
		
		return matrix;
	}
}
