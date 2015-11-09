package main.listeners;

import main.PoEItemAnalyzer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Anna on 04/11/2015.
 */
public class SettingsSaveListener implements ActionListener
{
	public SettingsSaveListener(JDialog dialog, PoEItemAnalyzer analyzer)
	{
		this.dialog = dialog;
		this.analyzer = analyzer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		this.dialog.dispose();
	}

	JDialog         dialog;
	PoEItemAnalyzer analyzer;
}
