package main;

import com.sun.xml.internal.ws.api.PropertySet;
import main.PoEItemAnalyzer;
import main.listeners.SettingsSaveListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsDialog extends JDialog
{
	public SettingsDialog(PoEItemAnalyzer analyzer, MainForm mainForm)
	{
		this.analyzer = analyzer;
		this.mainForm = mainForm;
		setContentPane(contentPane);
		setSize(300, 400);
		setLocation(0, 500);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		this.compilePropertyList();

		// Closing operations
		buttonOK.addActionListener(new SettingsSaveListener(this, analyzer));
		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
	}

	/**
	 * Display the list of all available properties
	 */
	private void compilePropertyList()
	{
		List<JCheckBox> propertyList = mainForm.getPropertyLister().getPropertyListAsCheckboxes();

		JPanel panel = new JPanel();
		for (JCheckBox checkBox : propertyList)
		{
			panel.add(checkBox);
		}
		panel.setLayout(new GridLayout(propertyList.size(), 1));
		this.propertyScrollPane.getViewport().add(panel);
	}

	private JPanel          contentPane;
	private JButton         buttonOK;
	private JButton         buttonCancel;
	private JCheckBox       enableSubCategorizedViewCheckBox;
	private JTabbedPane     settingsTabbedPane;
	private JScrollPane     propertyScrollPane;
	private PoEItemAnalyzer analyzer;
	private MainForm        mainForm;
}
