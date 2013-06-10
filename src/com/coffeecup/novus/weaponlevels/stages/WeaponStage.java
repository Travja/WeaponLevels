package com.coffeecup.novus.weaponlevels.stages;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coffeecup.novus.weaponlevels.Config;
import com.coffeecup.novus.weaponlevels.item.LevelEnchantment;
import com.coffeecup.novus.weaponlevels.type.TypeChecker;

public class WeaponStage extends Stage
{
	private int damage;
	
	public WeaponStage(String name, int level, ChatColor color, List<LevelEnchantment> enchantments, int damage)
	{
		super(name, level, color, enchantments);
		
		this.damage = damage;
	}

	@Override
	public void apply(ItemStack item)
	{
		if (Config.USE_RPG)
		{
			
		}
		else
		{
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(getColor() + TypeChecker.getInGameName(item.getType()));			
			item.setItemMeta(meta);
			
			for (LevelEnchantment enchantment : getEnchantments())
			{
				enchantment.apply(item);
			}
		}
	}
	
	public int getDamageBonus()
	{
		return damage;
	}
	
}
