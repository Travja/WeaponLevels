package com.coffeecup.novus.weaponlevels.stages;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coffeecup.novus.weaponlevels.Config;
import com.coffeecup.novus.weaponlevels.item.LevelEnchantment;
import com.coffeecup.novus.weaponlevels.type.TypeChecker;

public class Stage
{
	private final String name;
	private final int level;
	private final ChatColor color;
	private final List<LevelEnchantment> enchantments;
	private final Map<String, Integer> bonuses;
	
	public Stage(String name, int level, ChatColor color, List<LevelEnchantment> enchantments, Map<String, Integer> bonuses)
	{
		this.name = name;
		this.level = level;
		this.color = color;
		this.enchantments = enchantments;
		this.bonuses = bonuses;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public ChatColor getColor()
	{
		return color;
	}
	
	public List<LevelEnchantment> getEnchantments()
	{
		return enchantments;
	}
	
	public int getBonus(String name)
	{
		return bonuses.get(name);
	}
	
	public void apply(ItemStack item)
	{
		if (Config.USE_RPG)
		{
			// RPGItems implementation here
		}
		else if (item.hasItemMeta())
		{
			ItemMeta meta = item.getItemMeta();
			
			if (meta.hasDisplayName())
			{
				if (ChatColor.stripColor(meta.getDisplayName()).equals(TypeChecker.getInGameName(item.getType())))
				{
					meta.setDisplayName(getColor() + TypeChecker.getInGameName(item.getType()));
				}
			}
			else
			{
				meta.setDisplayName(getColor() + TypeChecker.getInGameName(item.getType()));
			}
			
			item.setItemMeta(meta);
			
			for (LevelEnchantment enchantment : getEnchantments())
			{
				enchantment.apply(item);
			}
		}
	}
}
