package ru.sfedu.mmcs.portfolio.swing;

import ru.sfedu.mmcs.portfolio.sources.SourceCovariance;

public class DataModelCovariance extends javax.swing.table.AbstractTableModel {

	private static final long serialVersionUID = 2491638031047147915L;
	protected SourceCovariance _data;
	
	public DataModelCovariance(SourceCovariance data)
	{
		_data = data;
	}
	
	@Override
	public int getColumnCount() {
		return _data.getCountVariables() + 1;
	}

	@Override
	public int getRowCount() {
		return _data.getCountVariables();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return (columnIndex == 0) ? String.valueOf(rowIndex + 1) : _data.get(rowIndex, columnIndex - 1);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return column > 0;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int columnIndex) {
		return (columnIndex == 0) ? String.class : Double.class;
	}
	
	@Override
	public String getColumnName(int column) {
		return (column > 0) ? _data.getName(column - 1) : null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if((columnIndex > 0) && (columnIndex - 1 < _data.getCountVariables()))
		{
			_data.set(rowIndex, columnIndex - 1, (Double)aValue);
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
	}
	
}
