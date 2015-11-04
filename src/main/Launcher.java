package main;


import javax.swing.*;


public class Launcher
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("A Thing");
		MainForm mainForm = new MainForm(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setContentPane(mainForm.main);

		frame.setVisible(true);

		mainForm.initArrays();
		PoEItemAnalyzer analyzer = new PoEItemAnalyzer(mainForm);
		mainForm.setAnalyzer(analyzer);
		JMenuBar menuBar = mainForm.initMenu();
		frame.setJMenuBar(menuBar);
		new Thread(analyzer).start();
		new Thread(new ClipBoarder()).start();
	}
}
