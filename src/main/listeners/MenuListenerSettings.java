package main.listeners;

import main.MainForm;
import main.PoEItemAnalyzer;
import main.SettingsDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Anna on 04/11/2015.
 */
public class MenuListenerSettings implements ActionListener
{
	public MenuListenerSettings(PoEItemAnalyzer analyzer, MainForm mainForm)
	{
		this.analyzer = analyzer;
		this.mainForm = mainForm;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Open settings window
		SettingsDialog settingsDialog = new SettingsDialog(this.analyzer, this.mainForm);


		settingsDialog.setVisible(true);
	}

	PoEItemAnalyzer analyzer;
	MainForm        mainForm;
}
