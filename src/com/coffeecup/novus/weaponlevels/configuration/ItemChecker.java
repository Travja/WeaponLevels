package com.coffeecup.novus.weaponlevels.configuration;

import java.io.IOException;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.coffeecup.novus.weaponlevels.ToolType;
import com.coffeecup.novus.weaponlevels.Util;
import com.coffeecup.novus.weaponlevels.WLPlugin;
import com.coffeecup.novus.weaponlevels.WeaponType;

public class ItemChecker
{
	private static YamlConfiguration names = null;

	public static void loadItems(WLPlugin plugin)
	{
		names = new YamlConfiguration();

		try
		{
			names.load(plugin.getResource("names.yml"));
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Check if an item is a weapon.
	 * 
	 * @param plugin
	 *            - Instance of main class
	 * @param material
	 *            - The material of the item to check
	 * @return True if the item is a weapon.
	 */
	public static boolean isWeapon(WLPlugin plugin, Material material)
	{
		if (names == null)
		{
			loadItems(plugin);
		}

		return names.getConfigurationSection("weapons").getValues(false).containsKey(material.name());
	}

	/**
	 * Check if an item is armor.
	 * 
	 * @param plugin
	 *            - Instance of main class
	 * @param material
	 *            - The material of the item to check
	 * @return True if the item is armor.
	 */
	public static boolean isArmor(WLPlugin plugin, Material material)
	{
		if (names == null)
		{
			loadItems(plugin);
		}

		return names.getConfigurationSection("armor").getValues(false).containsKey(material.name());
	}

	/**
	 * Check if an item is a tool.
	 * 
	 * @param plugin
	 *            - Instance of main class
	 * @param material
	 *            - The material of the item to check
	 * @return True if the item is a tool.
	 */
	public static boolean isTool(WLPlugin plugin, Material material)
	{
		if (names == null)
		{
			loadItems(plugin);
		}

		return names.getConfigurationSection("tools").getValues(false).containsKey(material.name());
	}

	/**
	 * Checks if a tool is the correct tool to use for the specified block.
	 * 
	 * @param plugin
	 *            - Instance of main class
	 * @param block
	 *            - The block to check
	 * @param tool
	 *            - The tool to check
	 * @return True if the tool is the proper tool for the block.
	 */
	public static boolean isCorrectTool(WLPlugin plugin, ToolType tool, Block block)
	{
		if (names == null)
		{
			loadItems(plugin);
		}

		String values = names.getConfigurationSection("correct tools.").getString(tool.name());

		return Util.getCommaSeperatedValues(values).contains(String.valueOf(block.getTypeId()));
	}

	/**
	 * Get the name of the item that is shown in-game.
	 * 
	 * @param plugin
	 *            - Instance of main class
	 * @param material
	 *            - The material to get the name of
	 * @return The in-game name of the item.
	 */
	public static String getInGameName(WLPlugin plugin, Material material)
	{
		if (isWeapon(plugin, material))
		{
			return names.getConfigurationSection("weapons").getString(material.name());
		}
		else if (isArmor(plugin, material))
		{
			return names.getConfigurationSection("armor").getString(material.name());
		}
		else if (isTool(plugin, material))
		{
			return names.getConfigurationSection("tools").getString(material.name());
		}
		else
		{
			return Util.capitalizeFirst(material.name(), '_');
		}
	}

	public static WeaponType getWeaponType(WLPlugin plugin, Material material)
	{
		if (isWeapon(plugin, material))
		{
			return WeaponType.WEAPON;
		}
		else if (isArmor(plugin, material))
		{
			return WeaponType.ARMOR;
		}
		else if (isTool(plugin, material))
		{
			return WeaponType.TOOL;
		}
		else
		{
			return WeaponType.ITEM;
		}
	}
}
