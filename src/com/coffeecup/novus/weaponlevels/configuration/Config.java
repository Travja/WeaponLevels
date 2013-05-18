package com.coffeecup.novus.weaponlevels.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.coffeecup.novus.weaponlevels.LevelStats;
import com.coffeecup.novus.weaponlevels.Twin;
import com.coffeecup.novus.weaponlevels.Util;
import com.coffeecup.novus.weaponlevels.WLPlugin;
import com.coffeecup.novus.weaponlevels.WeaponType;

public class Config
{
	public static YamlConfiguration OPTIONCONFIG = new YamlConfiguration();
	public static YamlConfiguration WEAPONCONFIG = new YamlConfiguration();
	public static YamlConfiguration ARMORCONFIG = new YamlConfiguration();
	public static YamlConfiguration TOOLCONFIG = new YamlConfiguration();
	public static YamlConfiguration ITEMCONFIG = new YamlConfiguration();

	public static int MAX_LEVEL;
	public static boolean NON_WEAPONS_ENABLED;
	public static int EXP_ON_LEVEL;
	public static int EXP_PER_HIT;
	public static boolean DISABLE_SPAWNERS;
	public static boolean ALLOW_STACKS;

	public static int getLevel(WeaponType type, LevelStats stats)
	{
		if (type.config == null)
		{
			type.config = new YamlConfiguration();
		}

		YamlConfiguration config = type.config;

		return config.getInt("stages." + stats.name().toLowerCase() + ".level");
	}

	public static int getArmor(WeaponType type, LevelStats stats)
	{
		if (type.config == null)
		{
			type.config = new YamlConfiguration();
		}

		YamlConfiguration config = type.config;

		return config.getInt("stages." + stats.name().toLowerCase() + ".armor");
	}

	public static int getDamage(WeaponType type, LevelStats stats)
	{
		if (type.config == null)
		{
			type.config = new YamlConfiguration();
		}

		YamlConfiguration config = type.config;

		return config.getInt("stages." + stats.name().toLowerCase() + ".damage");
	}

	public static ChatColor getColor(WeaponType type, LevelStats stats)
	{
		if (type.config == null)
		{
			type.config = new YamlConfiguration();
		}

		YamlConfiguration config = type.config;

		String color = config.getString("stages." + stats.name().toLowerCase() + ".color").toUpperCase();

		return ChatColor.valueOf(color);
	}

	public static List<Twin<Integer>> getEnchantments(WeaponType type, LevelStats stats)
	{
		if (type.config == null)
		{
			type.config = new YamlConfiguration();
		}

		YamlConfiguration config = type.config;

		List<Twin<Integer>> list = parseEnchantments(config, "stages." + stats.name().toLowerCase()
				+ ".enchantments");

		return list;
	}

	public static void loadOptionConfig(WLPlugin plugin) throws IOException, InvalidConfigurationException
	{
		loadConfig(plugin, OPTIONCONFIG, "config.yml");
	}

	public static void loadWeaponConfig(WLPlugin plugin, WeaponType type) throws IOException,
			InvalidConfigurationException
	{
		loadConfig(plugin, type.config, type.filename);
	}

	public static void loadConfig(WLPlugin plugin, YamlConfiguration config, String filename) throws IOException,
			InvalidConfigurationException
	{
		File folder = plugin.getDataFolder();

		String dataPath = folder.getPath() + File.separator + "configuration" + File.separator;

		File configFile = new File(dataPath + filename);

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		YamlConfiguration defaultConfig = new YamlConfiguration();
		defaultConfig.load(plugin.getResource(filename));

		config.load(configFile);
		config.setDefaults(defaultConfig);
		config.options().copyDefaults(true);
		config.save(configFile);
	}

	public static void loadConfigValues(WLPlugin plugin)
	{
		FileConfiguration config = OPTIONCONFIG;

		MAX_LEVEL = config.getInt("general.maximum level");
		NON_WEAPONS_ENABLED = config.getBoolean("general.normal items enabled");
		EXP_ON_LEVEL = config.getInt("general.experience on level up");
		EXP_PER_HIT = config.getInt("general.experience per hit");
		DISABLE_SPAWNERS = config.getBoolean("general.disable mob spawners");
		ALLOW_STACKS = config.getBoolean("general.allow stacks");

		if (EXP_PER_HIT < 5)
		{
			EXP_PER_HIT = 5;
		}
	}

	private static List<Twin<Integer>> parseEnchantments(FileConfiguration config, String path)
	{
		List<Twin<Integer>> list = new ArrayList<Twin<Integer>>();
		String enchantments = config.getString(path);

		for (String string : Util.getCommaSeperatedValues(enchantments))
		{
			try
			{
				String[] temp = string.split("\\.");

				int eID = Integer.parseInt(temp[0]);
				int level = Integer.parseInt(temp[1]);

				Twin<Integer> twin = new Twin<Integer>(eID, level);

				list.add(twin);
			}
			catch (NumberFormatException e)
			{
				Logger.getLogger("Minecraft").warning("WeaponLevels Enchantment Format Error!");
			}
		}

		return list;
	}

	public static int getDeathExperience(WLPlugin plugin, EntityType type)
	{
		String name = type.name().replace('_', ' ').toLowerCase();
		int exp = plugin.getConfig().getInt("general.experience per kill." + name);

		if (exp >= 6)
			return exp;
		else
			return 6;
	}

	public static boolean isItemEnabled(WLPlugin plugin, int typeId)
	{
		String disabledItems = plugin.getConfig().getString("general.disabled items");

		if (Util.getCommaSeperatedValues(disabledItems).contains(String.valueOf(typeId)))
		{
			return false;
		}

		if (!NON_WEAPONS_ENABLED)
		{
			if (!ItemChecker.isWeapon(plugin, Material.getMaterial(typeId))
					&& !ItemChecker.isArmor(plugin, Material.getMaterial(typeId))
					&& !ItemChecker.isTool(plugin, Material.getMaterial(typeId)))
			{
				return false;
			}
		}

		return true;
	}
}
