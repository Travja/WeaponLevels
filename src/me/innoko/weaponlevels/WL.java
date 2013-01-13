package me.innoko.weaponlevels;

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
import java.util.logging.Logger;

import me.innoko.weaponlevels.configuration.Config;
import me.innoko.weaponlevels.configuration.ItemChecker;
import me.innoko.weaponlevels.listeners.ArmorListener;
import me.innoko.weaponlevels.listeners.ToolListener;
import me.innoko.weaponlevels.listeners.WeaponListener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class WL extends JavaPlugin
{
	public PluginManager pm;

	public Logger log;

	public static List<Location> placedBlockStore;

	public static File placedBlocks;

	public List<LivingEntity> spawnedMobs;

	@Override
	public void onDisable()
	{
		saveBlockStore();

		log.info("WeaponLevels by InnoKo is now disabled.");
	}

	@Override
	public void onEnable()
	{
		pm = getServer().getPluginManager();
		log = Logger.getLogger("Minecraft");

		pm.registerEvents(new WeaponListener(this), this);
		pm.registerEvents(new ToolListener(this), this);
		pm.registerEvents(new ArmorListener(this), this);

		spawnedMobs = new ArrayList<LivingEntity>();

		try
		{
			loadData();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}

		ItemChecker.loadItems(this);

		log.info("WeaponLevels by InnoKo is now enabled!");
	}

	private void loadData() throws IOException, InvalidConfigurationException
	{
		File folder = getDataFolder();

		if (!folder.exists())
		{
			folder.mkdir();
		}
		else
		{
			// Delete the old config file if it is there
			File oldConfigFile = new File(folder.getPath() + File.separator + "config.yml");

			if (oldConfigFile.exists())
			{
				oldConfigFile.delete();
			}
		}

		String dataPath = folder.getPath() + File.separator;

		File configFolder = new File(dataPath + "configuration");

		if (!configFolder.exists())
		{
			configFolder.mkdir();
		}

		Config.loadOptionConfig(this);
		Config.loadConfigValues(this);
		Config.loadWeaponConfig(this, WeaponType.ARMOR);
		Config.loadWeaponConfig(this, WeaponType.ITEM);
		Config.loadWeaponConfig(this, WeaponType.TOOL);
		Config.loadWeaponConfig(this, WeaponType.WEAPON);

		loadBlockStore(dataPath);
	}

	private void loadBlockStore(String dir) throws IOException
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

				World w = getServer().getWorld(temp[0]);
				int x = Integer.valueOf(temp[1]);
				int y = Integer.valueOf(temp[2]);
				int z = Integer.valueOf(temp[3]);

				Location loc = new Location(w, x, y, z);

				placedBlockStore.add(loc);
			}
		}

		reader.close();
	}

	private void saveBlockStore()
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

		values = values.substring(values.length());

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
