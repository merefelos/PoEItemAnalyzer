package main;

import main.listeners.SettingsSaveListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SettingsDialog extends JDialog
{
	public SettingsDialog(PoEItemAnalyzer analyzer, MainForm mainForm)
	{
		this.analyzer = analyzer;
		this.mainForm = mainForm;
		this.setTitle("Property settings");
		setContentPane(contentPane);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();

		setSize(500, (dimension.height / 3) * 2 - 60);
		setLocation(0, dimension.height / 3);
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
	public void compilePropertyList()
	{
		final List<JCheckBox> propertyList = mainForm.getPropertyLister()
				.getPropertyListAsCheckboxes();

		this.checkBoxPanel = new JPanel();

		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		gridBagLayout.setConstraints(this.checkBoxPanel, constraints);
		this.checkBoxPanel.setLayout(gridBagLayout);
		int i = 0;
		for (JCheckBox checkBox : propertyList)
		{
			constraints.gridx = 0;
			constraints.gridy = i++;
			checkBox.setVisible(true);
			this.checkBoxPanel.add(checkBox, constraints);
		}
		constraints = new GridBagConstraints();
		constraints.weighty = 1;
		this.checkBoxPanel.add(new JLabel(""), constraints);

		this.propertyScrollPane.getViewport().add(this.checkBoxPanel);

		// Filter by label part
		this.filterInput.addKeyListener(new PropertyFilterKeyAdapter(this));
		this.selectAllButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (JCheckBox checkBox : propertyList)
				{
					if (checkBox.isVisible())
					{
						checkBox.setSelected(true);
					}
				}
			}
		});
		this.deselectAllButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				for (JCheckBox checkBox : propertyList)
				{
					if (checkBox.isVisible())
					{
						checkBox.setSelected(false);
					}
				}
			}
		});
	}

	public JTextField getFilterInput()
	{
		return filterInput;
	}

	public MainForm getMainForm()
	{
		return mainForm;
	}

	public JPanel getCheckBoxPanel()
	{
		return checkBoxPanel;
	}

	private JPanel          contentPane;
	private JButton         buttonOK;
	private JButton         buttonCancel;
	private JCheckBox       enableSubCategorizedViewCheckBox;
	private JTabbedPane     settingsTabbedPane;
	private JPanel          checkBoxPanel;
	private JScrollPane     propertyScrollPane;
	private JPanel          propertyFilterPanel;
	private JTextField      filterInput;
	private JButton         selectAllButton;
	private JButton         deselectAllButton;
	private PoEItemAnalyzer analyzer;
	private MainForm        mainForm;
}
