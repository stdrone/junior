package ru.sfedu.mmcs.portfolio;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import ru.sfedu.mmcs.portfolio.loaders.DataLoader;
import ru.sfedu.mmcs.portfolio.methods.Method;
import ru.sfedu.mmcs.portfolio.methods.MethodGame;
import ru.sfedu.mmcs.portfolio.methods.MethodRatio;
import ru.sfedu.mmcs.portfolio.methods.MethodRaw;
import ru.sfedu.mmcs.portfolio.methods.MethodRisk;
import ru.sfedu.mmcs.portfolio.methods.MethodValue;
import ru.sfedu.mmcs.portfolio.sources.PortfolioList;

public class AnalyzerPortfolio extends Analyzer {
	
	private List<Method> _methods = new ArrayList<Method>();
	private Vector2D _var;
	private TreeMap<String, Portfolio> _results = new TreeMap<String, Portfolio>();
	private Portfolio _basePortfolio;
	private PortfolioList _resultPortfolios;
	
	private void initMethods(DataLoader data, Portfolio portfolio) {
		_methods.clear();
		_methods.add(new MethodRaw(data, portfolio));
		_methods.add(new MethodValue(data, portfolio));
		_methods.add(new MethodRisk(data, portfolio));
		_methods.add(new MethodRatio(data, portfolio));
		_methods.add(new MethodGame(data, portfolio));
	}
	
	public void loadData(DataLoader loader, Portfolio portfolio) {
		_var = portfolio._var;
		_basePortfolio = portfolio;
		super.loadData(loader);
		_resultPortfolios = new PortfolioList(_results);
	}
	
	public PortfolioList getSource()
	{
		return _resultPortfolios;
	}

	@Override
	protected void calculate() {
		initMethods(_data, _basePortfolio);
		for(Method m : _methods)
			calculate(m);
	}
	
	protected void calculate(Method method) {
		if(_var != null)
		{
			Portfolio portfolio = method.calculcatePortfolio(_var);
			if(portfolio != null)
				_results.put(method.getName(), portfolio);
		}
	}
	
	public Portfolio getBasePortfolio() {
		return _basePortfolio;
	}
}
