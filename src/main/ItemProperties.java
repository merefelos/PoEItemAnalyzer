package main;

/**
 * Created by Anna on 01/11/2015.
 */
public class ItemProperties
{
	public String getId()
	{
		return id;
	}

	public int getLevel()
	{
		return level;
	}

	public String getContext()
	{
		return context;
	}

	public int getValue()
	{
		return value;
	}

	public String getType()
	{
		return type;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public void setContext(String context)
	{
		this.context = context;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public ItemProperties(String id, int level, String context, int value, String type)
	{
		this.id = id;
		this.level = level;
		this.context = context;
		this.value = value;
		this.type = type;
	}

	public String buildMapKey()
	{
		return id + ":" + level + ":" + type + ":" + context;
	}

	@Override
	public String toString()
	{
		return id + ":" + level + ":" + type + ":" + context + ":" + value;
	}

	private String id;
	private int    level;
	private String context;
	private int    value;
	private String type;
}
