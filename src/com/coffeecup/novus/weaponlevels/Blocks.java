package com.coffeecup.novus.weaponlevels;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.coffeecup.novus.weaponlevels.util.Util;

public class Blocks
{
	private static List<Location> placedBlockStore;
	private static File placedBlocks;
	
	public static boolean isNaturallyPlaced(Block block)
	{
		return !placedBlockStore.contains(block.getLocation());
	}
	
	public static void addPlacedBlock(Location loc)
	{
		placedBlockStore.add(loc);
	}
	
	public static void removePlacedBlock(Location loc)
	{
		placedBlockStore.remove(loc);
	}
	
	public static void loadBlockStore(String dir) throws IOException
	{
		placedBlockStore = new ArrayList<Location>();
		placedBlocks = new File(dir + "blocks.dat");

		if (!placedBlocks.exists())
		{
			placedBlocks.createNewFile();
		}

		FileInputStream in = new FileInputStream(placedBlocks);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(in)));

		String s;
		while ((s = reader.readLine()) != null)
		{
			List<String> values = Util.getCommaSeperatedValues(s);

			for (String value : values)
			{
				String[] temp = value.split(":");

				World w = Bukkit.getServer().getWorld(temp[0]);
				int x = Integer.valueOf(temp[1]);
				int y = Integer.valueOf(temp[2]);
				int z = Integer.valueOf(temp[3]);

				Location loc = new Location(w, x, y, z);

				placedBlockStore.add(loc);
			}
		}

		reader.close();
	}

	public static void saveBlockStore()
	{		
		String values = "";
		for (Location loc : placedBlockStore)
		{
			String w = loc.getWorld().getName();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

			values += w + ":" + x + ":" + y + ":" + z + ",";
		}
		
		try
		{
			FileOutputStream out = new FileOutputStream(placedBlocks);
			PrintStream printer = new PrintStream(out);

			printer.print(values);

			printer.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
}