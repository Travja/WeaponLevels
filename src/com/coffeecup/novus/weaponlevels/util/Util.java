package com.coffeecup.novus.weaponlevels.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;

import com.coffeecup.novus.weaponlevels.Config;
import com.coffeecup.novus.weaponlevels.Permissions;
import com.coffeecup.novus.weaponlevels.stages.Stage;
import com.coffeecup.novus.weaponlevels.stages.StageManager;
import com.coffeecup.novus.weaponlevels.type.ItemType;

public class Util
{
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

	public static void dropExperience(Location loc, int expToDrop, int expPerOrb)
	{
		World world = loc.getWorld();

		int maxOrbs = expToDrop / expPerOrb;

		for (int orb = 0; orb < maxOrbs; orb++)
		{
			((ExperienceOrb) world.spawn(loc, ExperienceOrb.class)).setExperience(expPerOrb);
		}
	}

	public static String searchListForString(List<String> list, String string, String def)
	{
		if (list == null) return def;
		
		for (String s : list)
		{
			if (ChatColor.stripColor(s).startsWith(ChatColor.stripColor(string)))
			{
				return s;
			}
		}

		return def;
	}
	
	public static String searchListForString(List<String> list, String string, String def, String prefix)
	{
		if (list == null) return def;
		
		for (String line : list)
		{
			String strippedLine = ChatColor.stripColor(line);
			
			if (strippedLine.startsWith(prefix))
			{
				return line.substring(prefix.length());
			}
			
			if (strippedLine.startsWith(ChatColor.stripColor(string)))
			{
				return line;
			}
		}

		return def;
	}
	
	public static int searchListForStringID(List<String> list, String string, int def)
	{
		if (list == null) return def;
		
		ListIterator<String> i = list.listIterator();
		
		while (i.hasNext())
		{
			String s = i.next();
			if (ChatColor.stripColor(s).startsWith(ChatColor.stripColor(string)))
			{
				return i.nextIndex() - 1;
			}
		}
		
		return def;
	}
	
	public static int searchListForStringID(List<String> list, String string, int def, String prefix)
	{
		if (list == null) return def;
		
		ListIterator<String> i = list.listIterator();
		
		while (i.hasNext())
		{
			String s = i.next();
			if (ChatColor.stripColor(s).startsWith(prefix + ChatColor.stripColor(string)))
			{
				return i.nextIndex() - 1;
			}
		}
		
		return def;
	}
	
	public static ChatColor getSafeChatColor(String color, ChatColor def)
	{
		for (ChatColor c : ChatColor.values())
		{
			if (color.equalsIgnoreCase(c.name()))
			{
				return c;
			}
		}
		
		return def;
	}
	
	public static int getLevelOnCurve(int min, int max, double ratio)
	{
		Random rand = new Random();
		int level = 1;
		
		int roll = rand.nextInt(100) + 1;
		
		for (int i = min; i <= max; i++)
		{			
			if (roll <= (ratio / i) * 100)
			{
				//System.out.println("i: " + i + " needed: " + (ratio / i * 100) + " roll: " + roll);
				level = i;
			}
			else
			{
				return level;
			}
		}
		
		return level;
	}
	
	public static int getMaxLevel(Player player, ItemType type)
	{
		if (!Config.USE_PERMS)
		{
			return Config.MAX_LEVEL;
		}
		
		for (int level = Config.MAX_LEVEL; level > 0; level--)
		{
			Stage stage = StageManager.getStage(type, level);
			
			if (stage == null) continue;
			
			if (Permissions.hasPermission(player, stage.getName()))
			{
				return level;
			}
		}
		
		return 0;
	}
	
	public static void printlnObj(PrintStream printer, Object...objects)
	{
		String line = "";
		
		for (Object obj : objects)
		{
			line += obj.toString() + ":";
		}
		
		printer.println(line);
	}
}
