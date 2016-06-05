package ru.sfedu.mmcs.portfolio;

import ru.sfedu.mmcs.portfolio.frontier.Frontier;
import ru.sfedu.mmcs.portfolio.frontier.FrontierValue;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;

public class AnalyzerData extends Analyzer {
	
	@Override
	public void loadData(DataLoader loader) {
		super.loadData(loader);
	}
	
	public Frontier getResult()
	{
		return _frontier;
	}
	
	private FrontierValue _frontier;
	@Override
	protected void calculate() {
		_frontier = new FrontierValue(this._data);
	}
}
