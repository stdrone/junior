package ru.sfedu.mmcs.portfolio.db.swing;

import java.util.Date;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;

public class DataModelActives extends AbstractTableModel {
	private static final long serialVersionUID = 4254328213046888333L;
	
	private String[] _activeNames;
	private int[] _activeIds;
	private boolean[] _choosen;

	public DataModelActives(TreeMap<Integer,String> chosen) {
		init(chosen);
	}

	public DataModelActives() {
		init(new TreeMap<Integer,String>());
	}
	
	private void init(TreeMap<Integer,String> chosen) {
		TreeMap<Integer,String> actives = SQLiteData.getActives();
		_activeNames = new String[actives.size()];
		_activeIds = new int[_activeNames.length];
		_choosen = new boolean[_activeNames.length];
		int i = 0;
		for(Entry<Integer, String> row : actives.entrySet())
		{
			_activeNames[i] = row.getValue();
			_activeIds[i] = row.getKey();
			_choosen[i] = (chosen == null) ? false : chosen.containsKey(_activeIds[i]);
			i ++;
		}
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public int getRowCount() {
		return _activeIds.length;
		
	}

	public void removeRow(int row) {
		SQLiteData.removeActive(_activeIds[row]);
		init(getChoosen());
		fireTableDataChanged();
	}
	
	public void addRow(String name) {
		SQLiteData.setPrice(new Date(), name, null, null);
		init(getChoosen());
		fireTableDataChanged();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return (col == 0)
					? _choosen[row]
					: _activeNames[row];
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int columnIndex) {
		return (columnIndex == 0) ? Boolean.class : String.class;
	} 
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "";
		default:
			return "Актив";
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return true;
		default:
			return false;
		}	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		_choosen[rowIndex] = (boolean) aValue;
	}
	
	public TreeMap<Integer,String> getChoosen() {
		TreeMap<Integer,String> actives = new TreeMap<Integer,String>();
		for(int i = 0; i < _activeIds.length; i++)
			if(_choosen[i])
				actives.put(_activeIds[i], _activeNames[i]);
		return actives;
	}
}
