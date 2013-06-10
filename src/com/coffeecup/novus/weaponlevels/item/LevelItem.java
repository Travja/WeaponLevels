package com.coffeecup.novus.weaponlevels.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coffeecup.novus.weaponlevels.Config;
import com.coffeecup.novus.weaponlevels.stages.NullStage;
import com.coffeecup.novus.weaponlevels.stages.Stage;
import com.coffeecup.novus.weaponlevels.type.ItemType;

public class LevelItem
{
	private ItemStack itemStack;
	private ItemType type;
	private Stage stage;
	private int level;
	private int experience;
	
	public LevelItem(ItemStack itemStack)
	{
		this.itemStack = itemStack;
		
		if (LevelItemManager.hasLevelData(itemStack))
		{
			readLevelData();
		}
		else
		{
			createNewLevelData();
		}
	}
	
	public ItemStack getItemStack()
	{
		return itemStack;
	}
	
	public Stage getStage()
	{
		if (stage == null)
		{
			return new NullStage();
		}
		
		return stage;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int level)
	{
		this.level = level;
		update();
	}
	
	public int getExperience()
	{
		return experience;
	}
	
	public boolean addExperience(int amount)
	{		
		experience += amount;
		
		boolean levelUp = false;
		
		while (experience >= 100)
		{
			level++;
			experience -= 100;
			
			levelUp = true;
		}
		
		update();
		
		return levelUp;
	}
	
	private void readLevelData()
	{
		ItemMeta meta = itemStack.getItemMeta();
		
		type = LevelItemManager.getType(itemStack);
		level = LevelItemManager.getLevel(meta);
		experience = LevelItemManager.getExperience(meta);
		stage = LevelItemManager.getStage(type, level);
	}
	
	private void writeLevelData()
	{
		ItemMeta meta = itemStack.getItemMeta();
		List<String> lore = meta.getLore();
		
		if (lore == null) 
		{
			lore = new ArrayList<String>();
		}
		
		lore.clear();
		lore.add(Config.DESCRIPTION_COLOR + "Level " + level);
		lore.add(Config.DESCRIPTION_COLOR + "EXP: " + LevelItemManager.createExpBar(100, experience, 5));
		
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
	}
	
	private void createNewLevelData()
	{
		type = LevelItemManager.getType(itemStack);
		level = 1;
		experience = 0;
		
		update();
	}
	
	public void update()
	{
		if (level > Config.MAX_LEVEL)
		{
			level = Config.MAX_LEVEL;
			experience = 100;
		}
		
		stage = LevelItemManager.getStage(type, level);
		
		if (stage != null)
		{
			stage.apply(itemStack);
		}
		
		writeLevelData();
	}
}