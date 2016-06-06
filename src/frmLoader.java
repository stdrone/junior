import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JRadioButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.swing.MigLayout;
import ru.sfedu.mmcs.portfolio.PortfolioException;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;
import ru.sfedu.mmcs.portfolio.loaders.DataLoaderDB;
import ru.sfedu.mmcs.portfolio.loaders.DataLoaderManual;
import ru.sfedu.mmcs.portfolio.sources.SourcePrices;

import javax.swing.ButtonGroup;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;


public class frmLoader extends JDialog {

	private static final long serialVersionUID = 8457405360475510348L;
	private final JPanel contentPanel = new JPanel();
	private JLabel txtCSVFile;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel pnManual;
	private JPanel pnDB;
	private JSpinner spnVariables;
	private appMain _appMain;
	private SourcePrices _prices;

	private void enablePanel(JPanel pnPanel, boolean enable)
	{
		Component[] components = pnPanel.getComponents();
		for (int i = 0; i < components.length; ++i)
		{
			components[i].setEnabled(enable);
		}
	}
	
	private void on_change()
	{
		enablePanel(pnManual, buttonGroup.getSelection().getActionCommand() == "Manual");
		enablePanel(pnDB, buttonGroup.getSelection().getActionCommand() == "DB");
	}

	/**
	 * Create the dialog.
	 * @param appMain 
	 */
	public frmLoader(appMain appMain) {
		setResizable(false);
		setTitle("Новые исходные данные");
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		_appMain = appMain;
		_prices = new SourcePrices();
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 426, 292);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JRadioButton rbnManual = new JRadioButton("<html>Ввести ковариационную матрицу и матрицу доходностей вручную</html>");
			rbnManual.setSelected(true);
			rbnManual.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					on_change();
				}
			});
			contentPanel.setLayout(new MigLayout("", "[grow]", "[26px][33px][34px][37px][]"));
			{
				JPanel panel = new JPanel();
				contentPanel.add(panel, "cell 0 0,grow");
				panel.setLayout(new BorderLayout(0, 0));
				{
					JLabel label = new JLabel("<html>Выберите способ задания условий задачи поиска минимальной границы</html>");
					panel.add(label);
				}
			}
			rbnManual.setActionCommand("Manual");
			buttonGroup.add(rbnManual);
			contentPanel.add(rbnManual, "cell 0 1,grow");
		}
		{
			pnManual = new JPanel();
			contentPanel.add(pnManual, "cell 0 2,grow");
			pnManual.setLayout(new MigLayout("", "[grow][55px]", "[32px]"));
			{
				JLabel lblNewLabel = new JLabel("Количество ценных бумаг");
				pnManual.add(lblNewLabel, "cell 0 0,grow");
			}
			{
				spnVariables = new JSpinner();
				spnVariables.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				pnManual.add(spnVariables, "cell 1 0,grow");
			}
		}
		{
			JRadioButton rbnDB = new JRadioButton("<html>Рассчитать матрицы ковариации и доходностей на основании выбранных активов</html>");
			rbnDB.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					on_change();
				}
			});
			rbnDB.setActionCommand("DB");
			buttonGroup.add(rbnDB);
			contentPanel.add(rbnDB, "cell 0 3,grow");
		}
		{
			pnDB = new JPanel();
			contentPanel.add(pnDB, "cell 0 4,grow");
			pnDB.setLayout(new MigLayout("", "[grow][55px]", "[23px]"));
			{
				{
					txtCSVFile = new JLabel("Нет выбранных активов");
					pnDB.add(txtCSVFile, "cell 0 0,alignx center,growy,aligny center");
				}
			}
			JButton btnNewButton = new JButton("Обзор");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frmDataViewEdit aEdit = new frmDataViewEdit(_prices);
					aEdit.setVisible(true);
					_prices = aEdit.getPrices();
					txtCSVFile.setText((_prices.getCountActives() == 0)
							? "Не выбрано ни одного актива"
							: String.format("Выбрано активов %d", _prices.getCountActives()).toString());
				}
			});
			pnDB.add(btnNewButton, "cell 1 0,alignx left,aligny top");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u041F\u0440\u043E\u0434\u043E\u043B\u0436\u0438\u0442\u044C");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						DataLoader data = null;
						switch(buttonGroup.getSelection().getActionCommand())
						{
						case "Manual":
							data = new DataLoaderManual(getVariables());
							break; 
						case "DB":
							try {
								data = new DataLoaderDB(_prices);
							} catch(PortfolioException ex) {
								JOptionPane.showMessageDialog(contentPanel, ex.getMessage(), "Ошибка при загрузке файла", JOptionPane.ERROR_MESSAGE);
								return;
							}
							break;
						}
						if(data != null)
						{
							frmEditior editor = new frmEditior(data);
							editor.setVisible(true);
							data = editor.getData();
							if(data != null)
							{
								try
								{
									_appMain.setData(data);
									dispose();
								}
								catch(PortfolioException ex)
								{
									JOptionPane.showMessageDialog(contentPanel, ex.getMessage(), "Ошибка при расчете", JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("\u041E\u0442\u043C\u0435\u043D\u0430");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		on_change();
	}
	protected int getVariables() {
		return (int) spnVariables.getValue();
	}
}
