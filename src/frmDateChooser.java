import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import java.awt.GridLayout;
import javax.swing.JLabel;

public class frmDateChooser extends JDialog implements ChangeListener {

	private static final long serialVersionUID = -8897920467320758345L;
	private final JPanel contentPanel = new JPanel();
	private Date _dateMin, _dateMax;
	private JDatePickerImpl _pDateFrom,_pDateTo;
	private JButton _okButton;
	private JLabel _lblPeriod;

	public frmDateChooser(Date dateFrom, Date dateTo) {
		setTitle("Выбор периода");
		setModal(true);
		_dateMin = dateFrom;
		_dateMax = dateTo;
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		setBounds(100, 100, 357, 164);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(3, 0, 0, 0));
		{
			DateFormat format = DateFormat.getDateInstance();
			JLabel lblAvailDate = new JLabel(String.format("Допустимые даты от %s до %s", format.format(dateFrom), format.format(dateTo)).toString());
			contentPanel.add(lblAvailDate);
		}
		_lblPeriod = new JLabel("Выбран период");
		_okButton = new JButton("OK");
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel);
			panel.setLayout(new GridLayout(1, 0, 0, 0));
			
			Properties p = new Properties();
			p.put("text.today", "Сегодня");
			p.put("text.month", "Месяц");
			p.put("text.year", "Год");
			
			UtilDateModel model = new UtilDateModel();
			model.addChangeListener(this);
			model.setValue(_dateMin);
			JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
			_pDateFrom = new JDatePickerImpl(datePanel, new DateLabelFormatter());
			_pDateFrom.setTextEditable(true);
			panel.add(_pDateFrom);
			
			model = new UtilDateModel();
			model.addChangeListener(this);
			datePanel = new JDatePanelImpl(model, p);
			_pDateTo = new JDatePickerImpl(datePanel, new DateLabelFormatter());
			_pDateTo.setTextEditable(true);
			model.setValue(_dateMax);
			panel.add(_pDateTo);
		}
		contentPanel.add(_lblPeriod);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				_okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				_okButton.setActionCommand("OK");
				buttonPane.add(_okButton);
				getRootPane().setDefaultButton(_okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						_pDateFrom.getModel().setValue(null);
						_pDateTo.getModel().setValue(null);
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				//buttonPane.add(cancelButton);
			}
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

	private boolean changed = true;
	@Override
	public void stateChanged(ChangeEvent arg0) {
		if(arg0.getSource() instanceof UtilDateModel) {
			if(changed)
			{
				changed = false;
				UtilDateModel model = (UtilDateModel) arg0.getSource();
				Date date = model.getValue();
				if(date != null && date.before(_dateMin))
					model.setValue(_dateMin);
				if(date != null && date.after(_dateMax))
					model.setValue(_dateMax);
			}
			changed = true;
			if(_pDateFrom != null && _pDateTo != null)
			{
				Date dateFrom = (Date)_pDateFrom.getModel().getValue(),
						dateTo = (Date)_pDateTo.getModel().getValue();
				if(dateFrom != null && dateTo != null && !dateFrom.equals(dateTo))
				{
					_okButton.setEnabled(true);
					_lblPeriod.setText(String.format("Выбран период %d дня(дней)",
							TimeUnit.DAYS.convert(dateTo.getTime() - dateFrom.getTime(),TimeUnit.MILLISECONDS)));
				}
				else
				{
					_okButton.setEnabled(false);
					_lblPeriod.setText("");
				}
			}
		}
	}
	
	public Date getBegin() {
		return (Date)_pDateFrom.getModel().getValue();
	}
	public Date getEnd() {
		return (Date)_pDateTo.getModel().getValue();
	}
	public void setBegin(Date date) {
		((UtilDateModel)_pDateFrom.getModel()).setValue(date);
	}
	public void setEnd(Date date) {
		((UtilDateModel)_pDateTo.getModel()).setValue(date);
	}
}
