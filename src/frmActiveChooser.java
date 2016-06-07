import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import ru.sfedu.mmcs.portfolio.db.swing.DataModelActives;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class frmActiveChooser extends JDialog {

	private static final long serialVersionUID = -4878939282001533318L;
	private final JPanel contentPanel = new JPanel();
	private JTable _table;
	private JTextField _txtFilter;
	private TableRowSorter<DataModelActives> _sorter;
	private TreeMap<Integer,String> _actives;;
	private boolean _isOk = false;

	/**
	 * Create the dialog.
	 */
	public frmActiveChooser(TreeMap<Integer,String> actives) {
		setTitle("Выбор активов");
		setIconImage(Toolkit.getDefaultToolkit().getImage(appMain.class.getResource("/res/app.png")));
		setModal(true);
		setBounds(100, 100, 392, 317);
		_actives = actives;
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel filterPane = new JPanel();
			getContentPane().add(filterPane, BorderLayout.NORTH);
			filterPane.setLayout(new BoxLayout(filterPane, BoxLayout.X_AXIS));
			{
				JLabel label = new JLabel("Наименование актива");
				filterPane.add(label);
			}
			{
				_txtFilter = new JTextField();
				_txtFilter.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent arg0) {
						if((arg0.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK) && arg0.getKeyCode() == KeyEvent.VK_SPACE) {
							if(_table.getRowCount() > 0)
							{
								int row = _table.convertRowIndexToModel(0);
								if(row >= 0) {
									TableModel model = _table.getModel();
									model.setValueAt(!(boolean)model.getValueAt(row, 0), row, 0);
									_table.repaint();
								}
							}
						}
						else if((arg0.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) && arg0.getKeyCode() == KeyEvent.VK_ENTER) {
							oK();
						}
					}
				});
				_txtFilter.getDocument().addDocumentListener(new DocumentListener() {
					
					private void filter() {
						RowFilter<DataModelActives, Object> rf = null;
						try {
							String filter = "(?ui)" + _txtFilter.getText();
							rf = RowFilter.regexFilter(filter, 1);
						} catch (java.util.regex.PatternSyntaxException e) {
							return;
						}
						_sorter.setRowFilter(rf);
					}
					
					@Override
					public void removeUpdate(DocumentEvent e) {
						filter();
					}
					
					@Override
					public void insertUpdate(DocumentEvent e) {
						filter();
					}
					
					@Override
					public void changedUpdate(DocumentEvent e) {
					}
				});
				filterPane.add(_txtFilter);
			}
			JPanel panel_eq = new JPanel();
			JButton buttonPlus = new JButton("");
			buttonPlus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					((DataModelActives)_table.getModel()).addRow(_txtFilter.getText());
				}
			});
			buttonPlus.setBorder(BorderFactory.createEmptyBorder());
			buttonPlus.setIcon(new ImageIcon(frmActiveChooser.class.getResource("/res/plus.png")));
			panel_eq.add(buttonPlus);
			JButton buttonMinus = new JButton("");
			buttonMinus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(_table.getRowCount() > 0) {
						((DataModelActives)_table.getModel()).removeRow(
								_table.convertRowIndexToModel(_table.getSelectedRow())
							);
					}
				}
			});
			buttonMinus.setBorder(BorderFactory.createEmptyBorder());
			buttonMinus.setIcon(new ImageIcon(frmActiveChooser.class.getResource("/res/minus.png")));
			panel_eq.add(buttonMinus);
			filterPane.add(panel_eq);
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			DataModelActives model = new DataModelActives(_actives);
			_sorter = new TableRowSorter<DataModelActives>(model);
			contentPanel.setLayout(new BorderLayout(0, 0));
			List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
			sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
			_sorter.setSortKeys(sortKeys); 
			_table = new JTable();
			_table.setModel(model);
			_table.setRowSorter(_sorter);
			_table.getColumnModel().getColumn(0).setMaxWidth(25);
			_table.getTableHeader().setReorderingAllowed(false);
			JScrollPane scrollPane = new JScrollPane(_table);
			contentPanel.add(scrollPane);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						oK();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void oK() {
		_actives = ((DataModelActives)_table.getModel()).getChoosen();
		_isOk = true;
		dispose();
	}
	
	public TreeMap<Integer,String> getActives() {
		return _actives; 
	}
	
	public boolean isOk() {
		return _isOk;
	}
}
