package ru.sfedu.mmcs.portfolio.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.TreeMap;

import org.apache.commons.lang3.time.DateUtils;

import ru.sfedu.mmcs.portfolio.sources.SourcePrices;

public class SQLiteData {
	public static void setPrice(Date date, String active, Double price, Double price_new) {
		try {
			PreparedStatement qry = SQLiteConnection.db().prepareStatement("SELECT id FROM active WHERE name = ?");
			qry.setString(1, active);
			ResultSet rs = qry.executeQuery();
			int id;
			if (!rs.next()) {
				qry.close();
				qry = SQLiteConnection.db().prepareStatement("INSERT INTO active(NAME) VALUES (?)");
				qry.setString(1, active);
				qry.executeUpdate();
				qry.close();
				setPrice(date, active, price, price_new);
				return;
			}
			id = rs.getInt("id");
			qry.close();
			qry = SQLiteConnection.db()
					.prepareStatement("INSERT OR REPLACE INTO price(DATE,ACTIVE,PRICE,PRICE_NEW) VALUES (?, ?, ?, ?)");
			qry.setDate(1, new java.sql.Date(date.getTime()));
			qry.setInt(2, id);
			if (price != null)
				qry.setDouble(3, price);
			if (price_new != null)
				qry.setDouble(4, price_new);
			qry.executeUpdate();
			qry.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static TreeMap<Integer, String> getActives() {
		TreeMap<Integer, String> data = new TreeMap<Integer, String>();
		try {
			PreparedStatement qry = SQLiteConnection.db().prepareStatement("SELECT id,name FROM active");
			ResultSet rs = qry.executeQuery();
			while (rs.next()) {
				data.put(rs.getInt("id"), rs.getString("name"));
			}
			qry.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public static Date getMinMaxDate(TreeMap<Integer, String> actives, boolean min) {
		Date data = null;
		for(int active : actives.keySet())
			try {
				PreparedStatement qry = SQLiteConnection.db().prepareStatement("SELECT min(date),max(date) FROM price p WHERE p.active = ? and (p.price is not null or p.price_new is not null)");
				qry.setInt(1, active);
				ResultSet rs = qry.executeQuery();
				while (rs.next()) {
					if(min) {
						Date dat = rs.getDate(1);
						if(dat != null && (data == null || dat.after(data)))
							data = dat;
					}
					else {
						Date dat = rs.getDate(2);
						if(dat != null && (data == null || dat.before(data)))
							data = dat;
					}
				}
				qry.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return data;
	}

	public static SourcePrices getPrices(TreeMap<Integer, String> actives, Date dateFrom, Date dateTo) {
		if(dateFrom == null && actives.size() > 0)
		{
			dateFrom = getMinMaxDate(actives, true);
			dateTo = DateUtils.addDays(dateFrom, 40);
		}
		SourcePrices data = new SourcePrices(actives, dateFrom, dateTo);
		try {
			for (Integer active : actives.keySet()) {
				PreparedStatement qry = SQLiteConnection.db().prepareStatement(
						"SELECT id,name,date,price,price_new FROM active a left join price p on (a.id = p.active) WHERE date between ? and ? and a.id = ? order by date, name");
				if (dateFrom == null)
					dateFrom = new Date(Long.MIN_VALUE);
				qry.setDate(1, new java.sql.Date(dateFrom.getTime()));
				if (dateTo == null)
					dateTo = new Date(Long.MAX_VALUE);
				qry.setDate(2, new java.sql.Date(dateTo.getTime()));
				qry.setInt(3, active);

				ResultSet rs = qry.executeQuery();
				while (rs.next()) {
					Double price = rs.getDouble("price");
					if (rs.wasNull())
						price = null;
					Double price_new = rs.getDouble("price_new");
					if (rs.wasNull())
						price_new = null;
					data.add(rs.getString("name"), rs.getDate("date"), price, price_new);
				}
				qry.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	
	public static void removeActive(int id) {
		try {
			SQLiteConnection.db().startTransaction();
			PreparedStatement qry = SQLiteConnection.db().prepareStatement("DELETE FROM price WHERE active = ?");
			qry.setInt(1, id);
			qry.execute();
			qry = SQLiteConnection.db().prepareStatement("DELETE FROM active WHERE id = ?");
			qry.setInt(1, id);
			qry.execute();
			SQLiteConnection.db().commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
