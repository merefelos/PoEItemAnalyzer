package main;


import javax.swing.*;

/**
 * TODO: Create/populate ID - handsome ID file (for display in the configuration list)
 * TODO: Configure general (subcategories disabled) vs. categorized view
 * CONSIDER: turn configs into a table, to list also type etc.
 * TODO: Configurable threshold to show/hide properties with a certain rating
 * TODO: Enable collecting gem properties
 */
public class Launcher
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("A Thing");
		MainForm mainForm = new MainForm(frame);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(500, 400);
		frame.setContentPane(mainForm.main);

		mainForm.initArrays();
		PoEItemAnalyzer analyzer = new PoEItemAnalyzer(mainForm);
		mainForm.setAnalyzer(analyzer);
		JMenuBar menuBar = mainForm.initMenu();
		mainForm.initPropertySettings();
		frame.setJMenuBar(menuBar);
		frame.setVisible(true);
		new Thread(analyzer).start();
		new Thread(new ClipBoarder()).start();
	}
}
