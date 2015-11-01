

package main;


import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;


public class MainForm
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("A Thing");
		MainForm mainForm = new MainForm();

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.setContentPane(mainForm.main);

		frame.setVisible(true);
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


		this.labels.put(1, this.label1);
		this.labels.put(2, this.label2);
		this.labels.put(3, this.label3);
		this.labels.put(4, this.label4);
		this.labels.put(5, this.label5);
		this.labels.put(6, this.label6);
		this.labels.put(7, this.label7);
		this.labels.put(8, this.label8);
		this.labels.put(9, this.label9);
	}

	public void setRawText(String text)
	{
		this.rawText.setText(text);
		c = 1;
	}


	public void addInfo(String label, int rating)
	{
		if (c < 10)
		{
			this.labels.get(c).setText(label);

			Color color = Color.PINK;

			switch (rating)
			{
				case -1:
					color = Color.gray;
					break;
				case 1:
					color = Color.GREEN;
					break;
				case 2:
					color = new Color(100, 200, 20);
					break;
				case 3:
					color = Color.yellow;
					break;
				case 4:
					color = Color.orange;
					break;
				case 5:
					color = Color.red;
					break;
			}

			this.panels.get(c).setBackground(color);
		}

		c++;
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

	private JTextArea rawText;
	public Map<Integer, JPanel> panels = new HashMap<Integer, JPanel>();
	public Map<Integer, JLabel> labels = new HashMap<Integer, JLabel>();
}
