

package main;


import java.awt.Color;


public class PropertyRater
{
	public PropertyRater(int maxValue, int value)
	{
		this.maxValue = maxValue;
		this.value = value;
		this.percentage = this.calculatePercentage();

		if (this.percentage < 0)
		{
			;this.percentage = 0;
		}

		if (this.percentage > 1)
		{
			this.percentage = 1;
		}
	}

	private double calculatePercentage()
	{
		double max = maxValue * 1.0;
		double value = this.value * 1.0;

		return value/max;
	}

	public String formattedPercentage()
	{
		int rv = (int) Math.floor(calculatePercentage() * 100);

		return "[" + rv + "%]";
	}

	public Color calculateColor()
	{
		int limit = 255;

		int r = (int) (limit - (limit * percentage));
		int g = (int) (limit * percentage);
		int b = 0;

		if (this.percentage > .98)
		{
			b = (int) (g * .90);
			g = 62;
			r = 146;
		}

		return new Color(r, g, b);
	}


	public double getPercentage()
	{
		return percentage;
	}


	private double percentage;
	private int maxValue;
	private int value;
}
