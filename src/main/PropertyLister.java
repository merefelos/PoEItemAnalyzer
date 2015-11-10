package main;

import javafx.util.Pair;

import javax.swing.*;
import java.io.File;
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
		this.propertyPrettyList = new HashMap<>();
		this.propertyListCheckBoxes = new ArrayList<>();
		this.analyzer.fileManager.readPrettyPropertyNames(new File("pretty.cnf"), this);
		this.populatePropertyMap();
		this.buildCheckBoxes();
	}

	public void populatePropertyMap()
	{
		for (ItemProperties itemProperties : this.analyzer.getProperties())
		{
			propertyMap.put(this.buildCheckBoxName(itemProperties.getId(), itemProperties.getLevel(),
					itemProperties.getContext()), itemProperties);
		}
	}

	public String buildCheckBoxName(String id, int level, String context)
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
		this.checkBoxMap = new HashMap<>();
		for (String property : this.propertyMap.keySet())
		{
			String label = property;
			if (this.propertyPrettyList.get(this.propertyMap.get(property).getId()) != null)
			{
				label = this.buildCheckBoxName(
						this.propertyPrettyList.get(this.propertyMap.get(property).getId()),
						this.propertyMap.get(property).getLevel(),
						this.propertyMap.get(property).getContext());
			}
			JCheckBox checkBox = new JCheckBox(label, true);
			checkBox.setName(property);
			propertyListCheckBoxes.add(checkBox);
			this.checkBoxMap.put(property, checkBox);
		}
	}

	public void addPrettyProperty(String name, String prettyName)
	{
		if (this.propertyPrettyList.get(name) == null)
		{
			prettyName = prettyName.replaceAll("[^a-zA-Z%\\+ ]", "");
			if (!prettyName.equals(""))
			{
				this.propertyPrettyList.put(name, prettyName);

				for (JCheckBox checkBox : this.getPropertyListAsCheckboxes())
				{
					if (checkBox.getName().contains(name))
					{
						checkBox.setText(prettyName);
					}
				}
			}
		}
	}

	public Pair<String, String> parsePrettyProperty(String raw)
	{
		StringTokenizer tokenizer = new StringTokenizer(raw, ":");
		return new Pair<>(tokenizer.nextToken(), tokenizer.nextToken());
	}

	public HashMap<String, String> getPropertyPrettyList()
	{
		return propertyPrettyList;
	}

	public HashMap<String, JCheckBox> getCheckBoxMap()
	{
		return checkBoxMap;
	}

	public List<JCheckBox> getPropertyListAsCheckboxes()
	{
		if (!this.propertyListCheckBoxes.isEmpty())
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
		}
		return this.propertyListCheckBoxes;
	}

	private PoEItemAnalyzer                 analyzer;
	private HashMap<String, ItemProperties> propertyMap;
	private List<JCheckBox>                 propertyListCheckBoxes;
	private HashMap<String, JCheckBox>      checkBoxMap;
	private HashMap<String, String>         propertyPrettyList;
}
