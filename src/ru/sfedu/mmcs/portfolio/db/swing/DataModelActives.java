package ru.sfedu.mmcs.portfolio.db.swing;

import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

public class DataModelActives extends AbstractTableModel {
	private static final long serialVersionUID = 4254328213046888333L;
	
	private String[] _activeNames;
	private int[] _activeIds;
	private boolean[] _choosen;
	
	public DataModelActives(TreeMap<Integer,String> actives) {
		_activeNames = new String[actives.size()];
		_activeIds = new int[_activeNames.length];
		_choosen = new boolean[_activeNames.length];
		int i = 0;
		for(Entry<Integer, String> row : actives.entrySet())
		{
			_activeNames[i] = row.getValue();
			_activeIds[i] = row.getKey();
			_choosen[i] = false;
			i ++;
		}
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public int getRowCount() {
		return _activeIds.length;
		
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
}
