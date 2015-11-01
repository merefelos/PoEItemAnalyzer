package main;

import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Anna on 01/11/2015.
 */
public class PoEItemAnalyzer implements Runnable
{
	public PoEItemAnalyzer(MainForm display)
	{
		this.display = display;
		this.readFromFile();
		this.readTypesFromFile();
		this.patterns.add(".*[0-9]+.*");
	}

	@Override
	public void run()
	{
		while (PoEItemAnalyzer.running)
		{
			String s = queue.poll();
			if (s != null)
			{
				display.setRawText(s);
				this.analyzeItem(s);
			}

			try
			{
				Thread.sleep(113);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			c++;
			if (c == 10)
			{
				this.c = 0;
				this.putIntoFile();
			}
		}
	}

	private boolean analyzeItem(String item)
	{
		int level = -1;
		// Clear display before outputting the new stuff
		display.resetLabels();

		// Grab level
		Pattern pattern1 = Pattern.compile("\nLevel: [0-9]+");
		Matcher matcher = pattern1.matcher(item);

		if (matcher.find())
		{
			String s = matcher.group(0);
			StringTokenizer strokenizer = new StringTokenizer(s, " ");

			String s1 = strokenizer.nextToken();
			s1 = strokenizer.nextToken();
			level = Integer.parseInt(s1);
		}

		// Decide on item type
		String type = this.digForType(item);

		// Grab sockets
		Pattern pattern2 = Pattern.compile("\nSockets: .*\n");
		Matcher matcher2 = pattern2.matcher(item);
		int sockets = 0;

		if (matcher2.find())
		{
			String s = matcher2.group(0);
			StringTokenizer strokenizer = new StringTokenizer(s, ":");

			String s1 = strokenizer.nextToken();
			s1 = strokenizer.nextToken();

			s1 = s1.replaceAll("[^A-Z]", "");
			sockets = s1.length();

			ItemProperties socketprops = map.get(this.buildMapKey("Sockets", level, "null"));
			if (socketprops == null)
			{
				socketprops = new ItemProperties("Sockets", level, "null", sockets, type);
				this.properties.add(socketprops);
				this.map.put(socketprops.buildMapKey(), socketprops);
			}
			else
			{
				if (socketprops.value < sockets)
				{
					socketprops.value = sockets;
				}
			}

			display.addInfo("Sockets", sockets);
		}

		// If there are no requirements, level is 0
		Pattern pattern3 = Pattern.compile("\nRequirements:.*\n");
		Matcher matcher3 = pattern3.matcher(item);
		if (!matcher3.find())
		{
			level = 0;
		}

		Scanner scanner = new Scanner(item);

		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine();
			boolean goAway = false;
			for (Skippable skippable : Skippable.values())
			{
				if (line.startsWith(skippable.name()))
				{
					goAway = true;
				}
			}
			if (!goAway)
			{
				int rating = this.analyzeLine(line, level, type);
				if (rating > 0)
				{
					display.addInfo(line, rating);
				}
			}
		}

		return true;
	}

	private String digForType(String item)
	{
		for (String type : types.keySet())
		{
			List<String> subtypeList = types.get(type);
			for (String subtype : subtypeList)
			{
				if (item.contains(subtype))
				{
					return type;
				}
			}
		}
		return "other";
	}

	private int analyzeLine(String line, int level, String type)
	{
		int returnvalue = -1;

		if (!garbage.contains(line))
		{
			String id = this.denumerize(line);

			if (!id.equals(""))
			{
				id = id.replaceAll("augmented", "");

				int value = this.digForValue(line);
				double dValue = -1;
				if (value == -1)
				{
					dValue = this.digForDecimal(line);
				}

				Pair<Integer, Integer> range = null;

				if (dValue == -1 && value == -1)
				{
					range = this.digForRange(line);
				}

				if (range != null)
				{
					ItemProperties minProperties = map.get(this.buildMapKey(id, level, "min"));
					ItemProperties maxProperties = map.get(this.buildMapKey(id, level, "max"));
					int storedMin = 0;
					int storedMax = 0;

					if (minProperties == null)
					{
						minProperties = new ItemProperties(id, level, "min", range.getKey(), type);
						this.properties.add(minProperties);
						this.map.put(minProperties.buildMapKey(), minProperties);
					}
					else
					{
						int v = range.getKey();
						storedMin = minProperties.value;
						if (minProperties.value < v)
						{
							minProperties.value = v;
						}
					}


					if (maxProperties == null)
					{
						maxProperties = new ItemProperties(id, level, "max", range.getValue(), type);
						this.properties.add(maxProperties);
						this.map.put(maxProperties.buildMapKey(), maxProperties);

					}
					else
					{
						int v = range.getValue();
						storedMax = maxProperties.value;
						if (maxProperties.value < v)
						{
							maxProperties.value = v;
						}
					}
					returnvalue = this.analyzeRating(minProperties.value,
							maxProperties.value, id, level);
				}
				else if (dValue != -1)
				{
					ItemProperties properties = this.map.get(this.buildMapKey(id, level, "null"));

					// scaling up the doubles
					dValue *= 100;

					int retardedValue = (int) dValue;
					int storedValue = 0;
					if (properties == null)
					{
						ItemProperties itemProperties = new ItemProperties(id, level, "null", retardedValue,
								type);
						this.properties.add(itemProperties);
						this.map.put(itemProperties.buildMapKey(), itemProperties);
						returnvalue = 1;
					}
					else
					{
						storedValue = properties.value;
						if (properties.value < retardedValue)
						{
							properties.value = retardedValue;
						}
						returnvalue = this.analyzeRating(id, storedValue, "null", retardedValue);
					}

				}
				else if (value != -1)
				{
					ItemProperties properties = this.map.get(this.buildMapKey(id, level, "null"));
					int storedValue = 0;

					if (properties == null)
					{
						ItemProperties itemProperties = new ItemProperties(id, level, "null", value, type);
						this.properties.add(itemProperties);
						this.map.put(itemProperties.buildMapKey(), itemProperties);
						returnvalue = 1;
					}
					else
					{
						storedValue = properties.value;
						if (properties.value < value)
						{
							properties.value = value;
						}
						returnvalue = this.analyzeRating(id, level, "null", value);
					}

				}

//				String v = "" + value + " " + dValue + " " + range;

//				System.out.println(id + " -> " + v);
			}
		}

		return returnvalue;
	}

