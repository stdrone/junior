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
import javax.swing.table.TableRowSorter;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;
import ru.sfedu.mmcs.portfolio.db.swing.DataModelActives;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class frmActiveChooser extends JDialog {

	private static final long serialVersionUID = -4878939282001533318L;
	private final JPanel contentPanel = new JPanel();
	private JTable _table;
	private JTextField _txtFilter;
	private TableRowSorter<DataModelActives> _sorter;
	private TreeMap<Integer,String> _actives;;

	/**
	 * Create the dialog.
	 */
	public frmActiveChooser(TreeMap<Integer,String> actives) {
		setTitle("Выбор активов");
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		setModal(true);
		setBounds(100, 100, 370, 317);
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
				_txtFilter.getDocument().addDocumentListener(new DocumentListener() {
					
					private void filter() {
						RowFilter<DataModelActives, Object> rf = null;
						try {
							rf = RowFilter.regexFilter(_txtFilter.getText(), 1);
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
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			DataModelActives model = new DataModelActives(SQLiteData.getActives(),actives);
			_sorter = new TableRowSorter<DataModelActives>(model);
			contentPanel.setLayout(new BorderLayout(0, 0));
			List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
			sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
			_sorter.setSortKeys(sortKeys); 
			
			_table = new JTable();
			_table.setModel(model);
			_table.setRowSorter(_sorter);
			_table.getColumnModel().getColumn(0).setPreferredWidth(23);
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
						_actives = ((DataModelActives)_table.getModel()).getChoosen();
						dispose();
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
	
	public TreeMap<Integer,String> getActives() {
		return _actives; 
	}

}
