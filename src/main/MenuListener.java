package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anna on 04/11/2015.
 */
public class MenuListener implements ActionListener, ItemListener
{
	public MenuListener(JFrame mainFrame, PoEItemAnalyzer analyzer)
	{
		this.mainFrame = mainFrame;
		this.analyzer = analyzer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnValue = fileChooser.showDialog(this.mainFrame.getContentPane(), "Choose directory");
		if (returnValue == JFileChooser.APPROVE_OPTION)
		{
			File chosenFile = fileChooser.getSelectedFile();
			List<ItemProperties> itemPropertiesList = new ArrayList<>();
			FileManager fileManager = new FileManager();
			fileManager.readFromFile(chosenFile, itemPropertiesList);

			for (ItemProperties itemProperties : itemPropertiesList)
			{
				ItemProperties property = this.analyzer.map.get(itemProperties.buildMapKey());
				if (property == null)
				{
					this.analyzer.properties.add(itemProperties);
					this.analyzer.map.put(itemProperties.buildMapKey(), itemProperties);
				}
				else
				{
					if (itemProperties.value > property.value)
					{
						property.value = itemProperties.value;
					}
				}

			}
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e)
	{
		int breakpoint = 0;
	}

	JFrame          mainFrame;
	PoEItemAnalyzer analyzer;
}
