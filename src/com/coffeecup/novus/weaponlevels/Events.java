package com.coffeecup.novus.weaponlevels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.item.LevelItem;
import com.coffeecup.novus.weaponlevels.item.LevelItemManager;
import com.coffeecup.novus.weaponlevels.util.Util;

public class Events implements Listener
{
	private Plugin plugin;
	
	private HashMap<Player, LevelItem> itemStorage = new HashMap<Player, LevelItem>();
	private List<UUID> spawnStorage = new ArrayList<UUID>();
	
	public Events(Plugin wlPlugin)
	{
		plugin = wlPlugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAnimate(PlayerAnimationEvent event)
	{
		Player player = event.getPlayer();
		ItemStack itemStack = player.getItemInHand();
		
		if (itemStack != null)
		{
			itemStorage.put(player, new LevelItem(itemStack));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Player)
		{
			Player player = (Player) event.getDamager();
			ItemStack itemStack = player.getItemInHand();
			
			if (itemStack == null || itemStack.getTypeId() == 0 || !Config.isItemEnabled(plugin, itemStack.getTypeId()) || 
					event.isCancelled() || event.getEntity().hasMetadata("NPC") ||
					(event.getEntity() instanceof Player && !player.getWorld().getPVP()) ||
					(Config.DISABLE_SPAWNERS && spawnStorage.contains(event.getEntity().getUniqueId())) ||
					!Config.ALLOW_STACKS && itemStack.getAmount() > 1)
			{
				return;
			}
			
			LevelItem levelItem = itemStorage.get(player);
			
			if (levelItem == null)
			{
				levelItem = new LevelItem(itemStack);
			}
			
			boolean hasPermission = true;
			
			if (!Permissions.hasPermission(player, levelItem.getStage().getName()))
			{
				if (Config.PERMS_TO_USE)
				{
					player.sendMessage(ChatColor.RED + "This weapon's level is too high for you to use!");
					event.setCancelled(true);
					return;
				}
				
				if (Config.PERMS_TO_LEVEL)
				{
					hasPermission = false;
				}
			}
			
			if (hasPermission)
			{
				if (levelItem.addExperience(Config.EXP_PER_HIT))
				{
					Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
				}
			}
			
			itemStorage.put(player, levelItem);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(EntityDeathEvent event)
	{
		if (spawnStorage.contains(event.getEntity().getUniqueId()))
		{
			spawnStorage.remove(event.getEntity().getUniqueId());
			
			if (Config.DISABLE_SPAWNERS)
			{
				return;
			}
		}
		
		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
			
			if (damageEvent.getDamager() instanceof Player)
			{
				Player player = (Player) damageEvent.getDamager();
				ItemStack itemStack = player.getItemInHand();
				
				if (itemStack == null || itemStack.getTypeId() == 0 || Config.isItemEnabled(plugin, itemStack.getTypeId()) ||
						!Config.ALLOW_STACKS && itemStack.getAmount() > 1)
				{
					return;
				}
				
				LevelItem levelItem = itemStorage.get(player);
				
				if (levelItem == null)
				{
					levelItem = new LevelItem(itemStack);
				}
				
				boolean hasPermission = true;
				
				if (!Permissions.hasPermission(player, levelItem.getStage().getName()))
				{
					if (Config.PERMS_TO_LEVEL)
					{
						hasPermission = false;
					}
				}
				
				if (hasPermission)
				{
					if (levelItem.addExperience(Config.getDeathExperience(plugin, event.getEntityType())))
					{
						Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
					}
				}
				
				itemStorage.put(player, levelItem);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSpawn(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == SpawnReason.SPAWNER)
		{
			spawnStorage.add(event.getEntity().getUniqueId());
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraft(CraftItemEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		ItemStack itemStack = event.getCurrentItem();
		
		if (!Config.ALLOW_STACKS && itemStack.getAmount() > 1)
		{
			return;
		}
		
		LevelItem item = new LevelItem(itemStack);
		item.setLevel(Util.getLevelOnCurve(1, Util.getMaxLevel(player, LevelItemManager.getType(itemStack)), Config.CRAFT_RATIO));
		item.update();
	}
}
