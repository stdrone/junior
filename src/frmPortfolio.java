import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import ru.sfedu.mmcs.portfolio.AnalyzerPortfolio;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;
import ru.sfedu.mmcs.portfolio.swing.DataModelPortfolio;
import ru.sfedu.mmcs.portfolio.swing.DecimalFormatRenderer;

import javax.swing.JTable;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;

public class frmPortfolio extends JDialog {

	private static final long serialVersionUID = -3134187366450315499L;
	private final JPanel contentPanel = new JPanel();
	private JTable _tabPortfolio;
	private AnalyzerPortfolio _data;
	private JButton _calcButton;

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings("serial")
	public frmPortfolio(AnalyzerPortfolio data) {
		setTitle("Рассчитанные портфели");
		setResizable(false);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		_data = data;
		
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			_tabPortfolio = new JTable() {
				public String getToolTipText(MouseEvent e) {
					java.awt.Point p = e.getPoint();
					int rowIndex = rowAtPoint(p);
					int colIndex = columnAtPoint(p);
					if(rowIndex >= 0 && colIndex >= 0)
						return (String) getValueAt(rowIndex, colIndex).toString();
					return null;
				}
			};
			_tabPortfolio.setFillsViewportHeight(true);
			_tabPortfolio.getTableHeader().setReorderingAllowed(false);
			if(_data != null)
				_tabPortfolio.setModel(new DataModelPortfolio(_data.getSource()));
			_tabPortfolio.getColumnModel().getColumn(0).setResizable(false);
			_tabPortfolio.getColumnModel().getColumn(0).setPreferredWidth(270);
			for(int i = _tabPortfolio.getColumnCount() - 1; i > 0; i--)
			{
				_tabPortfolio.getColumnModel().getColumn(i).setResizable(false);
				_tabPortfolio.getColumnModel().getColumn(i).setCellRenderer( new DecimalFormatRenderer() );
			}
			JScrollPane scrollPane = new JScrollPane(_tabPortfolio);
			contentPanel.add(scrollPane);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				if(data.haveFuture()) {
					_calcButton = new JButton("Прогноз");
					_calcButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							DataLoader loader = _data.getLoader();
							frmDateChooser dtChoose = new frmDateChooser(loader.getAnalyzeEnd(), loader.getDataEnd());
							dtChoose.setVisible(true);
							if(dtChoose.getBegin() != null && dtChoose.getEnd() != null)
							{
								int row = Math.max(_tabPortfolio.getSelectedRow(), 0) ;
								frmValueChart chart = new frmValueChart(_data, _data.getSource().getPortfolio(row),
										dtChoose.getBegin(), dtChoose.getEnd());
								chart.setVisible(true);
							}
						}
					});
					buttonPane.add(_calcButton);
				}
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
