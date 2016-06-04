package ru.sfedu.mmcs.portfolio.db;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteConnection implements Closeable {
	private Connection _conn;
	private Statement _stmt;
	
	protected SQLiteConnection() {
		String path = SQLiteConnection.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		//String decodedPath = URLDecoder.decode(path, "UTF-8");
		 try {
			_conn = new org.sqlite.SQLiteConnection(path, "junior.db");
			_stmt = _conn.createStatement();
			_stmt.execute("PRAGMA foreign_keys = ON");
		} catch (SQLException e) {
			// TODO: Ошибка при открытии базы
			e.printStackTrace();
		}
		check01(); 
	}
	
	private static SQLiteConnection _db = new SQLiteConnection();
	
	public static SQLiteConnection db() {
		return _db;
	}
	
	private void check01() {
		try {
			ResultSet count = _stmt.executeQuery("SELECT count(*) cnt FROM sqlite_master WHERE type = 'table' AND name = 'active'");
			count.next();
			if(count.getInt("cnt") == 0)
			{
				_stmt.executeUpdate("CREATE TABLE active (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT NOT NULL)");
				_stmt.executeUpdate("CREATE TABLE price (DATE INT NOT NULL, ACTIVE INT NOT NULL, PRICE REAL, PRICE_NEW REAL, FOREIGN KEY(ACTIVE) REFERENCES ACTIVE(ID), PRIMARY KEY (DATE, ACTIVE))");
				_stmt.executeUpdate("CREATE INDEX price_da ON price (DATE ASC,ACTIVE ASC);");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return _conn.prepareStatement(sql);
	}

	@Override
	public void close() throws IOException {
		try {
			_stmt.close();
			_conn.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
	
	public void startTransaction() throws SQLException {
		_conn.setAutoCommit(false);
	}
	
	public void commit() throws SQLException {
		_conn.commit();
		_conn.setAutoCommit(false);
	}
}
