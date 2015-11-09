package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Created by Anna on 09/11/2015.
 */
public class PropertyFilterKeyAdapter extends KeyAdapter
{
	public PropertyFilterKeyAdapter(SettingsDialog settingsDialog)
	{
		this.settingsDialog = settingsDialog;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		super.keyTyped(e);
		this.settingsDialog.getCheckBoxPanel().removeAll();
		this.settingsDialog.getCheckBoxPanel().revalidate();
		String input = this.settingsDialog.getFilterInput().getText() + e.getKeyChar();
		input = input.replaceAll("[^a-zA-Z%\\+]", "");

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		int i = 0;

		for (JCheckBox checkbox : this.settingsDialog.getMainForm().getPropertyLister()
				.getPropertyListAsCheckboxes())
		{
			if (checkbox.getText().contains(input))
			{
				constraints.gridx = 0;
				constraints.gridy = i++;
				checkbox.setVisible(true);
				this.settingsDialog.getCheckBoxPanel().add(checkbox, constraints);
			}
			else
			{
				checkbox.setVisible(false);
			}
		}

		// A cruel way to trick GridBag into Laying Out its components like a decent layout should
		constraints = new GridBagConstraints();
		constraints.weighty = 1;
		constraints.gridx = 0;
		constraints.gridy = i;
		this.settingsDialog.getCheckBoxPanel().add(new JLabel(""), constraints);

		constraints = new GridBagConstraints();
		constraints.weightx = 1;
		constraints.gridx = 1;
		this.settingsDialog.getCheckBoxPanel().add(new JLabel(""), constraints);
	}

	SettingsDialog settingsDialog;
}
