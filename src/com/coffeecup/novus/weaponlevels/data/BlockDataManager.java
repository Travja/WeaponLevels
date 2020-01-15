package com.coffeecup.novus.weaponlevels.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.util.Util;

public class BlockDataManager
{
	/*private static HashMap<Location, BlockData> data = new HashMap<Location, BlockData>();
	
	public static void put(Block block, LevelData data)
	{
		BlockDataManager.data.put(block.getLocation(), new BlockData(data.getLevel(), data.getExperience()));
	}
	
	public static void apply(Player player, Block block, ItemStack itemStack)
	{
		check(block);
		data.get(block.getLocation()).apply(player, itemStack);
	}
	
	public static int getLevel(Block block)
	{
		check(block);
		return data.get(block.getLocation()).level;
	}
	
	public static int getExperience(Block block)
	{
		check(block);
		return data.get(block.getLocation()).exp;
	}
	
	public static void addExperience(Block block, int amount)
	{
		check(block);
		data.put(block.getLocation(), data.get(block.getLocation()).addExperience(amount));
	}
	
	private static void check(Block block)
	{
		if (!data.containsKey(block.getLocation()))
		{
			data.put(block.getLocation(), new BlockData(1, 0));
		}
	}

	public static void load(File file) throws IOException
	{
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		
		String str;
		while ((str = reader.readLine()) != null)
		{
			String[] split = str.split(":");
			World w = Bukkit.getWorld(split[0]);
			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			int z = Integer.parseInt(split[3]);
			int level = Integer.parseInt(split[4]);
			int exp = Integer.parseInt(split[5]);
			
			data.put(new Location(w, x, y, z), new BlockData(level, exp));
		}
		
		reader.close();
	}
	
	public static void save(File file) throws IOException
	{
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		
		PrintStream printer = new PrintStream(new FileOutputStream(file));
		
		for (Entry<Location, BlockData> entry : data.entrySet())
		{
			Location loc = entry.getKey();
			BlockData block = entry.getValue();
			String w = loc.getWorld().getName();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();
			int level = block.level;
			int exp = block.exp;
			
			Util.printlnObj(printer, w, x, y, z, level, exp);
		}
		
		printer.close();
	}*/
}
