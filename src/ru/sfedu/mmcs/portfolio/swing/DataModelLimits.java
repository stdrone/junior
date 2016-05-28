package ru.sfedu.mmcs.portfolio.swing;

import ru.sfedu.mmcs.portfolio.sources.SourceLimits;

public class DataModelLimits extends javax.swing.table.AbstractTableModel {

	private static final long serialVersionUID = 7549728947097223459L;
	protected SourceLimits _data;
	
	public DataModelLimits(SourceLimits data)
	{
		_data = data;
	}
	
	@Override
	public int getColumnCount() {
		return _data.getCountVariables() + 2;
	}

	@Override
	public int getRowCount() {
		return _data.getCountLimits();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if(columnIndex == 0)
			return _data.getLeftLimit(rowIndex);
		if(columnIndex == _data.getCountVariables() + 1)
			return _data.getRightLimit(rowIndex);
		return _data.getVariable(rowIndex, columnIndex - 1);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int columnIndex) {
		return Double.class;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == 0)
			return "B1";
		else if(column == _data.getCountVariables() + 1)
			return "B2";
		return String.valueOf(column);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(columnIndex == 0)
			_data.setLimitLeft(rowIndex, (double)aValue);
		else if(columnIndex == _data.getCountVariables() + 1)
			_data.setLimitRight(rowIndex, (double)aValue);
		else
			_data.setVariable(rowIndex, columnIndex - 1, (double)aValue);
		super.setValueAt(aValue, rowIndex, columnIndex);
	}
	
}
