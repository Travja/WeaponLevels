package com.coffeecup.novus.weaponlevels.stages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.item.LevelEnchantment;

public class NullStage extends Stage
{

	public NullStage()
	{
		super("LevelItem", 0, ChatColor.WHITE, new ArrayList<LevelEnchantment>());
	}

	@Override
	public void apply(ItemStack item)
	{
	}

}
