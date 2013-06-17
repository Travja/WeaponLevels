package com.coffeecup.novus.weaponlevels;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import com.coffeecup.novus.weaponlevels.type.TypeChecker;
import com.coffeecup.novus.weaponlevels.util.ConfigFile;
import com.coffeecup.novus.weaponlevels.util.Util;

public class Config
{
	public static ConfigFile CONFIG = new ConfigFile("config.yml");
	public static ConfigFile STAGES = new ConfigFile("stages.yml");
	public static ConfigFile GROUPS = new ConfigFile("groups.yml");
	public static ConfigFile ITEMS = new ConfigFile("items.yml");

	public static boolean USE_PERMS;
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

	public static void loadConfig(WLPlugin plugin, ConfigFile config) throws IOException,
			InvalidConfigurationException
	{
		File folder = plugin.getDataFolder();

		String dataPath = folder.getPath() + File.separator + "configuration" + File.separator;

		File configFile = new File(dataPath + config.getName());

		if (!configFile.exists())
		{
			configFile.createNewFile();
		}

		ConfigFile defaultConfig = new ConfigFile(config.getName());
		defaultConfig.load(plugin.getResource(config.getName()));

		config.load(configFile);
		config.setDefaults(defaultConfig);
		config.options().copyDefaults(true);
		config.save(configFile);
	}

	public static void loadConfigValues(WLPlugin plugin)
	{
		USE_PERMS = CONFIG.getBoolean("general.use permissions");
		DESCRIPTION_COLOR = Util.getSafeChatColor(CONFIG.getString("general.item description color"), ChatColor.GRAY);
		EXP_COLOR = Util.getSafeChatColor(CONFIG.getString("general.item experience bar color"), ChatColor.WHITE);
		CRAFT_RATIO = CONFIG.getDouble("general.crafting ratio");
		MAX_LEVEL = CONFIG.getInt("general.maximum level");
		NON_WEAPONS_ENABLED = CONFIG.getBoolean("general.normal items enabled");
		EXP_ON_LEVEL = CONFIG.getInt("general.experience on level up");
		EXP_PER_HIT = CONFIG.getInt("general.experience per hit");
		DISABLE_SPAWNERS = CONFIG.getBoolean("general.disable mob spawners");
		ALLOW_STACKS = CONFIG.getBoolean("general.allow stacks");

		if (EXP_PER_HIT < 5)
		{
			EXP_PER_HIT = 5;
		}
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
			if (!TypeChecker.isWeapon(Material.getMaterial(typeId))
					&& !TypeChecker.isArmor(Material.getMaterial(typeId))
					&& !TypeChecker.isTool(Material.getMaterial(typeId)))
			{
				return false;
			}
		}

		return true;
	}
	
	public static void removeOldData(WLPlugin plugin)
	{
		try
		{
			deleteOldConfigs(plugin);
		} 
		catch (IOException | InvalidConfigurationException e)
		{
			
		}
		
		CONFIG.set("general.require permissions for leveling items", null);
		CONFIG.set("general.require permissions for using items", null);
	}
	
	private static void deleteOldConfigs(WLPlugin plugin) throws IOException, InvalidConfigurationException
	{
		File folder = plugin.getDataFolder();
		
		new File(folder.getPath() + File.separator + "config.yml").delete();
		
		for (File file : new File(folder.getPath() + File.separator + "configuration").listFiles())
		{
			if (file.getName().equalsIgnoreCase("armor.yml"))
				file.delete();
			if (file.getName().equalsIgnoreCase("weapons.yml"))
				file.delete();
			if (file.getName().equalsIgnoreCase("tools.yml"))
				file.delete();
			if (file.getName().equalsIgnoreCase("items.yml"))
			{
				YamlConfiguration config = new YamlConfiguration();
				config.load(file);
				if (config.contains("stages"))
					file.delete();
			}
		}
	}
}
