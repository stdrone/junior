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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;
import ru.sfedu.mmcs.portfolio.db.swing.DataModelActives;

import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.event.InputMethodListener;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.InputMethodEvent;

public class frmActiveChooser extends JDialog {

	private static final long serialVersionUID = -4878939282001533318L;
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JTextField txtFilter;
	private TableRowSorter<DataModelActives> _sorter;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			frmActiveChooser dialog = new frmActiveChooser();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public frmActiveChooser() {
		setTitle("Выбор активов");
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		setBounds(100, 100, 370, 317);
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
				txtFilter = new JTextField();
				txtFilter.getDocument().addDocumentListener(new DocumentListener() {
					
					private void filter() {
						RowFilter<DataModelActives, Object> rf = null;
						try {
							rf = RowFilter.regexFilter(txtFilter.getText(), 0);
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
				filterPane.add(txtFilter);
			}
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			DataModelActives model = new DataModelActives(SQLiteData.getActives());
			_sorter = new TableRowSorter<DataModelActives>(model);
			List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
			sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
			_sorter.setSortKeys(sortKeys); 
			
			JScrollPane scrollPane = new JScrollPane();
			table = new JTable();
			table.setModel(model);
			table.setRowSorter(_sorter);
			scrollPane.add(table);
			contentPanel.add(scrollPane);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
