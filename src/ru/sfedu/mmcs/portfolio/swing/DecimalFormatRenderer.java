package ru.sfedu.mmcs.portfolio.swing;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class DecimalFormatRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 6905055812264669652L;
	private static final DecimalFormat formatter = new DecimalFormat( "#0.00######" );
	 
    public Component getTableCellRendererComponent(
       JTable table, Object value, boolean isSelected,
       boolean hasFocus, int row, int column) {

       // First format the cell value as required

       value = formatter.format((Number)value);

          // And pass it on to parent class

       return super.getTableCellRendererComponent(
          table, value, isSelected, hasFocus, row, column );
    }
}
