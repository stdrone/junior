import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import net.miginfocom.swing.MigLayout;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;
import ru.sfedu.mmcs.portfolio.swing.DataModelCovariance;
import ru.sfedu.mmcs.portfolio.swing.DataModelLimits;
import ru.sfedu.mmcs.portfolio.swing.DataModelMeanValues;
import ru.sfedu.mmcs.portfolio.swing.DecimalFormatRenderer;

import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;


public class frmEditior extends JDialog {

	private static final long serialVersionUID = -6212371988626793009L;
	private final JPanel contentPanel = new JPanel();
	private DataLoader _dataLoader;
	private JTable _tabA;

	/**
	 * Create the dialog.
	 * @param _appMain 
	 */
	public frmEditior(DataLoader data) {
		setResizable(false);
		setTitle("Исходные данные задачи");
		_dataLoader = data;
		if(_dataLoader.haveSource())
		{
			frmDateChooser dtChoose = new frmDateChooser(data.getAnalyzeBegin(), data.getAnalyzeEnd());
			dtChoose.setVisible(true);
			if(dtChoose.getBegin() != null && dtChoose.getEnd() != null)
				_dataLoader = new DataLoader(_dataLoader, dtChoose.getBegin(), dtChoose.getEnd());
			else
				_dataLoader = null;
		}
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(appMain.class.getResource("/res/app.png")));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 405, 459);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[378px]", "[144px][164px,grow]"));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, "cell 0 0,grow");
			panel.setLayout(new MigLayout("", "[grow][grow]", "[grow][grow][grow]"));
			{
				JLabel lblV = new JLabel("Ковариационная матрица");
				panel.add(lblV, "flowx,cell 0 0 3 1,grow");
			}
			{
				JLabel lblV = new JLabel("V=");
				panel.add(lblV, "flowx,cell 0 1,grow");
				JLabel lbl = new JLabel("  ");
				panel.add(lbl, "flowx,cell 1 1,grow");
			}
			{
				JTable tabV;
				tabV = new JTable();
				tabV.setFillsViewportHeight(true);
				tabV.setRowSelectionAllowed(false);
				tabV.getTableHeader().setReorderingAllowed(false);
				if(data != null)
				{
					tabV.setModel(new DataModelCovariance(_dataLoader.getCovariance()));
					
					tabV.getColumnModel().getColumn(0).setResizable(false);
					tabV.getColumnModel().getColumn(0).setPreferredWidth(23);
					for(int i = tabV.getColumnCount() - 1; i > 0; i--)
					{
						tabV.getColumnModel().getColumn(i).setResizable(false);
						tabV.getColumnModel().getColumn(i).setCellRenderer( new DecimalFormatRenderer() );
					}
				}
				tabV.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

				JScrollPane scrollPane = new JScrollPane(tabV);
				panel.add(scrollPane, "flowx,cell 2 1,grow");
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, "cell 0 1,grow");
			panel.setLayout(new MigLayout("", "[378px]", "[128px:n:200px][grow]"));
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1, "cell 0 0,grow");
				panel_1.setLayout(new MigLayout("", "[grow]", "[grow][grow]"));
				{
					JPanel panel_eq = new JPanel();
					panel_eq.setLayout(new BoxLayout(panel_eq, BoxLayout.X_AXIS));
					JLabel lblNewLabel = new JLabel("Оганичения на переменные (b1 <= Ax <= b2)");
					panel_eq.add(lblNewLabel);
					panel_1.add(panel_eq, "flowx,cell 0 0 3 1,grow");
					JButton buttonPlus = new JButton("");
					buttonPlus.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							_dataLoader.addEquation();
							refreshTabA();
						}
					});
					buttonPlus.setBorder(BorderFactory.createEmptyBorder());
					buttonPlus.setIcon(new ImageIcon(frmEditior.class.getResource("/res/plus.png")));
					panel_eq.add(buttonPlus);
					JButton buttonMinus = new JButton("");
					buttonMinus.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(_tabA.getRowCount() > 0) {
								_dataLoader.delEquation(_tabA.getSelectedRow());
								refreshTabA();
							}
						}
					});
					buttonMinus.setBorder(BorderFactory.createEmptyBorder());
					buttonMinus.setIcon(new ImageIcon(frmEditior.class.getResource("/res/minus.png")));
					panel_eq.add(buttonMinus);
				}
				{
					_tabA = new JTable();
					_tabA.setFillsViewportHeight(true);
					_tabA.setRowSelectionAllowed(false);
					_tabA.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					_tabA.getTableHeader().setReorderingAllowed(false);
					refreshTabA();
					JScrollPane scrollPane = new JScrollPane(_tabA);
					panel_1.add(scrollPane, "flowx,cell 2 1,grow");
				}
			}
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1, "cell 0 1,grow");
				panel_1.setLayout(new MigLayout("", "[grow][grow]", "[grow][grow]"));
				{
					JLabel lblNewLabel = new JLabel("Доходности");
					panel_1.add(lblNewLabel, "flowx,cell 0 0 3 1,grow");
				}
				{
					JLabel lblNewLabel = new JLabel("m=");
					panel_1.add(lblNewLabel, "flowx,cell 0 1,grow");
				}
				{
					JTable tabM;
					tabM = new JTable();
					tabM.setRowSelectionAllowed(false);
					tabM.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					tabM.getTableHeader().setReorderingAllowed(false);
					if(data != null)
					{
						tabM.setModel(new DataModelMeanValues(_dataLoader.getMeanValues()));
						
						for(int i = tabM.getColumnCount() - 1; i >= 0; i--)
						{
							tabM.getColumnModel().getColumn(i).setResizable(false);
							tabM.getColumnModel().getColumn(i).setCellRenderer( new DecimalFormatRenderer() );
						}
					}
					JScrollPane scrollPane = new JScrollPane(tabM);
					panel_1.add(scrollPane, "cell 0 1");
				}
			}
		}
		{		
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("\u0420\u0430\u0441\u0441\u0447\u0438\u0442\u0430\u0442\u044C");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
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
						_dataLoader = null;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	@Override
	public void setVisible(boolean b) {
		if(_dataLoader != null)
			super.setVisible(b);
	}
	
	private void refreshTabA() {
		if(_dataLoader != null)
		{
			_tabA.setModel(new DataModelLimits(_dataLoader.getLimits()));
			for(int i = _tabA.getColumnCount() - 1; i >= 0 ; i--)
			{
				_tabA.getColumnModel().getColumn(i).setResizable(false);
				_tabA.getColumnModel().getColumn(i).setCellRenderer( new DecimalFormatRenderer() );
			}
		}
	}
	
	public DataLoader getData()
	{
		return _dataLoader;
	}
}
