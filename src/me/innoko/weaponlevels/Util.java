package me.innoko.weaponlevels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;

public class Util
{
	/**
	 * Creates an EXP bar using the chosen parameters.
	 * 
	 * @param max
	 *            - The maximum value of the bar.
	 * @param amount
	 *            - The filled amount of the bar.
	 * @param ratio
	 *            - The value of each line in the bar.
	 * @return A string representation of the EXP bar.
	 */
	public static String createExpBar(int max, int amount, int ratio)
	{
		String bar = ChatColor.GRAY + "[";

		for (int i = 1; i <= max / ratio; i++)
		{
			if ((amount / ratio) >= i)
				bar += ChatColor.WHITE + "|";
			else
				bar += ChatColor.GRAY + "|";
		}
		bar += ChatColor.GRAY + "]";

		return bar;
	}

	/**
	 * Reads a text-based experience bar.
	 * 
	 * @param expBar
	 *            - The experience bar to read, in the format: [|||||||||||||||]
	 * @param multiplier
	 *            - The exp value of each line in the bar.
	 * @param on
	 *            - The color of the lines that are filled.
	 * @param off
	 *            - The color of the lines that are empty.
	 * @return The experience value of the bar.
	 */
	public static int readExperience(String expBar, int multiplier, ChatColor on, ChatColor off)
	{
		int exp = 0;

		expBar = expBar.substring(3, expBar.length() - 3); // Remove brackets

		String[] lines = expBar.split("\\|");

		for (int i = 0; i < lines.length; i++)
		{
			String line = lines[i];

			if (line.equals(on.toString()))
			{
				exp += multiplier;
			}
			else if (line.equals(off.toString()))
			{
				break;
			}
		}

		return exp;
	}

	/**
	 * Capitalizes the first letter of each word in the string.
	 * 
	 * @param string
	 *            - The string to capitalize.
	 * @param divider
	 *            - The character that divides the words.
	 * @return The capitalized string.
	 */
	public static String capitalizeFirst(String string, char divider)
	{
		String div = String.valueOf(divider);

		String[] words = string.split(div);

		string = "";

		for (int i = 0; i < words.length - 1; i++)
		{
			string += words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase() + " ";
		}

		string += words[words.length - 1].substring(0, 1).toUpperCase()
				+ words[words.length - 1].substring(1).toLowerCase();

		return string;
	}

	/**
	 * Get a list of values in a string that are seperated by commas. If there
	 * are spaces after the commas, they will automatically be removed. If the
	 * string starts with "none", it will return an empty list.
	 * 
	 * @param string
	 *            - The string that contains the CSV.
	 * @return A list containing each of the values.
	 */
	public static List<String> getCommaSeperatedValues(String string)
	{
		List<String> list = new ArrayList<String>();

		if (string.startsWith("none"))
			return list;

		String[] values = string.split(",");

		for (int i = 0; i < values.length; i++)
		{
			String value = values[i];

			if (value.startsWith(" "))
			{
				value = value.substring(1);
			}

			list.add(value);
		}

		return list;
	}

	/**
	 * Drops experience orbs at the given location containing the given amount
	 * of experience. This method will evenly distribute experience among the
	 * orbs.
	 * 
	 * @param loc
	 *            - The location to drop experience at.
	 * @param expToDrop
	 *            - The amount of experience to drop.
	 * @param expPerOrb
	 *            - The amount of experience that each dropped orb will contain.
	 *            Any remainder will not be dropped.
	 */
	public static void dropExperience(Location loc, int expToDrop, int expPerOrb)
	{
		World world = loc.getWorld();

		int maxOrbs = expToDrop / expPerOrb;

		for (int orb = 0; orb < maxOrbs; orb++)
		{
			((ExperienceOrb) world.spawn(loc, ExperienceOrb.class)).setExperience(expPerOrb);
		}
	}

	/**
	 * Searches the list for the a string that starts with the specified string.
	 * 
	 * @param list
	 *            - The list to search in.
	 * @param string
	 *            - The string to search for.
	 * @param def
	 *            - The default string to return if no string was found.
	 * @return The full string that was found, or the default string if no
	 *         string was found.
	 */
	public static String searchListForString(List<String> list, String string, String def)
	{
		for (String s : list)
		{
			if (ChatColor.stripColor(s).startsWith(ChatColor.stripColor(string)))
			{
				return s;
			}
		}

		return def;
	}
}
