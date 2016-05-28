package ru.sfedu.mmcs.portfolio.swing;

import ru.sfedu.mmcs.portfolio.sources.SourceMeanValues;

public class DataModelMeanValues extends javax.swing.table.AbstractTableModel {

	private static final long serialVersionUID = 7549728947097223459L;
	protected SourceMeanValues _data;
	
	public DataModelMeanValues(SourceMeanValues data)
	{
		_data = data;
	}
	
	@Override
	public int getColumnCount() {
		return _data.getCountVariables();
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		if(arg1 >= 0 && arg1 <= _data.getCountVariables())
				return _data.get(arg1);
		return null;
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
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if((columnIndex >= 0) && (columnIndex - 1 < _data.getCountVariables()))
		{
			_data.set(columnIndex, (Double)aValue);
			super.setValueAt(aValue, rowIndex, columnIndex);
		}
	}
	
}
