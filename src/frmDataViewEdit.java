import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;
import ru.sfedu.mmcs.portfolio.db.swing.DataModelPrices;
import ru.sfedu.mmcs.portfolio.sources.SourcePrices;

public class frmDataViewEdit extends JDialog implements ChangeListener {

	private static final long serialVersionUID = 1330921098227284835L;
	private final JPanel contentPanel = new JPanel();
	private JTable _table;
	private JDatePickerImpl _pDateFrom,_pDateTo;
	private SourcePrices _prices;

	/**
	 * Create the dialog.
	 */
	public frmDataViewEdit(SourcePrices prices) {
		_prices = prices;
		if(_prices == null) {
			_prices = new SourcePrices();
		}
		setTitle("Просмотр и изменение данных");
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		setModal(true);
		setBounds(100, 100, 870, 375);
		getContentPane().setLayout(new BorderLayout());
		{
			Properties p = new Properties();
			p.put("text.today", "Сегодня");
			p.put("text.month", "Месяц");
			p.put("text.year", "Год");
			
			UtilDateModel model = new UtilDateModel();
			model.addChangeListener(this);
			model.setValue(_prices.getDate(0));
			JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
			_pDateFrom = new JDatePickerImpl(datePanel, new DateLabelFormatter());
			_pDateFrom.setTextEditable(true);
			
			model = new UtilDateModel();
			model.addChangeListener(this);
			datePanel = new JDatePanelImpl(model, p);
			_pDateTo = new JDatePickerImpl(datePanel, new DateLabelFormatter());
			_pDateTo.setTextEditable(true);
			model.setValue(_prices.getDate((int)_prices.getCountDates() - 1));

			JPanel filterPane = new JPanel();
			getContentPane().add(filterPane, BorderLayout.NORTH);
			filterPane.add(new JLabel("Дата от"));
			filterPane.add(_pDateFrom);
			filterPane.add(new JLabel("дата по"));
			filterPane.add(_pDateTo);
			JButton activesButton = new JButton("Выбор активов");
			activesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frmActiveChooser aChooser = new frmActiveChooser(((DataModelPrices)_table.getModel()).getSource().getActivesData());
					aChooser.setVisible(true);
					if(aChooser.isOk())
					{
						SourcePrices prices = SQLiteData.getPrices(aChooser.getActives(), (Date)_pDateFrom.getModel().getValue(), (Date)_pDateTo.getModel().getValue());
						setModel(new DataModelPrices(prices));
					}
				}
			});
			filterPane.add(activesButton);
			filterPane.setLayout(new BoxLayout(filterPane, BoxLayout.X_AXIS));
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			DataModelPrices model = new DataModelPrices(_prices);
			contentPanel.setLayout(new BorderLayout(0, 0));
			
			_table = new JTable();
			setModel(model);
			_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			JScrollPane scrollPane = new JScrollPane(_table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
						_prices = ((DataModelPrices)_table.getModel()).getSource();
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
	
	private void setModel(DataModelPrices model) {
		_table.setModel(model);
		for(int i = _table.getColumnCount() - 1; i > 0; --i) {
			_table.getColumnModel().getColumn(i).setPreferredWidth(50);
		}
		_table.getColumnModel().getColumn(0).setPreferredWidth(100);
	}
	
	public SourcePrices getPrices() {
		return _prices; 
	}

	private Date _dateFrom = new Date();
	private Date _dateTo = new Date();
	@Override
	public void stateChanged(ChangeEvent arg0) {
		if(_table != null &&
				(!_dateFrom.equals(_pDateFrom.getModel().getValue()) || !_dateTo.equals(_pDateTo.getModel().getValue()))) {
			SourcePrices prices = SQLiteData.getPrices(((DataModelPrices)_table.getModel()).getSource().getActivesData(), (Date)_pDateFrom.getModel().getValue(), (Date)_pDateTo.getModel().getValue());
			setModel(new DataModelPrices(prices));
			_dateFrom = (Date) _pDateFrom.getModel().getValue();
			_dateTo = (Date) _pDateTo.getModel().getValue();
		}
	}

	@SuppressWarnings("serial")
	private class DateLabelFormatter extends AbstractFormatter {

		private String datePattern = "dd.MM.yyyy";
		private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
		
		@Override
		public Object stringToValue(String text) throws ParseException {
			return dateFormatter.parseObject(text);
		}

		@Override
		public String valueToString(Object value) throws ParseException {
			if (value != null) {
				Calendar cal = (Calendar) value;
				return dateFormatter.format(cal.getTime());
			}
			
			return "";
		}

	}
}
