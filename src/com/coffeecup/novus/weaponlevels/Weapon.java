package com.coffeecup.novus.weaponlevels;

import java.util.ArrayList;
import java.util.List;


import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import think.rpgitems.item.RPGItem;

import com.coffeecup.novus.weaponlevels.configuration.Config;
import com.coffeecup.novus.weaponlevels.configuration.ItemChecker;

public class Weapon
{
	public static final String codeSeq = "▲";
	
	private WLPlugin plugin;

	private int level;
	private int experience;
	private String name;
	private List<String> lore;

	private ItemStack itemStack;
	private ItemMeta meta;

	public WeaponType type;	

	public Weapon(WLPlugin instance, ItemStack item)
	{
		this.plugin = instance;

		if (!hasWeaponLevelMeta(item))
		{
			level = 1;
			experience = 0;

			if (item.hasItemMeta())
			{
				name = item.getItemMeta().getDisplayName();
				
				if (name == null)
				{
					name = ItemChecker.getInGameName(plugin, item.getType());
				}
			}
			else
			{
				name = ItemChecker.getInGameName(plugin, item.getType());
			}

			lore = new ArrayList<String>();

			itemStack = item;
			meta = itemStack.getItemMeta();
		}
		else
		{
			itemStack = item;
			meta = itemStack.getItemMeta();

			name = meta.getDisplayName();
			lore = (ArrayList<String>) meta.getLore();
			level = Integer.valueOf(Util.searchListForString(lore, codeSeq + "Level", "Level 1").split(" ")[1]);
			experience = Util.readExperience(
					Util.searchListForString(lore, codeSeq + "EXP", Util.createExpBar(100, 0, 5)).substring(7), 5,
					ChatColor.WHITE, ChatColor.GRAY);
		}

		type = ItemChecker.getWeaponType(plugin, itemStack.getType());
	}

	public static boolean hasWeaponLevelMeta(ItemStack item)
	{
		if (!item.hasItemMeta())
			return false;

		ItemMeta meta = item.getItemMeta();

		String search = Util.searchListForString(meta.getLore(), codeSeq, "false");
		
		if (search != "false")
			return true;
		else
			return false;
	}

	public ItemStack getItemStack()
	{
		return itemStack;
	}

	public int getExperience()
	{
		return experience;
	}

	public int getLevel()
	{
		return level;
	}

	public void addExperience(int amount)
	{
		// Prevent stacks from gaining xp
		if (!Config.ALLOW_STACKS && itemStack.getAmount() > 1)
		{
			return;
		}
		
		experience += amount;

		if (experience >= 100)
		{
			addLevel();
			experience = experience - 100;
		}

		if (level >= Config.MAX_LEVEL)
		{
			experience = 100;
		}
	}

	public void addLevel()
	{
		level++;

		if (level > Config.MAX_LEVEL)
		{
			level = Config.MAX_LEVEL;
			experience = 100;
		}
	}

	public LevelStats getLevelStats()
	{
		if (level >= Config.getLevel(type, LevelStats.BEST))
		{
			return LevelStats.BEST;
		}
		else if (level >= Config.getLevel(type, LevelStats.GREAT))
		{
			return LevelStats.GREAT;
		}
		else if (level >= Config.getLevel(type, LevelStats.BETTER))
		{
			return LevelStats.BETTER;
		}
		else if (level >= Config.getLevel(type, LevelStats.GOOD))
		{
			return LevelStats.GOOD;
		}
		else
		{
			return LevelStats.BASIC;
		}
	}

	public void applyEnchantments(LevelStats stats)
	{
		List<Twin<Integer>> enchants = Config.getEnchantments(type, stats);

		if (enchants == null)
		{
			return;
		}

		for (Twin<Integer> twin : enchants)
		{
			int eID = twin.getFirst();
			int level = twin.getSecond();

			if (level == 0)
				continue;

			itemStack.addUnsafeEnchantment(Enchantment.getById(eID), level);
		}
	}

	public void update()
	{
		if (Config.USE_RPG)
		{
			RPGItem rpg = plugin.toRPGItem(itemStack);
			
			if (rpg == null)
			{
				updateNormal();
			}
			else
			{
				updateRPG(rpg);
			}
		}
		else 
		{
			updateNormal();
		}
	}
	
	public void updateNormal()
	{
		lore.clear();
		lore.add(ChatColor.GRAY + "▲Level " + level);
		lore.add(ChatColor.GRAY + "▲EXP: " + Util.createExpBar(100, experience, 5));

		meta.setLore(lore);
		meta.setDisplayName(Config.getColor(type, getLevelStats()) + ChatColor.stripColor(name));

		itemStack.setItemMeta(meta);

		applyEnchantments(getLevelStats());
	}
	 
	public void updateRPG(RPGItem rpg)
	{
		lore = rpg.getTooltipLines("en_GB");
		lore = lore.subList(1, lore.size());
		
		int levelID = Util.searchListForStringID(lore, codeSeq + "Level", -1);
		if (levelID != -1)
		{
			lore.remove(levelID);
		}
		
		int expID = Util.searchListForStringID(lore, codeSeq + "EXP", -1);
		if (expID != -1)
		{
			lore.remove(expID);
		}
		
		lore.add(ChatColor.GRAY + "▲Level " + level);
		lore.add(ChatColor.GRAY + "▲EXP: " + Util.createExpBar(100, experience, 5));
		
		meta.setLore(lore);
		
		itemStack.setItemMeta(meta);
		
		applyEnchantments(getLevelStats());
	}
}
