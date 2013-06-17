package com.coffeecup.novus.weaponlevels;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import com.coffeecup.novus.weaponlevels.data.BlockDataManager;
import com.coffeecup.novus.weaponlevels.stages.StageManager;
import com.coffeecup.novus.weaponlevels.type.TypeChecker;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import think.rpgitems.api.RPGItems;
import think.rpgitems.item.RPGItem;


public class WLPlugin extends JavaPlugin
{
	public PluginManager pm;
	public PluginDescriptionFile pdf;
	public Logger log;
	
	public Events events;
	public Commands cmdListener;
	
	public static RPGItems rpgItems;
	public static think.rpgitems.Plugin rpgPlugin;
	
	@Override
	public void onEnable()
	{
		pm = getServer().getPluginManager();
		pdf = getDescription();
		log = Logger.getLogger("Minecraft");
		
		events = new Events(this);
		cmdListener = new Commands(this);

		pm.registerEvents(events, this);
		
		getCommand("wl").setExecutor(cmdListener);
		
		rpgPlugin = (think.rpgitems.Plugin) pm.getPlugin("RPG Items");
		Config.USE_RPG = rpgPlugin != null;
		
		if (Config.USE_RPG)
			{rpgItems = new RPGItems();}
		
		if (Config.USE_RPG)
		{
			log.warning("RPG ITEMS DETECTED. ITEMS CREATED BY RPG ITEMS WILL CURRENTLY NOT WORK WITH THIS PLUGIN.");
			Config.USE_RPG = false;
		}
		
		Permissions.BPERMISSIONS = pm.getPlugin("bPermissions") != null;

		try
		{
			loadData();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}

		TypeChecker.loadItems(this);

		log.info("WeaponLevels v" + pdf.getVersion() + " by " + pdf.getAuthors() + " is now enabled!");
	}
	
	@Override
	public void onDisable()
	{
		log.info("Storing block data...");
		Blocks.save();
		try
		{
			BlockDataManager.save(new File(getDataFolder() + File.separator + "blocks" + File.separator + "blockdata.dat"));
		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		log.info("WeaponLevels v" + pdf.getVersion() + " by " + pdf.getAuthors() + " is now disabled.");
	}
	
	public void onReload()
	{
		log.info("Reloading WeaponLevels v" + pdf.getVersion() +"...");
		
		log.info("Storing block data...");
		Blocks.save();
		try
		{
			BlockDataManager.save(new File(getDataFolder() + File.separator + "blocks" + File.separator + "blockdata.dat"));
		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		log.info("Loading plugin data...");
		try
		{
			loadData();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		
		log.info("Loading item data...");
		TypeChecker.loadItems(this);	
		
		log.info("WeaponLevels has successfully reloaded.");
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
			File oldConfigFile = new File(folder.getPath() + File.separator + "config.yml");

			if (oldConfigFile.exists())
			{
				oldConfigFile.delete();
			}
			
			File oldBlocksFile = new File(folder.getPath() + File.separator + "blocks.dat");
			
			if (oldBlocksFile.exists())
			{
				oldBlocksFile.delete();
			}
		}

		String dataPath = folder.getPath() + File.separator;

		File configFolder = new File(dataPath + "configuration");

		if (!configFolder.exists())
		{
			configFolder.mkdir();
		}
		
		Config.removeOldData(this);
		Config.loadConfig(this, Config.CONFIG);
		Config.loadConfig(this, Config.STAGES);
		Config.loadConfig(this, Config.GROUPS);
		Config.loadConfig(this, Config.ITEMS);
		Config.loadConfigValues(this);
		
		TypeChecker.loadItems(this);
		
		File blocksFolder = new File(dataPath + "blocks");
		
		if (!blocksFolder.exists())
		{
			blocksFolder.mkdir();
		}
		
		Blocks.load(blocksFolder.getPath());		
		BlockDataManager.load(new File(blocksFolder.getPath() + File.separator + "blockdata.dat"));
		
		StageManager.loadStages();
	}
	
	public static RPGItem toRPGItem(ItemStack itemstack)
	{
		if (Config.USE_RPG)
		{
			//return rpgItems.toRPGItem(itemstack);
			return null;
		}
		else
		{
			return null;
		}
	}
}
