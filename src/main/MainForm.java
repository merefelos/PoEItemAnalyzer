

package main;


import main.listeners.MenuListenerImport;
import main.listeners.MenuListenerSettings;

import java.util.HashMap;
import java.util.Map;

import javax.swing.*;


public class MainForm
{
	public MainForm(JFrame frame)
	{
		this.mainFrame = frame;
	}

	public void setAnalyzer(PoEItemAnalyzer analyzer)
	{
		this.analyzer = analyzer;
	}

	public void initArrays()
	{
		this.panels.put(1, this.attributeIndicator1);
		this.panels.put(2, this.attributeIndicator2);
		this.panels.put(3, this.attributeIndicator3);
		this.panels.put(4, this.attributeIndicator4);
		this.panels.put(5, this.attributeIndicator5);
		this.panels.put(6, this.attributeIndicator6);
		this.panels.put(7, this.attributeIndicator7);
		this.panels.put(8, this.attributeIndicator8);
		this.panels.put(9, this.attributeIndicator9);
		this.panels.put(10, this.attributeIndicator10);
		this.panels.put(11, this.attributeIndicator11);
		this.panels.put(12, this.attributeIndicator12);
		this.panels.put(13, this.attributeIndicator13);
		this.panels.put(14, this.attributeIndicator14);
		this.panels.put(15, this.attributeIndicator15);
		this.panels.put(16, this.attributeIndicator16);


		this.labels.put(1, this.label1);
		this.labels.put(2, this.label2);
		this.labels.put(3, this.label3);
		this.labels.put(4, this.label4);
		this.labels.put(5, this.label5);
		this.labels.put(6, this.label6);
		this.labels.put(7, this.label7);
		this.labels.put(8, this.label8);
		this.labels.put(9, this.label9);
		this.labels.put(10, this.label10);
		this.labels.put(11, this.label11);
		this.labels.put(12, this.label12);
		this.labels.put(13, this.label13);
		this.labels.put(14, this.label14);
		this.labels.put(15, this.label15);
		this.labels.put(16, this.label16);
	}

	public JMenuBar initMenu()
	{
		// Import main.listeners
		this.menuBar = new JMenuBar();
		this.menu1 = new JMenu("Configuration tools");
		this.menuBar.add(menu1);
		this.menu1.add(new JMenuItem("Import & Merge")).addActionListener(new MenuListenerImport(this
				.mainFrame, this.analyzer));
		// Configuration settings
		this.menu2 = new JMenu("Settings");
		this.menuBar.add(this.menu2);
		this.menu2.add(new JMenuItem("Filter Settings")).addActionListener(new MenuListenerSettings
				(this.analyzer, this));
		return this.menuBar;
	}

	public void initPropertySettings()
	{
		this.propertyLister = new PropertyLister(this.analyzer);
	}

	public void setRawText(String text)
	{
		this.rawText.setText(text);
		c = 1;
	}

	public PropertyLister getPropertyLister()
	{
		return propertyLister;
	}

	public void addInfo(PropertyRater propertyRater, String originalText, String id, int level)
	{
		if (this.propertyLister.getCheckBoxMap().get(this.propertyLister.buildCheckBoxLabel(id,
				level, "null")) != null && this.propertyLister.getCheckBoxMap().get(this.propertyLister
				.buildCheckBoxLabel(id,
						level, "null")).isSelected())
		{
			if (c < 17)
			{
				this.labels.get(c).setText(propertyRater.formattedPercentage() + " " + originalText);

				this.panels.get(c).setBackground(propertyRater.calculateColor());
			}

			c++;
		}
	}

	public void resetLabels()
	{
		for (Integer i : this.labels.keySet())
		{
			this.labels.get(i).setText("");
			this.panels.get(i).setBackground(null);
		}
	}

	private int c = 1;

	private JPanel attributeIndicator1;

	private JPanel attributeIndicator2;

	private JPanel attributeIndicator3;

	private JPanel attributeIndicator4;

	private JPanel attributeIndicator5;

	private JPanel attributeIndicator6;

	private JPanel attributeIndicator7;

	private JPanel attributeIndicator8;

	private JPanel attributeIndicator9;

	private JLabel label1;

	private JLabel label2;

	private JLabel label3;

	private JLabel label4;

	private JLabel label5;

	private JLabel label6;

	private JLabel label7;

	private JLabel label8;

	private JLabel label9;

	public JPanel main;

	public JFrame   mainFrame;
	public JMenuBar menuBar;
	public JMenu    menu1;
	public JMenu    menu2;

	private JTextArea rawText;

	private JPanel attributeIndicator10;

	private JPanel attributeIndicator11;

	private JPanel attributeIndicator12;

	private JPanel attributeIndicator13;

	private JPanel attributeIndicator14;

	private JPanel attributeIndicator15;

	private JPanel attributeIndicator16;

	private JLabel label10;

	private JLabel label11;

	private JLabel label12;

	private JLabel label13;

	private JLabel label14;

	private JLabel label15;

	private JLabel label16;

	public Map<Integer, JPanel> panels = new HashMap<Integer, JPanel>();
	public Map<Integer, JLabel> labels = new HashMap<Integer, JLabel>();

	private PoEItemAnalyzer analyzer;
	private PropertyLister  propertyLister;
}
