package main;

import java.io.*;
import java.util.*;

/**
 * Created by Anna on 04/11/2015.
 * todo: http://alvinalexander.com/blog/post/java/read-text-file-from-jar-file
 */
public class FileManager
{
	public void readFromFile(File file, List<ItemProperties> itemProperties)
	{
		try
		{
			if (!file.canRead())
			{
				// When no file given, assume initialization and create a config file
				FileWriter fileWriter = new FileWriter(new File("properties.cnf"));
				fileWriter.close();
				file = new File("properties.cnf");
			}

			Scanner fileScanner = new Scanner(file);
			while (fileScanner.hasNextLine())
			{
				itemProperties.add(parseProperties(fileScanner.nextLine()));
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void putIntoFile(File file, List<ItemProperties> itemProperties)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(("properties.cnf")));

			for (ItemProperties property : itemProperties)
			{
				writer.write(property.toString());
				writer.newLine();
			}

			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public HashMap<String, List<String>> readTypesFromFile()
	{
		HashMap<String, List<String>> types = new HashMap<String, List<String>>(64);

		File dir = new File("types");
		File[] typeFiles = dir.listFiles();
		for (int i = 0; i < typeFiles.length; i++)
		{
			if (typeFiles[i].isFile())
			{
				StringTokenizer tokenizer =
					new StringTokenizer(typeFiles[i].getName(), ".");
				String typeName = tokenizer.nextToken();

				InputStream inputStream =
					this.getClass().getClassLoader().getResourceAsStream(typeFiles[i].getName());
				Scanner fileScanner = new Scanner(inputStream);
				List<String> subtypes = new ArrayList<String>();

				while (fileScanner.hasNextLine())
				{
					String line = fileScanner.nextLine();
					line = line.trim();
					if (line.length() > 0)
					{
						subtypes.add(line);
						this.getGroupMap().put(line, typeName);
					}
				}

				this.groups.put(typeName, subtypes);
				fileScanner.close();
			}
		}

		return types;
	}

	public void readCSVTypes()
	{
		File dir = new File("csvTypes");
		File[] typeFiles = dir.listFiles();

		for (int i = 0; i < typeFiles.length; i++)
		{
			if (typeFiles[i].isFile())
			{
				StringTokenizer tokenizer =
					new StringTokenizer(typeFiles[i].getName(), ".");
				String typeName = tokenizer.nextToken();

				InputStream inputStream =
					this.getClass().getClassLoader().getResourceAsStream(typeFiles[i].getName());
				Scanner fileScanner = new Scanner(inputStream);
				List<String> itemNames = new ArrayList<String>();

				while (fileScanner.hasNextLine())
				{
					String line = fileScanner.nextLine();
					line = line.trim();

					StringTokenizer strokenizer = new StringTokenizer(line, ",");

					String itemName = "";

					if (strokenizer.hasMoreTokens())
					{
						itemName = strokenizer.nextToken();
						this.groupMap.put(itemName, typeName);
						itemNames.add(itemName);
					}

					if (strokenizer.hasMoreTokens())
					{
						// implicit property
						String implicitProperty = strokenizer.nextToken();
						this.implicitAttributeMap.put(itemName, implicitProperty);
					}

					if (strokenizer.hasMoreTokens())
					{
						// subtype
						String subGroup = strokenizer.nextToken();
						this.groupMap.put(itemName, typeName + subGroup);
					}
				}

				this.groups.put(typeName, itemNames);
				fileScanner.close();
			}
		}
	}

	private ItemProperties parseProperties(String rawData)
	{
		StringTokenizer tokenizer = new StringTokenizer(rawData, ":");
		String id = tokenizer.nextToken();
		int level = Integer.parseInt(tokenizer.nextToken());
		String type = tokenizer.nextToken();
		String context = tokenizer.nextToken();
		int value = Integer.parseInt(tokenizer.nextToken());

		return new ItemProperties(id, level, context, value, type);
	}


	public Map<String, String> getGroupMap()
	{
		return groupMap;
	}


	public Map<String, String> getSubGroupMap()
	{
		return subGroupMap;
	}


	public Map<String, String> getImplicitAttributeMap()
	{
		return implicitAttributeMap;
	}


	public Map<String, List<String>> getGroups()
	{
		return groups;
	}


	private Map<String, String> groupMap = new HashMap<>();
	private Map<String, String> subGroupMap = new HashMap<>();;
	private Map<String, String> implicitAttributeMap = new HashMap<>();;
	private Map<String, List<String>> groups = new HashMap<>();

}
