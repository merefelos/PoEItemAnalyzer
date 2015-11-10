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
//		this.types = fileManager.readTypesFromFile();
		fileManager.readCSVTypes();
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
				display.resetLabels();

				s = this.preAnalysis(s);
				this.analyzeItem(s);

				if (!this.implicitAttribute.equals("Implicit found"))
				{
					if (!this.implicitAttribute.equals("No implicit Attribute"))
					{
						this.display.implicitMissingWarning();
					}
				}
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
				fileManager.recordPrettyPropertyNames(this.display.getPropertyLister()
						.getPropertyPrettyList());
			}
		}
	}

	private String preAnalysis(String item)
	{
		this.requiredLevel = 0;
		this.isUnique = false;

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
			this.requiredLevel = Integer.parseInt(s1);
		}

		// If there are no requirements, level is 0
		Pattern pattern3 = Pattern.compile("\nRequirements:.*\n");
		Matcher matcher3 = pattern3.matcher(item);
		if (!matcher3.find())
		{
			requiredLevel = 0;
		}

		// Decide on item type and other info.
		this.name = this.digForName(item);

		this.group = this.fileManager.getGroupMap().get(this.name);
		this.subGroup = this.fileManager.getSubGroupMap().get(this.name);
		this.implicitAttribute = this.fileManager.getImplicitAttributeMap().get(this.name);

		if (this.group == null)
		{
			this.group = "other";
		}

		if (this.implicitAttribute == null)
		{
			this.implicitAttribute = "No implicit Attribute";
		}
		else
		{
			this.implicitAttribute = this.denumerize(this.implicitAttribute);
		}

		return item.replaceAll("(^[^-]*-{8})", "");
	}


	private PropertyRater crunchSingleValueData(String property,
	                                            String category,
	                                            String context,
	                                            int value)
	{
		PropertyRater rater = null;

		if (category != null)
		{
			if (this.implicitAttribute.contains(property))
			{
				context = "implicit";
				this.implicitAttribute = "Implicit found";
			}

			ItemProperties properties = this.map
					.get(this.buildMapKey(property, this.requiredLevel, context, category));

			if (properties == null)
			{
				properties = new ItemProperties(property,
						this.requiredLevel,
						context,
						value,
						category);

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

			rater = this.analyzeRating(property, this.requiredLevel, context, value, this.group);

			if (context.equals("implicit"))
			{
				rater.setImplicit(true);
			}
		}

		return rater;
	}


	private PropertyRater crunchRangeData(String property,
	                                      String category,
	                                      int minValue,
	                                      int maxValue)
	{
		PropertyRater rater = null;

		if (category != null)
		{
			String context = "";

			if (this.implicitAttribute.contains(property))
			{
				context = " implicit";
				this.implicitAttribute = "Implicit found";
			}

			PropertyRater minRater =
					this.crunchSingleValueData(property, category, "min" + context, minValue);
			PropertyRater maxRater =
					this.crunchSingleValueData(property, category, "max" + context, maxValue);

			if (minRater.getPercentage() > maxRater.getPercentage())
			{
				rater = minRater;
			}
			else
			{
				rater = maxRater;
			}

			if (context.equals(" implicit"))
			{
				rater.setImplicit(true);
			}
		}

		return rater;
	}


	private boolean analyzeItem(String item)
	{
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


			PropertyRater groupRating = this.crunchSingleValueData("Sockets",
					this.group,
					"null",
					sockets);
			PropertyRater subGroupRating = this.crunchSingleValueData("Sockets",
					this.subGroup,
					"null",
					sockets);

			if (groupRating != null)
			{
				display.addInfo(groupRating, "Sockets", "Sockets", this.requiredLevel);
			}
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
			if (!goAway && !line.contains(this.name) && !line.contains("---"))
			{
				PropertyRater groupRating = this.analyzeLine(line, this.group);
				PropertyRater subGroupRating = this.analyzeLine(line, this.subGroup);

				if (group != null && groupRating != null)
				{
					display.addInfo(groupRating, line, this.denumerize(line), this.requiredLevel);
				}
			}
		}

		return true;
	}


	private String digForName(String item)
	{
		for (String name : this.fileManager.getGroupMap().keySet())
		{
			if (item.contains(name))
			{
				return name;
			}
		}

		return "other";
	}

	private PropertyRater analyzeLine(String line, String category)
	{
		PropertyRater returnRater = null;

		if (!garbage.contains(line) && category != null)
		{
			String id = this.denumerize(line);

			if (!id.equals(""))
			{
				id = id.replaceAll("augmented", "");

				// So it's good enough to store - let's pack it into the pretty list
				this.display.getPropertyLister().addPrettyProperty(id, line.replaceAll("augmented", ""));

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
					returnRater = this.crunchRangeData(id, category, range.getKey(), range.getValue());
				}
				else if (dValue != -1)
				{
					// scaling up the doubles
					dValue *= 100;

					int retardedValue = (int) dValue;

					returnRater = this.crunchSingleValueData(id, category, "null", retardedValue);
				}
				else if (value != -1)
				{
					returnRater = this.crunchSingleValueData(id, category, "null", value);
				}
			}
		}

		return returnRater;
	}

	private PropertyRater analyzeRating(String id, int level, String context, int newValue, String
			type)
	{
		int storedValue = 0;
		
		for (int i = -1; i <= level; i++)
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

	private int    requiredLevel     = -1;
	private String name              = null;
	private String group             = null;
	private String subGroup          = null;
	private String implicitAttribute = "No implicit Attribute";

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
		Rarity,
		Requirements,
		Sockets,
	}

	private final MainForm display;
}
