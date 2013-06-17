package com.coffeecup.novus.weaponlevels.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.item.*;

public class WeaponLevelsAPI
{	
	public static int getLevel(ItemStack item)
	{
		return new LevelData(item).getLevel();
	}
	
	public static int getLevel(Block block)
	{
		return BlockDataManager.getLevel(block);
	}
	
	public static int getExperience(ItemStack item)
	{
		return new LevelData(item).getExperience();
	}
	
	public static int getExperience(Block block)
	{
		return BlockDataManager.getExperience(block);
	}
	
	public static void addExperience(Player player, ItemStack item, int amount)
	{
		new LevelData(item).addExperience(player, amount);
	}
	
	public static int getBonus(ItemStack item, String bonus)
	{
		return new LevelData(item).getStage().getBonus(bonus);
	}
}
