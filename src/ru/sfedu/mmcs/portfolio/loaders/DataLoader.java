package ru.sfedu.mmcs.portfolio.loaders;
import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.Covariance;

import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.PortfolioException;
import ru.sfedu.mmcs.portfolio.sources.SourceCovariance;
import ru.sfedu.mmcs.portfolio.sources.SourceLimits;
import ru.sfedu.mmcs.portfolio.sources.SourceMeanValues;

public class DataLoader implements Serializable {
	
	private static final long serialVersionUID = 5524131352896367999L;
	protected int _n;
	protected int _p;
	
	protected double[][] _A;
	protected double[][] _V;
	protected double[] _B1;
	protected double[] _B2;
	protected double[] _M;
	protected String[] _names;
	protected double[][] _r;
	protected Date _dataBegin, _dataEnd, _analyzeBegin, _analyzeEnd;
	
	public DataLoader(DataLoader data)
	{
		this._n = data._n;
		this._p = data._p;
		this._V = data._V.clone();
		this._A = data._A.clone();
		this._M = data._M.clone();
		this._B1 = data._B1.clone();
		this._B2 = data._B2.clone();
		this._names = data._names.clone();
		this._r = (data._r == null) ? null : data._r.clone();
		this._dataBegin = data._dataBegin;
		this._dataEnd = data._dataEnd;
		this._analyzeBegin = data._analyzeBegin;
		this._analyzeEnd = data._analyzeEnd;
		checkData();
	}
	
	protected DataLoader(int n, int p, double[][] V, double[][] A, double[] M, double[] B1, double[] B2, String[] names)
	{
		this._n = n;
		this._p = p;
		this._V = V.clone();
		this._A = A.clone();
		this._M = M.clone();
		this._B1 = B1.clone();
		this._B2 = B2.clone();
		this._names = names.clone();
		checkData();
	}
	
	public DataLoader(DataLoader data, Date dateFrom, Date dateTo)
	{
		if(data._dataBegin.after(dateFrom) || data._dataEnd.before(dateTo))
			throw new PortfolioException("Указанные даты выходят за пределы доступных данных");
		this._n = data._n;
		this._p = data._p;
		this._V = data._V;
		this._A = data._A;
		this._M = data._M;
		this._B1 = data._B1;
		this._B2 = data._B2;
		this._names = data._names;
		this._r = data._r;
		this._dataBegin = data._dataBegin;
		this._dataEnd = data._dataEnd;
		this._analyzeBegin = dateFrom;
		this._analyzeEnd = dateTo;
		
		calc();
		checkData();
	}
	
	public void addEquation() {
		++_p;
		double[] b1 = _B1, b2 = _B2;
		double[][] a = _A;
		_B1 = new double[_p];
		_B2 = new double[_p];
		_A = new double[_p][_n];
		System.arraycopy(b1, 0, _B1, 0, _p - 1);
		System.arraycopy(b2, 0, _B2, 0, _p - 1);
		System.arraycopy(a, 0, _A, 0, _p - 1);
	}
	
	public void delEquation(int equation) {
		if(equation >=0 && equation < _p) {
			--_p;
			double[] b1 = _B1, b2 = _B2;
			double[][] a = _A;
			_B1 = new double[_p];
			_B2 = new double[_p];
			_A = new double[_p][_n];
			System.arraycopy(b1, 0, _B1, 0, equation);
			System.arraycopy(b2, 0, _B2, 0, equation);
			System.arraycopy(a, 0, _A, 0, equation);
			System.arraycopy(b1, equation + 1, _B1, equation, _p - equation);
			System.arraycopy(b2, equation + 1, _B2, equation, _p - equation);
			System.arraycopy(a, equation + 1, _A, equation, _p - equation);
		}
	}
	
	private void checkData()
	{
		if((_A.length > 0 && _V.length != _A[0].length) || _V.length != _M.length)
			throw new PortfolioException("Количество переменных по матрицам V, M и B не совпадают.");
		if(_B1.length != _A.length)
			throw new PortfolioException("Количество ограничений по матрицам A и B не совпадают.");
	}
	
	private RealMatrix getData(Date dataBegin, Date dataEnd) {
		long daysBeg = TimeUnit.DAYS.convert(dataBegin.getTime() - _dataBegin.getTime(),TimeUnit.MILLISECONDS);
		long daysLast = TimeUnit.DAYS.convert(dataEnd.getTime() - _dataBegin.getTime(),TimeUnit.MILLISECONDS);
		return new Array2DRowRealMatrix(_r).getSubMatrix((int) daysBeg, (int) daysLast, 0, _n - 1);
	}
	
	private void calc() {
		RealMatrix data = getData(_analyzeBegin, _analyzeEnd);

		for(int i = _n - 1; i >= 0; i--)
			_M[i] = StatUtils.mean(data.getColumn(i));
		
		Covariance cov = new Covariance(data);
		_V = cov.getCovarianceMatrix().getData();
	}
	
	public SourceCovariance getCovariance()
	{
		return new SourceCovariance(_V, _names);
	}
	
	public SourceLimits getLimits()
	{
		return new SourceLimits(_A, _B1, _B2, _names);
	}
	
	public SourceMeanValues getMeanValues()
	{
		return new SourceMeanValues(_M, _names);
	}
	
	public Date getDataBegin() {
		return (_dataBegin == null) ? null :(Date) _dataBegin.clone();
	}
	
	public Date getDataEnd() {
		return (_dataEnd == null) ? null :(Date) _dataEnd.clone();
	}
	
	public Date getAnalyzeBegin() {
		return (_analyzeBegin == null) ? getDataBegin() :(Date) _analyzeBegin.clone();
	}
	
	public Date getAnalyzeEnd() {
		return (_analyzeEnd == null) ? getDataEnd() : (Date) _analyzeEnd.clone();
	}
	
	public int[] getPortfolioBind(Portfolio portfolio) {
		int size = portfolio.getActives().size();
		int inew = 0, iold = 0;
		int[] idx = new int[size];
		for(String name : _names)
		{
			if(portfolio.getActives().containsKey(name))
			{
				idx[inew] = iold;
				inew++;
			}
			iold++;
		}
		return idx;
	}
	
	public boolean haveFuture() {
		return (_analyzeEnd != null && _analyzeEnd.before(_dataEnd));
	}
	
	public boolean haveSource() {
		return _dataBegin != null && _dataEnd != null;
	}
	
	public TreeMap<Date, Double[]> getFutureData(Date dateFrom, Date dateTo) {
		TreeMap<Date, Double[]> result = new TreeMap<Date, Double[]>();
		if(haveFuture()){
			if(dateFrom.before(_analyzeEnd))
				dateFrom = _analyzeEnd;
			if(dateTo.after(_dataEnd))
				dateTo = _dataEnd;
			
			double[][] data = getData(dateFrom, dateTo).getData();
			
			for(int i = 0; i < data.length; i++) {
				Double[] row = new Double[_n];
				for(int j = 0; j < _n; j++)
					row[j] = data[i][j];
				result.put(dateFrom, row);
				dateFrom = DateUtils.addDays(dateFrom, 1);
			}
		}
		return result;
	}
	
	public double[][] getAnalyzedData() {
		return getData(_analyzeBegin, _analyzeEnd).getData();
	}
}
