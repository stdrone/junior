package ru.sfedu.mmcs.portfolio.db.swing;

import java.text.SimpleDateFormat;

import javax.swing.table.AbstractTableModel;

import ru.sfedu.mmcs.portfolio.sources.SourcePrices;

@SuppressWarnings("serial")
public class DataModelPrices extends AbstractTableModel {

	private SourcePrices _prices;
	private SimpleDateFormat _dateFormat = new SimpleDateFormat("dd.MM.yy");
	
	public DataModelPrices(SourcePrices prices) {
		_prices = prices;
	}
	
	@Override
	public int getColumnCount() {
		return (int)_prices.getCountDates() + 1;
	}

	@Override
	public int getRowCount() {
		return (_prices == null) ? 0 : _prices.getCountActives();
	}

	@Override
	public Object getValueAt(int row, int col) {
		return (col == 0) ? _prices.getActive(row) : _prices.get(row, col - 1);
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
			return "Актив";
		default:
			return _dateFormat.format(_prices.getDate(column - 1));
		}
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex != 0);
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		_prices.set(rowIndex, columnIndex - 1, (Double) aValue);
	}
	
	public SourcePrices getSource() {
		return _prices;
	}
}
