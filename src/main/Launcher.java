package main;


import javax.swing.*;


public class Launcher
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("A Thing");
		MainForm mainForm = new MainForm();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setContentPane(mainForm.main);

		frame.setVisible(true);

		mainForm.initArrays();
		new Thread(new PoEItemAnalyzer(mainForm)).start();
		new Thread(new ClipBoarder()).start();
	}
}
