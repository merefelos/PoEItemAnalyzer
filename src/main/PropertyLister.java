package main;

import javax.swing.*;
import java.util.*;

/**
 * Created by Anna on 08/11/2015.
 */
public class PropertyLister
{
	public PropertyLister(PoEItemAnalyzer analyzer)
	{
		this.analyzer = analyzer;
		this.propertyMap = new HashMap<String, ItemProperties>();
		this.populatePropertyMap();
		this.buildCheckBoxes();
	}

	public void populatePropertyMap()
	{
		for (ItemProperties itemProperties : this.analyzer.getProperties())
		{
			propertyMap.put(this.buildCheckBoxLabel(itemProperties.getId(), itemProperties.getLevel(),
					itemProperties.getContext()), itemProperties);
		}
	}

	public String buildCheckBoxLabel(String id, int level, String context)
	{
		if (context.equals("null"))
		{
			return id + " (lvl " + level + ")";

		}
		else
		{
			return id + " (lvl " + level + " " + context + ")";
		}
	}

	public HashMap<String, ItemProperties> getPropertyMap()
	{
		return propertyMap;
	}

	public void buildCheckBoxes()
	{
		this.propertyListCheckBoxes = new ArrayList<>();
		this.checkBoxMap = new HashMap<>();
		for (String property : this.propertyMap.keySet())
		{
			JCheckBox checkBox = new JCheckBox(property, true);
			propertyListCheckBoxes.add(checkBox);
			this.checkBoxMap.put(property, checkBox);
		}
	}

	public HashMap<String, JCheckBox> getCheckBoxMap()
	{
		return checkBoxMap;
	}

	public List<JCheckBox> getPropertyListAsCheckboxes()
	{
		java.util.Collections.sort(this.propertyListCheckBoxes, new Comparator<JCheckBox>()
		{
			@Override
			public int compare(JCheckBox o1, JCheckBox o2)
			{
				String o1String = o1.getText();
				String o2String = o2.getText();
				return o1String.compareTo(o2String);
			}
		});
		return this.propertyListCheckBoxes;
	}

	private PoEItemAnalyzer                 analyzer;
	private HashMap<String, ItemProperties> propertyMap;
	private List<JCheckBox>                 propertyListCheckBoxes;
	private HashMap<String, JCheckBox>      checkBoxMap;
}
