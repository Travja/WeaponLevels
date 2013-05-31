package com.coffeecup.novus.weaponlevels;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.coffeecup.novus.weaponlevels.listeners.ArmorListener;
import com.coffeecup.novus.weaponlevels.listeners.CommandListener;
import com.coffeecup.novus.weaponlevels.listeners.EventListener;
import com.coffeecup.novus.weaponlevels.listeners.ToolListener;
import com.coffeecup.novus.weaponlevels.listeners.WeaponListener;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import think.rpgitems.api.RPGItems;
import think.rpgitems.item.RPGItem;

import com.coffeecup.novus.weaponlevels.configuration.BlockChecker;
import com.coffeecup.novus.weaponlevels.configuration.Config;
import com.coffeecup.novus.weaponlevels.configuration.ItemChecker;

public class WLPlugin extends JavaPlugin
{
	public PluginManager pm;
	public PluginDescriptionFile pdf;
	public Logger log;

	public List<LivingEntity> spawnedMobs;
	
	public WeaponListener weaponListener;
	public ToolListener toolListener;
	public ArmorListener armorListener;
	public EventListener eventListener;
	public CommandListener cmdListener;
	
	public RPGItems rpgItems;
	
	@Override
	public void onEnable()
	{
		pm = getServer().getPluginManager();
		pdf = getDescription();
		log = Logger.getLogger("Minecraft");
		
		spawnedMobs = new ArrayList<LivingEntity>();
		
		weaponListener = new WeaponListener(this);
		toolListener = new ToolListener(this);
		armorListener = new ArmorListener(this);
		eventListener = new EventListener(this);
		cmdListener = new CommandListener(this);

		pm.registerEvents(weaponListener, this);
		pm.registerEvents(toolListener, this);
		pm.registerEvents(armorListener, this);
		pm.registerEvents(eventListener, this);
		
		getCommand("wl").setExecutor(cmdListener);
		
		Config.USE_RPG = pm.getPlugin("RPG Items") != null;
		rpgItems = new RPGItems();
		
		if (Config.USE_RPG)
		{
			log.warning("RPG ITEMS DETECTED. ITEMS CREATED BY RPG ITEMS WILL CURRENTLY NOT WORK WITH THIS PLUGIN.");
		}
		
		PermissionManager.BPERMISSIONS = pm.getPlugin("bPermissions") != null;

		try
		{
			loadData();
		}
		catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}

		ItemChecker.loadItems(this);

		log.info("WeaponLevels v" + pdf.getVersion() + " by " + pdf.getAuthors() + " is now enabled!");
	}
	
	@Override
	public void onDisable()
	{
		log.info("Storing player-placed blocks...");
		BlockChecker.saveBlockStore();

		log.info("WeaponLevels v" + pdf.getVersion() + " by " + pdf.getAuthors() + " is now disabled.");
	}
	
	public void onReload()
	{
		log.info("Reloading WeaponLevels v" + pdf.getVersion() +"...");
		
		log.info("Storing player-placed blocks...");
		BlockChecker.saveBlockStore();
		
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
		ItemChecker.loadItems(this);	
		
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
	
	public RPGItem toRPGItem(ItemStack itemstack)
	{
		if (Config.USE_RPG)
		{
			return rpgItems.toRPGItem(itemstack);
		}
		else
		{
			return null;
		}
	}
}
