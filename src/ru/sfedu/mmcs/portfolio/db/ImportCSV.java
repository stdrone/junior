package ru.sfedu.mmcs.portfolio.db;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.time.DateUtils;

import ru.sfedu.mmcs.portfolio.PortfolioException;

public class ImportCSV {
	private ArrayList<ActionListener> _listeners = new ArrayList<ActionListener>();

	public void addListener(ActionListener listner) {
		_listeners.add(listner);
	}

	private void fireAction(String message) {
		ActionEvent e = new ActionEvent(this, 0, message);
		for (ActionListener a : _listeners)
			a.actionPerformed(e);
	}

	public void Import(File data) {
		try {
			Iterator<CSVRecord> isource = CSVParser.parse(data, Charset.forName("UTF-8"), CSVFormat.newFormat(','))
					.iterator();
			parse(isource);
		} catch (IOException e) {
			throw new PortfolioException(e.getLocalizedMessage());
		}

	}

	private void fillGap(String name, Date from, int days, Double priceFrom, Double priceTo) {
		for (int d = 1; d < days; d++) {
			from = DateUtils.addDays(from, 1);
			Double price = priceFrom + ((priceTo - priceFrom) / days) * d;
			SQLiteData.setPrice(from, name, null, price);
		}
	}

	private void parse(Iterator<CSVRecord> data) {
		int pricePos = 7;
		String suffix = "";
		Double lastPrice = null;
		Date lastDate = null;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatYear = new SimpleDateFormat("_MMyyyy");
		try {
			SQLiteConnection.db().startTransaction();
			while (data.hasNext()) {
				CSVRecord csv = data.next();
				if (csv.get(0).compareTo("<TICKER>") == 0) {
					for (int i = 0; i < csv.size(); i++)
						if (csv.get(i).compareTo("<CLOSE>") == 0) {
							pricePos = i;
							break;
						}
				} else {
					String name = csv.get(0) + suffix;
					Date date = null;
					try {
						date = formatter.parse(csv.get(2));
					} catch (ParseException e) {
						continue;
					}
					fireAction(String.format("%s (%s)", name, csv.get(2)));
					Double price = Double.parseDouble(csv.get(pricePos));
					if (lastDate != null) {
						long days = TimeUnit.DAYS.convert(date.getTime() - lastDate.getTime(), TimeUnit.MILLISECONDS);
						if(days >= 30)
						{
							suffix = formatYear.format(date);
							name = csv.get(0) + suffix;
							lastDate = null;
						}
						else if (days > 1)
							fillGap(name, lastDate, (int) days, lastPrice, price);
					}
					SQLiteData.setPrice(date, name, price, null);
					lastDate = date;
					lastPrice = price;
				}
			}
			fireAction("");
			SQLiteConnection.db().commit();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
