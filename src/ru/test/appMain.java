package ru.test;

import java.awt.EventQueue;
import java.awt.FileDialog;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ru.sfedu.mmcs.portfolio.db.ImportCSV;

import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

public class appMain {

	private JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					appMain window = new appMain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public appMain() {
		initialize();
	}
	
	private void chooseData() {
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Учебный пакет для задачи формирования оптимального инвестиционного портфеля");
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(".\\res\\app.png"));
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnData = new JMenu("Данные");
		menuBar.add(mnData);
		
		JMenuItem menuChoose = new JMenuItem("Выбрать данные");
		menuChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooseData();
			}
		});
		mnData.add(menuChoose);
		
		JMenuItem menuImport = new JMenuItem("Импорт данных");
		menuImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						FileDialog fileChooser = new FileDialog(frame,"Загрузка данных",FileDialog.LOAD);
						fileChooser.setMultipleMode(true);
						fileChooser.setFile("*.csv");
						fileChooser.setVisible(true);
						File[] files = fileChooser.getFiles();
						ImportCSV imp = new ImportCSV();
						imp.addListener(new ActionListener() {
							
							String _oldTitle = frame.getTitle(); 
							@Override
							public void actionPerformed(ActionEvent e) {
								if(e.getActionCommand().compareTo("") == 0)
									frame.setTitle(_oldTitle);
								else
									frame.setTitle(String.format("%s - %s", _oldTitle, e.getActionCommand()));
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
		
		mnData.addSeparator();
		
		JMenuItem menuItem_1 = new JMenuItem("Выход");
		mnData.add(menuItem_1);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(frame.getWidth() / 2);
		frame.getContentPane().add(splitPane);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				""
			}
		));
		scrollPane.setViewportView(table);
		
		JSplitPane splitPaneR = new JSplitPane();
		splitPaneR.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPaneR.setDividerLocation(frame.getHeight() / 2);
		splitPane.setRightComponent(splitPaneR);
	}
}
