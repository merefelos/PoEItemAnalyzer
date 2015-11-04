package main;


import javax.swing.*;

/**
 * TODO: Low level socket problem
 * TODO: Configurable property list (hide unwanted) - ID-based
 * TODO: Create/populate ID - handsome ID file (for display in the configuration list)
 * TODO: Configure general (subcategories disabled) vs. categorized view
 * TODO: Save subcategory property records as separate lines (e.g. an armor item would have two
 * records - one for "armor" and one for "energetic evasion armor")
 * TODO: Show property list (from menu), with sorting/filtering options, allow edit from config
 * window
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
