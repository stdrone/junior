import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import ru.sfedu.mmcs.portfolio.AnalyzerData;
import ru.sfedu.mmcs.portfolio.AnalyzerPortfolio;
import ru.sfedu.mmcs.portfolio.Portfolio;
import ru.sfedu.mmcs.portfolio.db.ImportCSV;
import ru.sfedu.mmcs.portfolio.loaders.DataLoader;
import ru.sfedu.mmcs.portfolio.swing.chart.EntityClickListner;
import ru.sfedu.mmcs.portfolio.swing.chart.EventEntityClick;
import ru.sfedu.mmcs.portfolio.swing.chart.JFrontierChart;

import java.awt.Toolkit;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

public class appMain {

	private JFrame _frame;
	private AnalyzerData _data = new AnalyzerData();
	private JFrontierChart _chartPanel;
	private final ButtonGroup grpDataExist = new ButtonGroup();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ru.sfedu.mmcs.portfolio.db.SQLiteConnection.db();
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					appMain window = new appMain();
					window._frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public appMain() {
		initialize();
	}
	
	

	private void updateChart()
	{
		if(_chartPanel == null)
		{
			Container panel = _frame.getContentPane();
			_chartPanel = JFrontierChart.createFrontierChartPanel("Зависимость риска от доходности", _data);
			_chartPanel.Events.addEventListner(new EntityClickListner() {
				public void entityClicked(EventEntityClick e) {
					analyzePortfolio(e.getPortfolio());
				}});
			panel.add(_chartPanel);
		}
		else
		{
			_chartPanel.refresh(_data);
		}
		_frame.revalidate();
		_frame.repaint();
	}
	
	private void loadData()
	{
		frmLoader loader = new frmLoader(this);
		loader.setVisible(true);
		updateChart();
		Enumeration<AbstractButton> elms = grpDataExist.getElements();
		while(elms.hasMoreElements())
			elms.nextElement().setEnabled(_data.getLoader() != null);
	}
	
	private void editData()
	{
		DataLoader data = _data.getLoader();
		frmEditior editor = new frmEditior(data);
		editor.setVisible(true);
		setData(editor.getData());
		updateChart();
	}
	
	private void analyzePortfolio(Portfolio portfolio) {
		AnalyzerPortfolio data = new AnalyzerPortfolio();
		data.loadData(_data.getLoader(), portfolio);
		frmPortfolio view = new frmPortfolio(data);
		view.setVisible(true);
	}
	
	private void saveData() {
		FileDialog fileChooser = new FileDialog(_frame,"Сохранение данных",FileDialog.SAVE	);
		fileChooser.setFile("*.junc");
		fileChooser.setVisible(true);
		File[] files = fileChooser.getFiles();
		if (files.length > 0)
		{
			File file = files[0];
			ObjectOutputStream fileOut = null;
			try {
				fileOut = new ObjectOutputStream(new FileOutputStream(file.getAbsolutePath()));
				fileOut.writeObject(_data.getLoader());
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(_frame, ex.getMessage(), "Ошибка при сохранении файла", JOptionPane.ERROR_MESSAGE);
			}
			finally
			{
				try {
					if(fileOut != null)
						fileOut.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_frame = new JFrame();
		_frame.setTitle("Учебный пакет для задачи формирования оптимального инвестиционного портфеля");
		_frame.setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		_frame.setBounds(100, 100, 1025, 630);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		_frame.setJMenuBar(menuBar);
		
		JMenu mnTask = new JMenu("Задача");
		menuBar.add(mnTask);
		
		JMenuItem mnOpenData = new JMenuItem("Новые исходные данные");
		mnTask.add(mnOpenData);
		
		JMenuItem nmOpen = new JMenuItem("Загрузить исходные данные");
		nmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDialog fileChooser = new FileDialog(_frame,"Загрузка исходных данных",FileDialog.LOAD);
				fileChooser.setFile("*.junc");
				fileChooser.setVisible(true);
				File[] file = fileChooser.getFiles();
				if (file.length > 0) {
					ObjectInputStream fileIn = null;
					try {
						fileIn = new ObjectInputStream(new FileInputStream(file[0].getAbsolutePath()));
						DataLoader data = (DataLoader) fileIn.readObject();
						_data.loadData(data);
					} catch (StreamCorruptedException ex) {
						JOptionPane.showMessageDialog(_frame, "Неверный формат файла", "Ошибка при загрузке файла", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (ClassNotFoundException ex) {
						JOptionPane.showMessageDialog(_frame, ex.getMessage(), "Ошибка при загрузке файла", JOptionPane.ERROR_MESSAGE);
						return;
					} catch (IOException ex) {
						JOptionPane.showMessageDialog(_frame, ex.getMessage(), "Ошибка при загрузке файла", JOptionPane.ERROR_MESSAGE);
						return;
					}
					finally
					{
						try {
							if(fileIn != null)
								fileIn.close();
						} catch (IOException ex) {
						}
					}
				}
			}
		});
		mnTask.add(nmOpen);
		
		JMenuItem mnEditData = new JMenuItem("Изменить исходные данные");
		mnEditData.setEnabled(false);
		mnTask.add(mnEditData);
		grpDataExist.add(mnEditData);
		
		JMenuItem mnSaveData = new JMenuItem("Сохранить исходные данные");
		mnSaveData.setEnabled(false);
		mnTask.add(mnSaveData);
		grpDataExist.add(mnSaveData);
		
		mnTask.addSeparator();
		JMenuItem mnExit = new JMenuItem("\u0412\u044B\u0445\u043E\u0434");
		mnTask.add(mnExit);
		mnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		mnSaveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveData();
			}
		});
		mnEditData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editData();
			}
		});
		mnOpenData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				loadData();
			}
		});
		
		JMenu mnData = new JMenu("\u0414\u0430\u043D\u043D\u044B\u0435");
		JMenuItem menuImport = new JMenuItem("Импорт данных");
		menuImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						FileDialog fileChooser = new FileDialog(_frame,"Загрузка данных",FileDialog.LOAD);
						fileChooser.setMultipleMode(true);
						fileChooser.setFile("*.csv");
						fileChooser.setVisible(true);
						File[] files = fileChooser.getFiles();
						ImportCSV imp = new ImportCSV();
						imp.addListener(new ActionListener() {
							
							String _oldTitle = _frame.getTitle(); 
							@Override
							public void actionPerformed(ActionEvent e) {
								if(e.getActionCommand().compareTo("") == 0)
									_frame.setTitle(_oldTitle);
								else
									_frame.setTitle(String.format("%s - %s", _oldTitle, e.getActionCommand()));
							}
						});
						for(File file : files)
							imp.Import(file);
					}
				});
				thread.start();
			}
		});
		mnData.add(menuImport);
		menuBar.add(mnData);
	}

	public void setData(DataLoader data)
	{
		if(data != null)
			_data.loadData(data);
	}
	
	public DataLoader getData()
	{
		return _data.getLoader();
	}
}
