package com.coffeecup.novus.weaponlevels.stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.coffeecup.novus.weaponlevels.item.LevelEnchantment;
import com.coffeecup.novus.weaponlevels.type.ItemType;
import com.coffeecup.novus.weaponlevels.util.Util;

public class StageManager
{
	private static HashMap<Integer, Stage> armorStages = new HashMap<Integer, Stage>();
	private static HashMap<Integer, Stage> itemStages = new HashMap<Integer, Stage>();
	private static HashMap<Integer, Stage> toolStages = new HashMap<Integer, Stage>();
	private static HashMap<Integer, Stage> weaponStages = new HashMap<Integer, Stage>();
 
	public static void loadStages(ItemType type)
	{
		Map<String, Object> yamlStages = type.config.getConfigurationSection("stages").getValues(false);
		
		for (Entry<String, Object> entry : yamlStages.entrySet())
		{
			String stageName = entry.getKey();
			int level = type.config.getInt("stages." + stageName + ".level");
			Stage stage = createStage(type, stageName);
			
			registerStage(type, stage, level);
		}
	}	
	
	public static Stage getStage(ItemType type, int level)
	{
		return getStageMap(type).get(level);
	}
	
	private static HashMap<Integer, Stage> getStageMap(ItemType type)
	{
		switch (type)
		{
			case ARMOR: return armorStages;
			case ITEM: return itemStages;
			case TOOL: return toolStages;
			case WEAPON: return weaponStages;
			default: return null;
		}
	}
	
	private static void registerStage(ItemType type, Stage stage, Integer level)
	{
		getStageMap(type).put(level, stage);
	}
	
	private static Stage createStage(ItemType type, String stageName)
	{
		YamlConfiguration config = type.config;
		
		switch (type)
		{
			case WEAPON: return createWeaponStage(config, stageName);
			case TOOL: return createToolStage(config, stageName);
			case ITEM: return createItemStage(config, stageName);
			case ARMOR: return createArmorStage(config, stageName);
			default: return null;
		}
	}
	
	private static WeaponStage createWeaponStage(YamlConfiguration config, String stageName)
	{
		ConfigurationSection stage = config.getConfigurationSection("stages." + stageName);
		
		String name = stageName;
		int level = stage.getInt("level");
		ChatColor color = Util.getSafeChatColor(stage.getString("color"), ChatColor.WHITE);
		List<LevelEnchantment> enchantments = getEnchantments(stage.getString("enchantments"));
		int damage = stage.getInt("damage");

		return new WeaponStage(name, level, color, enchantments, damage);
	}
	
	private static ToolStage createToolStage(ConfigurationSection config, String stageName)
	{
		ConfigurationSection stage = config.getConfigurationSection("stages." + stageName);
		
		String name = stageName;
		int level = stage.getInt("level");
		ChatColor color = Util.getSafeChatColor(stage.getString("color"), ChatColor.WHITE);
		List<LevelEnchantment> enchantments = getEnchantments(stage.getString("enchantments"));

		return new ToolStage(name, level, color, enchantments);
	}
	
	private static ItemStage createItemStage(ConfigurationSection config, String stageName)
	{
		ConfigurationSection stage = config.getConfigurationSection("stages." + stageName);
		
		String name = stageName;
		int level = stage.getInt("level");
		ChatColor color = Util.getSafeChatColor(stage.getString("color"), ChatColor.WHITE);
		List<LevelEnchantment> enchantments = getEnchantments(stage.getString("enchantments"));

		return new ItemStage(name, level, color, enchantments);
	}
	
	private static ArmorStage createArmorStage(ConfigurationSection config, String stageName)
	{
		ConfigurationSection stage = config.getConfigurationSection("stages." + stageName);
		
		String name = stageName;
		int level = stage.getInt("level");
		ChatColor color = Util.getSafeChatColor(stage.getString("color"), ChatColor.WHITE);
		List<LevelEnchantment> enchantments = getEnchantments(stage.getString("enchantments"));

		return new ArmorStage(name, level, color, enchantments);
	}	
	
	private static List<LevelEnchantment> getEnchantments(String data)
	{
		List<LevelEnchantment> list = new ArrayList<LevelEnchantment>();
		
		for (String enchantment : Util.getCommaSeperatedValues(data))
		{
			try
			{
				String[] split = enchantment.split("\\.");
				
				int id = Integer.valueOf(split[0]);
				int level = Integer.valueOf(split[1]);
				
				list.add(new LevelEnchantment(id, level));				
			}
			catch (NumberFormatException e)
			{
				Bukkit.getLogger().warning("WeaponLevels: Invalid enchantment format '" + enchantment + "'.");
			}
		}
		
		return list;
	}
}
