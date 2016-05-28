import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ru.sfedu.mmcs.portfolio.AnalyzerPortfolio;
import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.swing.chart.JValueChart;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;

public class frmValueChart extends JDialog {

	private static final long serialVersionUID = -5484884409614392959L;
	private final JPanel contentPanel = new JPanel();

	public frmValueChart(AnalyzerPortfolio data, Portfolio portfolio, Date dateFrom, Date dateTo) {
		setModal(true);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
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
			JValueChart _chartPanel = JValueChart.createValueChartPanel("Прогноз стоимости порфеля",
					data, portfolio, dateFrom, dateTo);
			contentPanel.add(_chartPanel);
		}
		contentPanel.setLayout(new GridLayout(1, 0, 0, 0));
	}

}
