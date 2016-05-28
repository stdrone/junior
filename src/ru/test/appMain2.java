package ru.test;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

public class appMain2 {

	private JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					appMain2 window = new appMain2();
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
	public appMain2() {
		initialize();
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
		
		JMenuItem menuItem = new JMenuItem("Выбрать данные");
		mnData.add(menuItem);
		
		mnData.addSeparator();
		
		JMenuItem menuItem_1 = new JMenuItem("Выход");
		mnData.add(menuItem_1);
		
		JMenu menu = new JMenu("Задача");
		menuBar.add(menu);
		
		JMenu menu_1 = new JMenu("Помощь");
		menu_1.setHorizontalAlignment(SwingConstants.RIGHT);
		menuBar.add(menu_1);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{ "\u0421\u0431\u0435\u0440\u0431\u0430\u043D\u043A", null, null, null, null, null},
				{ "\u041C\u0422\u0421"								, null, null, null, null, null},
				{"\u0420\u0443\u0441\u0430\u043B"					, null, null, null, null, null},
			},
			new String[] {
				"", "01.01.16", "02.01.16", "03.01.16", "04.01.16", "05.01.16",
			}
		));
		scrollPane.setViewportView(table);
		frame.getContentPane().add(scrollPane);
		
	}
}
