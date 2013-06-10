package com.coffeecup.novus.weaponlevels;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.coffeecup.novus.weaponlevels.type.ItemType;
import com.coffeecup.novus.weaponlevels.type.TypeChecker;
import com.coffeecup.novus.weaponlevels.util.Util;

public class Config
{
	public static YamlConfiguration OPTIONCONFIG = new YamlConfiguration();
	public static YamlConfiguration WEAPONCONFIG = new YamlConfiguration();
	public static YamlConfiguration ARMORCONFIG = new YamlConfiguration();
	public static YamlConfiguration TOOLCONFIG = new YamlConfiguration();
	public static YamlConfiguration ITEMCONFIG = new YamlConfiguration();

	public static boolean PERMS_TO_LEVEL;
	public static boolean PERMS_TO_USE;
	public static ChatColor DESCRIPTION_COLOR;
	public static ChatColor EXP_COLOR;
	public static double CRAFT_RATIO;
	public static int MAX_LEVEL;
	public static boolean NON_WEAPONS_ENABLED;
	public static int EXP_ON_LEVEL;
	public static int EXP_PER_HIT;
	public static boolean DISABLE_SPAWNERS;
	public static boolean ALLOW_STACKS;

	public static boolean USE_RPG;

	public static void loadOptionConfig(Plugin plugin) throws IOException, InvalidConfigurationException
	{
		loadConfig(plugin, OPTIONCONFIG, "config.yml");
	}

	public static void loadWeaponConfig(Plugin plugin, ItemType type) throws IOException,
			InvalidConfigurationException
	{
		loadConfig(plugin, type.config, type.filename);
	}

	public static void loadConfig(Plugin plugin, YamlConfiguration config, String filename) throws IOException,
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

	public static void loadConfigValues(Plugin plugin)
	{
		FileConfiguration config = OPTIONCONFIG;

		PERMS_TO_LEVEL = config.getBoolean("general.require permissions for leveling items");
		PERMS_TO_USE = config.getBoolean("general.require permissions for using items");
		DESCRIPTION_COLOR = Util.getSafeChatColor(config.getString("general.item description color"), ChatColor.GRAY);
		EXP_COLOR = Util.getSafeChatColor(config.getString("general.item experience bar color"), ChatColor.WHITE);
		CRAFT_RATIO = config.getDouble("general.crafting ratio");
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

	public static int getDeathExperience(Plugin plugin, EntityType type)
	{
		String name = type.name().replace('_', ' ').toLowerCase();
		int exp = plugin.getConfig().getInt("general.experience per kill." + name);

		if (exp >= 6)
			return exp;
		else
			return 6;
	}

	public static boolean isItemEnabled(Plugin plugin, int typeId)
	{
		String disabledItems = plugin.getConfig().getString("general.disabled items");

		if (Util.getCommaSeperatedValues(disabledItems).contains(String.valueOf(typeId)))
		{
			return false;
		}

		if (!NON_WEAPONS_ENABLED)
		{
			if (!TypeChecker.isWeapon(Material.getMaterial(typeId))
					&& !TypeChecker.isArmor(Material.getMaterial(typeId))
					&& !TypeChecker.isTool(Material.getMaterial(typeId)))
			{
				return false;
			}
		}

		return true;
	}
}
