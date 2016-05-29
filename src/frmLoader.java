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
import ru.sfedu.mmcs.portfolio.db.SQLiteData;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;
import ru.sfedu.mmcs.portfolio.loaders.DataLoaderDB;
import ru.sfedu.mmcs.portfolio.loaders.DataLoaderManual;

import javax.swing.ButtonGroup;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import java.awt.event.ActionListener;
import java.util.TreeMap;
import java.awt.event.ActionEvent;
import java.awt.Toolkit;


public class frmLoader extends JDialog {

	private static final long serialVersionUID = 8457405360475510348L;
	private final JPanel contentPanel = new JPanel();
	private JLabel txtCSVFile;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JPanel pnManual;
	private JPanel pnCSV;
	private JSpinner spnVariables;
	private JSpinner spnEqations;
	private appMain _appMain;
	private TreeMap<Integer,String> _actives;

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
		enablePanel(pnCSV, buttonGroup.getSelection().getActionCommand() == "CSV");
	}

	/**
	 * Create the dialog.
	 * @param appMain 
	 */
	public frmLoader(appMain appMain) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		_appMain = appMain;
		_actives = new TreeMap<Integer,String>();
		setResizable(false);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 514, 257);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JRadioButton rbnManual = new JRadioButton("Ввести ковариационную матрицу, матрицу доходностей и ограничения вручную");
			rbnManual.setSelected(true);
			rbnManual.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					on_change();
				}
			});
			contentPanel.setLayout(new MigLayout("", "[484px]", "[26px][33px][34px][37px]"));
			{
				JPanel panel = new JPanel();
				contentPanel.add(panel, "cell 0 0,grow");
				panel.setLayout(new BorderLayout(0, 0));
				{
					JLabel label = new JLabel("Выберите способ задания условий задачи поиска минимальной границы");
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
			pnManual.setLayout(new MigLayout("", "[105px][101px][117px][106px]", "[32px]"));
			{
				JLabel lblNewLabel = new JLabel("\u041A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E \u043F\u0435\u0440\u0435\u043C\u0435\u043D\u043D\u044B\u0445");
				pnManual.add(lblNewLabel, "cell 0 0,grow");
			}
			{
				spnVariables = new JSpinner();
				spnVariables.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
				pnManual.add(spnVariables, "cell 1 0,grow");
			}
			{
				JLabel lblNewLabel_1 = new JLabel("\u041A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E \u043E\u0433\u0440\u0430\u043D\u0438\u0447\u0435\u043D\u0438\u0439");
				pnManual.add(lblNewLabel_1, "cell 2 0,grow");
			}
			{
				spnEqations = new JSpinner();
				spnEqations.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
				pnManual.add(spnEqations, "cell 3 0,grow");
			}
		}
		{
			JRadioButton rbnCSV = new JRadioButton("<html>Рассчитать матрицы ковариации и доходностей на основании выбранных активов, ораничения задать вручную</html>");
			rbnCSV.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					on_change();
				}
			});
			rbnCSV.setActionCommand("CSV");
			buttonGroup.add(rbnCSV);
			contentPanel.add(rbnCSV, "cell 0 3,grow");
		}
		{
			pnCSV = new JPanel();
			contentPanel.add(pnCSV, "cell 0 4,grow");
			pnCSV.setLayout(new MigLayout("", "[grow][75px][grow][75px]", "[23px]"));
			{
				JLabel lblNewLabel_1 = new JLabel("\u041A\u043E\u043B\u0438\u0447\u0435\u0441\u0442\u0432\u043E \u043E\u0433\u0440\u0430\u043D\u0438\u0447\u0435\u043D\u0438\u0439");
				pnCSV.add(lblNewLabel_1, "cell 0 0,grow");
			}
			{
				JSpinner spnEqations_1 = new JSpinner();
				spnEqations_1.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
				pnCSV.add(spnEqations_1, "cell 1 0,grow");
			}
			{
				{
					txtCSVFile = new JLabel("Нет выбранных активов");
					pnCSV.add(txtCSVFile, "cell 2 0,alignx center,growy,aligny center");
				}
			}
			JButton btnNewButton = new JButton("\u041E\u0431\u0437\u043E\u0440...");
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					frmActiveChooser aChooser = new frmActiveChooser(_actives);
					aChooser.setVisible(true);
					_actives = aChooser.getActives();
					txtCSVFile.setText((_actives.size() == 0)
							? "Не выбрано ни одного актива"
							: String.format("Выбрано активов %d", _actives.size()).toString());
				}
			});
			pnCSV.add(btnNewButton, "cell 3 0,alignx left,aligny top");
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
							data = new DataLoaderManual(getVariables(), getEqations());
							break; 
						case "CSV":
							try {
								data = new DataLoaderDB(getEqations(), SQLiteData.getPrices(_actives, null, null));
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
	protected int getEqations() {
		return (int) spnEqations.getValue();
	}
}
