package com.coffeecup.novus.weaponlevels.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.coffeecup.novus.weaponlevels.Config;
import com.coffeecup.novus.weaponlevels.stages.Stage;
import com.coffeecup.novus.weaponlevels.type.ItemType;

import com.coffeecup.novus.weaponlevels.Permissions;

public class LevelData
{
	private ItemStack itemStack;
	private ItemType type;
	private Stage stage;
	private int level;
	private int experience;
	private boolean hasExperienceBar;
	
	public LevelData(ItemStack itemStack)
	{
		this.itemStack = itemStack;
		this.hasExperienceBar = LevelDataManager.hasExperienceBar(itemStack);
		
		if (LevelDataManager.hasLevelData(itemStack))
		{
			readLevelData();
		}
		else
		{
			createNewLevelData();
		}
	}
	
	public LevelData(ItemStack itemStack, boolean experienceBar)
	{
		this.itemStack = itemStack;
		this.hasExperienceBar = experienceBar;
		
		if (LevelDataManager.hasLevelData(itemStack))
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
		return stage;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	private boolean addLevel(Player player)
	{
		Stage stage = LevelDataManager.getStage(type, level + 1);
		
		if (Permissions.hasPermissionConfig(player, stage.getName()))
		{
			level++;
			experience -= 100;
			
			return true;
		}
		else
		{
			return false;
		}
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
	
	public boolean addExperience(Player player, int amount)
	{
		experience += amount;
		
		boolean levelUp = false;
		
		while (experience >= 100)
		{
			if (addLevel(player))
			{
				levelUp = true;
			}
			else
			{
				levelUp = false;
				break;
			}
		}
		
		update();
		
		return levelUp;
	}
	
	public boolean hasExperienceBar()
	{
		return hasExperienceBar;
	}
	
	public void setExperienceBar(boolean experienceBar)
	{
		hasExperienceBar = experienceBar;
		update();
	}
	
	private void readLevelData()
	{
		ItemMeta meta = itemStack.getItemMeta();
		
		type = LevelDataManager.getType(itemStack);
		level = LevelDataManager.getLevel(meta);
		experience = LevelDataManager.getExperience(meta, hasExperienceBar);
		stage = LevelDataManager.getStage(type, level);
	}
	
	private void writeLevelData()
	{
		ItemMeta meta = itemStack.getItemMeta();
		
		if (meta == null)
		{
			return;
		}
		
		List<String> lore = meta.getLore();
		
		if (lore == null) 
		{
			lore = new ArrayList<>();
		}

		List<String> markDelete = new ArrayList<>();
		for(String str: lore) {
			if(str.contains(Config.DESCRIPTION_COLOR + "Level ") || str.contains(Config.DESCRIPTION_COLOR + "EXP: "))
				markDelete.add(str);
		}

		for(String str: markDelete) {
			lore.remove(str);
		}

		if(!lore.isEmpty())
			lore.add("");
		
		lore.add(Config.DESCRIPTION_COLOR + "Level " + level);
		
		if (hasExperienceBar)
		{
			lore.add(Config.DESCRIPTION_COLOR + "EXP: " + LevelDataManager.createExpBar(100, experience, 5));
		}
		
		meta.setLore(lore);
		itemStack.setItemMeta(meta);
	}
	
	private void createNewLevelData()
	{
		type = LevelDataManager.getType(itemStack);
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
		
		stage = LevelDataManager.getStage(type, level);
		
		if (stage != null)
		{
			stage.apply(itemStack);
		}
		
		writeLevelData();
	}
}