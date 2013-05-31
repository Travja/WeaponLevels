package com.coffeecup.novus.weaponlevels.listeners;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;

import com.coffeecup.novus.weaponlevels.WLPlugin;
import com.coffeecup.novus.weaponlevels.Weapon;
import com.coffeecup.novus.weaponlevels.configuration.Config;

public class EventListener implements Listener
{
	private WLPlugin plugin;

	public HashMap<Player, Weapon> tempWeaponStorage;
	
	public EventListener(WLPlugin wl)
	{
		plugin = wl;
		tempWeaponStorage = new HashMap<Player, Weapon>();
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onArmSwing(PlayerAnimationEvent event)
	{
		if (event.getAnimationType() == PlayerAnimationType.ARM_SWING)
		{
			Player player = event.getPlayer();
			
			if (player.getItemInHand() == null)
			{
				return;
			}
			
			tempWeaponStorage.put(player, new Weapon(plugin, player.getItemInHand()));
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void preInteract(PlayerInteractEvent event)
	{
		if (Config.USE_RPG)
		{
			tempWeaponStorage.put(event.getPlayer(), new Weapon(plugin, event.getItem()));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void postInteract(PlayerInteractEvent event)
	{
		if (Config.USE_RPG)
		{
			Weapon weapon = tempWeaponStorage.get(event.getPlayer());
			weapon.update();
		}
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void preBlockBreak(BlockBreakEvent event)
	{
		if (Config.USE_RPG)
		{
			System.out.println("preblock");
			tempWeaponStorage.put(event.getPlayer(), new Weapon(plugin, event.getPlayer().getItemInHand()));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void postBlockBreak(BlockBreakEvent event)
	{
		if (Config.USE_RPG)
		{
			System.out.println("postblock");
			Weapon weapon = tempWeaponStorage.get(event.getPlayer());
			weapon.update();
		}
	}
}
