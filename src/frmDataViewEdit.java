import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.time.DateUtils;

import ru.sfedu.mmcs.portfolio.db.SQLiteData;
import ru.sfedu.mmcs.portfolio.db.swing.DataModelPrices;
import ru.sfedu.mmcs.portfolio.sources.SourcePrices;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;

public class frmDataViewEdit extends JDialog {

	private static final long serialVersionUID = 1330921098227284835L;
	private final JPanel contentPanel = new JPanel();
	private JTable _table;
	private SourcePrices _prices;
	private SimpleDateFormat _dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
	private Date _dateFrom = new Date(0);
	private Date _dateTo = new Date(0);
	private JLabel _lblDates;

	/**
	 * Create the dialog.
	 */
	public frmDataViewEdit(SourcePrices prices) {
		_prices = prices;
		if(_prices == null) {
			_prices = new SourcePrices();
		}
		else {
			_dateFrom = _prices.getDate(0);
			_dateTo = _prices.getDate(-1);
		}
		setTitle("Просмотр и изменение данных");
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		setModal(true);
		setBounds(100, 100, 870, 375);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel filterPane = new JPanel();
			getContentPane().add(filterPane, BorderLayout.NORTH);
			filterPane.setLayout(new MigLayout("", "[grow][100px][100px]", "[23px]"));
			JButton activesButton = new JButton("Выбор активов");
			activesButton.setHorizontalAlignment(SwingConstants.RIGHT);
			activesButton.setVerticalAlignment(SwingConstants.BOTTOM);
			activesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frmActiveChooser aChooser = new frmActiveChooser(((DataModelPrices)_table.getModel()).getSource().getActivesData());
					aChooser.setVisible(true);
					if(aChooser.isOk()) {
						SourcePrices prices = null;
						if(_dateFrom.equals(new Date(0)))
						{
							prices = SQLiteData.getPrices(aChooser.getActives(), null, null);
							_dateFrom = prices.getDate(0);
							_dateTo = DateUtils.addDays(_dateFrom, 40);
							setDates(_dateFrom, _dateTo);
						}
						else
						{
							prices = SQLiteData.getPrices(aChooser.getActives(), _dateFrom, _dateTo);
						}
						setModel(new DataModelPrices(prices));
					}
				}
			});
			_lblDates = new JLabel();
			setDates(_dateFrom, _dateTo);
			_lblDates.setHorizontalAlignment(SwingConstants.RIGHT);
			filterPane.add(_lblDates, "cell 0 0,alignx left,aligny center");
			{
				JButton periodButton = new JButton("Выбор периода");
				periodButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						frmDateChooser fDate = new frmDateChooser(null, null);
						fDate.setBegin(_dateFrom);
						fDate.setEnd(_dateTo);
						fDate.setVisible(true);
						if(fDate.isOk()) {
							setDates(fDate.getBegin(), fDate.getEnd());
							SourcePrices prices = SQLiteData.getPrices(((DataModelPrices)_table.getModel()).getSource().getActivesData(),
									_dateFrom, (Date)_dateTo);
							setModel(new DataModelPrices(prices));
						}
					}
				});
				filterPane.add(periodButton, "cell 1 0");
			}
			filterPane.add(activesButton, "cell 2 0,alignx left,aligny center");
		}
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			DataModelPrices model = new DataModelPrices(_prices);
			contentPanel.setLayout(new BorderLayout(0, 0));
			
			_table = new JTable();
			setModel(model);
			_table.getTableHeader().setReorderingAllowed(false);
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
	
	private void setDates(Date dateFrom, Date dateTo) {
		_dateFrom = dateFrom;
		_dateTo = dateTo;
		_lblDates.setText(String.format("Выбран период с %s по %s", _dateFormatter.format(_dateFrom),_dateFormatter.format(_dateTo)));
	}
	
	private void setModel(DataModelPrices model) {
		_table.setModel(model);
		for(int i = _table.getColumnCount() - 1; i > 0; --i) {
			_table.getColumnModel().getColumn(i).setPreferredWidth(65);
		}
		_table.getColumnModel().getColumn(0).setPreferredWidth(100);
	}
	
	public SourcePrices getPrices() {
		return _prices; 
	}
}
