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
		try
		{
			File dir = new File("types");
			File[] typeFiles = dir.listFiles();
			for (int i = 0; i < typeFiles.length; i++)
			{
				if (typeFiles[i].isFile())
				{
					StringTokenizer tokenizer = new StringTokenizer(typeFiles[i].getName(), ".");
					String typeName = tokenizer.nextToken();
					Scanner fileScanner = new Scanner(typeFiles[i]);
					List<String> subtypes = new ArrayList<String>();
					while (fileScanner.hasNextLine())
					{
						String line = fileScanner.nextLine();
						line = line.trim();
						if (line.length() > 0)
						{
							subtypes.add(line);
						}
					}
					types.put(typeName, subtypes);
					fileScanner.close();
				}
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		return types;
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

}
