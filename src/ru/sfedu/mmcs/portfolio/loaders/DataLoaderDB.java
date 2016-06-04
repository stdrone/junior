package ru.sfedu.mmcs.portfolio.loaders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import ru.sfedu.mmcs.portfolio.PortfolioException;
import ru.sfedu.mmcs.portfolio.sources.SourcePrices;

@SuppressWarnings("serial")
public class DataLoaderDB extends DataLoader {

	public DataLoaderDB(int p, SourcePrices prices) {
		super(prices.getCountActives(),p,new double[prices.getCountActives()][prices.getCountActives()],new double[p][prices.getCountActives()],new double[prices.getCountActives()],new double[p],new double[p], new String[prices.getCountActives()]);
		parse(prices);
	}
	
	private void parse(SourcePrices data) {
		TreeMap<Date,double[]> raw = new TreeMap<Date,double[]>();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

		double[] currentRow = new double[_n];
		double[] lastValue = new double[_n], readValue = new double[_n];
		Date readDate = null, lastDate = null; 

		boolean add = false;
		long countDates = data.getCountDates();
		for(int j = 0; j < countDates; j++)
		{
			lastDate = readDate;
			readDate = data.getDate(j);
			for(int i = _n - 1; i >= 0; i--) {
				if(_names[i] == null)
					_names[i] = data.getActive(i);
				lastValue[i] = readValue[i];
				
				Double rV = data.get(i, j);
				if(data.get(i, j) == null)
					throw new PortfolioException(String.format("Актив \"%s\" не содержит данных за %s", _names[i], formatter.format(readDate)));
				readValue[i] = rV;
				currentRow[i] = (readValue[i] - lastValue[i]) / lastValue[i];
			}
			if(add)
				raw.put(lastDate, currentRow.clone());
			add = true;
		}
		if(raw.size() == 0)
			throw new PortfolioException("Выбранные активы не содержат пересекающиеся временные даты.");
		_dataBegin = data.getDate(0);
		_dataEnd = lastDate;
		_r = raw.values().toArray(new double[0][0]);
	}
}