	private int analyzeRating(int newMin, int newMax, String id, int level)
	{
		int minRating = this.analyzeRating(id, level, "min", newMin);
		int maxRating = this.analyzeRating(id, level, "max", newMax);

		return Math.max(minRating, maxRating);
	}

	private int analyzeRating(String id, int level, String context, int newValue)
	{
		int storedValue = -1;
		int maxValue = -1;
		for (int i = 0; i <= level; i++)
		{
			ItemProperties props = map.get(this.buildMapKey(id, i, context));
			if (props != null)
			{
				storedValue = Math.max(storedValue, props.value);
			}
		}

		int allWeKnow = storedValue - newValue;
		if (allWeKnow <= 0)
		{
			return 1;
		}
		else
		{
			int deliminator = 0;
			int divisor = 2;
			int rating = 6;
			for (int i = 0; i < 4; i++)
			{
				deliminator = deliminator + storedValue / divisor;
				rating--;
				divisor *= 2;
				if (newValue <= deliminator)
				{
					return rating;
				}
			}
		}
		return 1;
	}

	private String buildMapKey(String id, int level, String context)
	{
		return id + ":" + level + ":" + context;
	}

	private Pair<Integer, Integer> digForRange(String line)
	{
		int rv = -1;
		Pattern pattern = Pattern.compile("[0-9]+-[0-9]+");
		Matcher matcher = pattern.matcher(line);

		int firstNumber = 0;
		int secondNumber = 0;

		while (matcher.find())
		{
			String range = matcher.group();
			StringTokenizer stringTokenizer = new StringTokenizer(range, "-");
			firstNumber += Integer.parseInt(stringTokenizer.nextToken());
			secondNumber += Integer.parseInt(stringTokenizer.nextToken());
		}

		Pair<Integer, Integer> pair = null;

		if (firstNumber != 0 && secondNumber != 0)
		{
			int x = firstNumber;
			int x1 = secondNumber;

			pair = new Pair<Integer, Integer>(x, x1);
		}

		return pair;
	}

	private double digForDecimal(String line)
	{
		double rv = -1;
		Pattern pattern1 = Pattern.compile("[0-9]+\\.[0-9]+");
		Matcher matcher1 = pattern1.matcher(line);

		if (matcher1.find())
		{
			String firstnumber = matcher1.group(0);
			rv = Double.parseDouble(firstnumber);
		}

		return rv;
	}

	private int digForValue(String line)
	{
		int rv = -1;
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(line);

		if (matcher.find())
		{
			String firstnumber = matcher.group(0);
			rv = Integer.parseInt(firstnumber);
		}

		if (matcher.find())
		{
			rv = -1;
		}

		return rv;
	}

	private String denumerize(String line)
	{
		String a = line;

		a = a.replaceAll("[^a-zA-Z%\\+]", "");

		return a;
	}

	private void readFromFile()
	{

		try
		{
			File file = new File("properties.cnf");
			if (!file.canRead())
			{
				FileWriter fileWriter = new FileWriter(new File("properties.cnf"));
				fileWriter.close();
				file = new File("properties.cnf");
			}
			Scanner fileScanner = new Scanner(file);
			while (fileScanner.hasNextLine())
			{
				properties.add(parseProperties(fileScanner.nextLine()));
			}

			for (ItemProperties property : properties)
			{
				map.put(property.buildMapKey(), property);
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

	private void readTypesFromFile()
	{
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
					this.types.put(typeName, subtypes);
					fileScanner.close();
				}
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

	private void putIntoFile()
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(("properties.cnf")));

			for (ItemProperties property : properties)
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

	public static boolean              running    = true;
	public static Queue<String>        queue      = new LinkedList<String>();
	public        List<ItemProperties> properties = new ArrayList<ItemProperties>(64);
	Map<String, ItemProperties> map = new HashMap<String, ItemProperties>();
	private int c = 0;

	// +[0-9]*%jabadajabada
	List<String>                  patterns = new ArrayList<String>(32);
	Set<String>                   garbage  = new HashSet<String>();
	HashMap<String, List<String>> types    = new HashMap<String, List<String>>(64);

	private enum Skippable
	{
		Str,
		Dex,
		Int,
		Item,
		Level,
	}

	private final MainForm display;
}
