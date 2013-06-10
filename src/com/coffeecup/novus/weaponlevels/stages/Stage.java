package com.coffeecup.novus.weaponlevels.stages;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.item.LevelEnchantment;

public abstract class Stage
{
	private final String name;
	private final int level;
	private final ChatColor color;
	private final List<LevelEnchantment> enchantments;
	
	public Stage(String name, int level, ChatColor color, List<LevelEnchantment> enchantments)
	{
		this.name = name;
		this.level = level;
		this.color = color;
		this.enchantments = enchantments;
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
	
	public abstract void apply(ItemStack item);
}
