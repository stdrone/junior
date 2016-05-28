package ru.sfedu.mmcs.portfolio.loaders;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.DateUtils;

import ru.sfedu.mmcs.portfolio.PortfolioException;

public class DataLoaderFinamCSV extends DataLoader {

	private static final long serialVersionUID = 7633742236090093251L;

	public DataLoaderFinamCSV(int p, List<File> data)
	{
		super(data.size(),p,new double[data.size()][data.size()],new double[p][data.size()],new double[data.size()],new double[p],new double[p], new String[data.size()]);
		
		@SuppressWarnings("unchecked")
		Iterator<CSVRecord>[] isource = new Iterator[_n];
		CSVParser[] source = new CSVParser[_n];
		try {
			for(int i = _n - 1; i >= 0; i--)
			{
				isource[i] = CSVParser.parse(data.get(i), Charset.forName("UTF-8"), CSVFormat.newFormat(',')).iterator();
			}
			parse(isource);
		}
		catch(IOException e){
			throw new PortfolioException(e.getLocalizedMessage());
		}
		finally {
			for(int i = data.size() - 1; i >= 0; i--)
				if(source[i] != null)
					try {
						source[i].close();
					} catch (IOException e) {}
		}
	}
	
	private void parse(Iterator<CSVRecord>[] data) {
		TreeMap<Date,double[]> raw = new TreeMap<Date,double[]>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		double[] currentRow = new double[_n];
		Date currentDate = null;

		Date[] lastDate = new Date[_n], readDate = new Date[_n];
		double[] lastValue = new double[_n], readValue = new double[_n];

		boolean read = true;
		boolean add;
		while(read)
		{
			if(currentDate != null)
				currentDate = DateUtils.addDays(currentDate, 1);
			read = true;
			for(int i = _n - 1; i >= 0; i--) {
				read = read && data[i].hasNext();
				if( read && 
						(readDate[i] == null || readDate[i].before(currentDate)) 
					)
				{
					CSVRecord csv = data[i].next();
					while(csv.get(0).compareTo("<TICKER>") == 0)
						csv = data[i].next();
					if(_names[i] == null)
						_names[i] = csv.get(0);
					lastDate[i] = readDate[i];
					lastValue[i] = readValue[i];
					try {
						readDate[i] = formatter.parse(csv.get(2));
					} catch (ParseException e) {}
					readValue[i] = Double.parseDouble(csv.get(7));
					if(currentDate == null || currentDate.after(readDate[i]))
						currentDate = readDate[i];
				}
			}
			for(int i = _n - 1; i >= 0; i--)
				if(lastDate[i] != null)
				{
					long D1 = TimeUnit.DAYS.convert(readDate[i].getTime() - lastDate[i].getTime(),TimeUnit.MILLISECONDS);
					long Dt = -1 + TimeUnit.DAYS.convert(currentDate.getTime() - lastDate[i].getTime(),TimeUnit.MILLISECONDS);
					double P10 = readValue[i] - lastValue[i];
					double P0 = lastValue[i];
					currentRow[i] = D1 * P10 / ( D1 * P0 + Dt * P10);
				}
				else
					currentRow[i] = 0.0;
			add = true;
			for(int i = _n - 1; i >= 0; i--)
				add = add && readDate[i] != null && lastDate[i] != null;
			if(add)
			{
				raw.put(currentDate, currentRow.clone());
				if(_dataBegin == null)
					_dataBegin = currentDate;
				if(_dataEnd == null || currentDate.after(_dataEnd))
					_dataEnd = currentDate;
			}
		}
		if(raw.size() == 0)
			throw new PortfolioException("Выбранные файлы не содержат пересекающиеся временные даты.");
		_r = raw.values().toArray(new double[0][0]);
	}
}
