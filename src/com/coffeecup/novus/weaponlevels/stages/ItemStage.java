package com.coffeecup.novus.weaponlevels.stages;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.item.LevelEnchantment;

public class ItemStage extends Stage
{

	public ItemStage(String name, int level, ChatColor color, List<LevelEnchantment> enchantments)
	{
		super(name, level, color, enchantments);
	}

	@Override
	public void apply(ItemStack weapon)
	{
		
	}

}
