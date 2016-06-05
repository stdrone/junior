package ru.sfedu.mmcs.portfolio.swing;

import ru.sfedu.mmcs.portfolio.sources.PortfolioList;

public class DataModelPortfolio extends javax.swing.table.AbstractTableModel {

	private static final long serialVersionUID = 2491638031047147915L;
	protected PortfolioList _data;
	
	public DataModelPortfolio(PortfolioList data)
	{
		_data = data;
	}
	
	@Override
	public int getColumnCount() {
		return _data.getCountActives() + 3;
	}

	@Override
	public int getRowCount() {
		return _data.getCountPortfolios();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return _data.getPortfolioName(rowIndex);
		case 1:
			return _data.getRisk(rowIndex);
		case 2:
			return _data.getValue(rowIndex);
		default:
			return _data.getPartOf(rowIndex, columnIndex - 3);
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int columnIndex) {
		return (columnIndex == 0) ? String.class : Double.class;
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "";
		case 1:
			return "Риск";
		case 2:
			return "Доход";
		default:
			return _data.getActiveName(column - 3);
		}
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		
	}
	
}
