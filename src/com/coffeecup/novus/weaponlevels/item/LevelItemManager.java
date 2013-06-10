package com.coffeecup.novus.weaponlevels.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coffeecup.novus.weaponlevels.Config;
import com.coffeecup.novus.weaponlevels.stages.Stage;
import com.coffeecup.novus.weaponlevels.stages.StageManager;
import com.coffeecup.novus.weaponlevels.type.ItemType;
import com.coffeecup.novus.weaponlevels.type.TypeChecker;
import com.coffeecup.novus.weaponlevels.util.Util;

public class LevelItemManager
{
	public static boolean hasLevelData(ItemStack itemStack)
	{
		if (!itemStack.hasItemMeta())
		{
			return false;
		}
		
		if (!itemStack.getItemMeta().hasLore())
		{
			return false;
		}
		
		if (Util.searchListForString(itemStack.getItemMeta().getLore(), "Level", "false", "▲") == "false")
		{
			System.out.println("Failed search.");
			return false;
		}
		
		return true;
	}
	
	public static int getLevel(ItemMeta meta)
	{
		String rawData = Util.searchListForString(meta.getLore(), "Level", "ERROR", "▲");
		
		if (rawData == "ERROR")
		{
			Bukkit.getLogger().warning("Error reading level data!");
			return 0;
		}
		
		String[] split = rawData.split(" ");
		
		return Integer.valueOf(split[1]);
	}
	
	public static int getExperience(ItemMeta meta)
	{
		String rawData = Util.searchListForString(meta.getLore(), "EXP:", "ERROR", "▲");
		
		if (rawData == "ERROR")
		{
			Bukkit.getLogger().warning("Error reading experience data!);");
			return 0;
		}
		
		String[] split = rawData.split(": "); 
		
		return readExperience(split[1], 5);
	}
	
	public static Stage getStage(ItemType type, int level)
	{
		Stage stage = null;
		
		for (int i = level; i > 0; i--)
		{
			stage = StageManager.getStage(type, i);
			
			if (stage != null)
			{
				break;
			}
		}
		
		return stage;
	}
	
	public static int readExperience(String expBar, int multiplier)
	{
		ChatColor on = Config.EXP_COLOR;
		ChatColor off = Config.DESCRIPTION_COLOR;
		
		int exp = 0;

		expBar = expBar.substring(1, expBar.length() - 1); // Remove brackets
		
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
	
	public static ItemType getType(ItemStack itemStack)
	{
		return TypeChecker.getWeaponType(itemStack.getType());
	}

	public static String createExpBar(int max, int amount, int ratio)
	{
		ChatColor on = Config.EXP_COLOR;
		ChatColor off = Config.DESCRIPTION_COLOR;
		
		String bar = off + "[";
	
		for (int i = 1; i <= max / ratio; i++)
		{
			if ((amount / ratio) >= i)
				bar += on + "|";
			else
				bar += off + "|";
		}
		bar += off + "]";
	
		return bar;
	}
}
