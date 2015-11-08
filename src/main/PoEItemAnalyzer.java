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
		this.fileManager = new FileManager();
		this.display = display;
		fileManager.readFromFile(new File("properties.cnf"), this.properties);
		this.populateMap();
		this.types = fileManager.readTypesFromFile();
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
			if (c == 50)
			{
				this.c = 0;
				fileManager.putIntoFile(new File("properties.cnf"), this.properties);
			}
		}
	}

	private boolean analyzeItem(String item)
	{
		int level = -1;
		this.isUnique = false;
		// Clear display before outputting the new stuff
		display.resetLabels();

		// Check uniqueness
		Pattern pattern4 = Pattern.compile("Rarity: Unique\n");
		Matcher matcher4 = pattern4.matcher(item);
		if (matcher4.find())
		{
			this.isUnique = true;
		}

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

			ItemProperties socketprops = map.get(this.buildMapKey("Sockets", level, "null", type));

			if (socketprops == null)
			{
				socketprops = new ItemProperties("Sockets", level, "null", sockets, type);
				if (!this.isUnique)
				{
					this.properties.add(socketprops);
					this.map.put(socketprops.buildMapKey(), socketprops);
				}
			}
			else
			{
				if (socketprops.getValue() < sockets && !this.isUnique)
				{
					socketprops.setValue(sockets);
				}
			}

			PropertyRater rating = this.analyzeRating("Sockets", level, "null", sockets, type);

			if (rating != null)
			{
				display.addInfo(rating, "Sockets", socketprops.getId(), level);
			}
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
				PropertyRater rating = this.analyzeLine(line, level, type);

				if (rating != null)
				{
					display.addInfo(rating, line, this.denumerize(line), level);
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

	private PropertyRater analyzeLine(String line, int level, String type)
	{
		PropertyRater returnRater = null;

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
					ItemProperties minProperties = map.get(this.buildMapKey(id, level, "min", type));
					ItemProperties maxProperties = map.get(this.buildMapKey(id, level, "max", type));

					if (minProperties == null)
					{
						minProperties = new ItemProperties(id, level, "min", range.getKey(), type);
						if (!this.isUnique)
						{
							this.properties.add(minProperties);
							this.map.put(minProperties.buildMapKey(), minProperties);
						}
					}
					else
					{
						int v = range.getKey();

						if (minProperties.getValue() < v)
						{
							minProperties.setValue(v);
						}
					}


					if (maxProperties == null)
					{
						maxProperties = new ItemProperties(id, level, "max", range.getValue(), type);

						if (!this.isUnique)
						{
							this.properties.add(maxProperties);
							this.map.put(maxProperties.buildMapKey(), maxProperties);
						}
					}
					else
					{
						int v = range.getValue();
						if (maxProperties.getValue() < v)
						{
							maxProperties.setValue(v);
						}
					}

					returnRater = this.analyzeRating(minProperties.getValue(),
							maxProperties.getValue(), id, level, type);
				}
				else if (dValue != -1)
				{
					ItemProperties properties = this.map.get(this.buildMapKey(id, level, "null", type));

					// scaling up the doubles
					dValue *= 100;

					int retardedValue = (int) dValue;
					int storedValue = 0;
					if (properties == null)
					{
						properties = new ItemProperties(id, level, "null", retardedValue,
								type);

						if (!this.isUnique)
						{
							this.properties.add(properties);
							this.map.put(properties.buildMapKey(), properties);
						}
					}
					else
					{
						if (properties.getValue() < retardedValue && !this.isUnique)
						{
							properties.setValue(retardedValue);
						}
					}

					returnRater = this.analyzeRating(id, storedValue, "null", retardedValue, type);
				}
				else if (value != -1)
				{
					ItemProperties properties = this.map.get(this.buildMapKey(id, level, "null", type));

					if (properties == null)
					{
						properties = new ItemProperties(id, level, "null", value, type);

						if (!this.isUnique)
						{
							this.properties.add(properties);
							this.map.put(properties.buildMapKey(), properties);
						}
					}
					else
					{
						if (properties.getValue() < value && !this.isUnique)
						{
							properties.setValue(value);
						}
					}

					returnRater = this.analyzeRating(id, level, "null", value, type);
				}
			}
		}

		return returnRater;
	}

	private PropertyRater analyzeRating(int newMin, int newMax, String id, int level, String type)
	{
		PropertyRater minRating = this.analyzeRating(id, level, "min", newMin, type);
		PropertyRater maxRating = this.analyzeRating(id, level, "max", newMax, type);

		PropertyRater rater;

		if (minRating.getPercentage() > maxRating.getPercentage())
		{
			rater = minRating;
		}
		else
		{
			rater = maxRating;
		}

		return rater;
	}

	private PropertyRater analyzeRating(String id, int level, String context, int newValue, String
			type)
	{
		int storedValue = 0;
		int maxValue = -1;
		for (int i = 0; i <= level; i++)
		{
			ItemProperties props = map.get(this.buildMapKey(id, i, context, type));

			if (props != null)
			{
				storedValue = Math.max(storedValue, props.getValue());
			}
		}

		return new PropertyRater(storedValue, newValue);
	}

	private String buildMapKey(String id, int level, String context, String type)
	{
		return id + ":" + level + ":" + type + ":" + context;
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

	private void populateMap()
	{
		for (ItemProperties property : this.properties)
		{
			this.map.put(property.buildMapKey(), property);
		}
	}

	public List<ItemProperties> getProperties()
	{
		return properties;
	}

	public static boolean       running = true;
	public static Queue<String> queue   = new LinkedList<String>();

	public List<ItemProperties> properties = new ArrayList<ItemProperties>(64);

	public Map<String, ItemProperties> getMap()
	{
		return map;
	}

	Map<String, ItemProperties> map = new HashMap<String, ItemProperties>();
	public FileManager fileManager;

	private int     c        = 0;
	private boolean isUnique = false;

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
