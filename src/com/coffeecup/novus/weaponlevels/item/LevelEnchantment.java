package com.coffeecup.novus.weaponlevels.item;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class LevelEnchantment
{
	private Enchantment enchantment;
	private int level;
	
	public LevelEnchantment(Enchantment enchantment, int level)
	{
		this.enchantment = enchantment;
		this.level = level;
	}
	
	public LevelEnchantment(int id, int level)
	{
		this.enchantment = Enchantment.getById(id);
		this.level = level;
	}
	
	public void apply(ItemStack item)
	{
		item.addUnsafeEnchantment(enchantment, level);
	}
}
