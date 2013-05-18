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
import java.util.logging.Logger;

import com.coffeecup.novus.weaponlevels.listeners.ArmorListener;
import com.coffeecup.novus.weaponlevels.listeners.ToolListener;
import com.coffeecup.novus.weaponlevels.listeners.WeaponListener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.coffeecup.novus.weaponlevels.configuration.BlockChecker;
import com.coffeecup.novus.weaponlevels.configuration.Config;
import com.coffeecup.novus.weaponlevels.configuration.ItemChecker;

public class WLPlugin extends JavaPlugin
{
	public PluginManager pm;

	public Logger log;

	public List<LivingEntity> spawnedMobs;	

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

		log.info("WeaponLevels by TheRealNovus is now enabled!");
	}
	
	@Override
	public void onDisable()
	{
		log.info("Storing player-placed blocks...");
		BlockChecker.saveBlockStore();

		log.info("WeaponLevels by TheRealNovus is now disabled.");
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

		BlockChecker.loadBlockStore(dataPath);
	}


}
