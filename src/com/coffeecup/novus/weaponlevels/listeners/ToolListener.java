package com.coffeecup.novus.weaponlevels.listeners;


import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.ToolType;
import com.coffeecup.novus.weaponlevels.Util;
import com.coffeecup.novus.weaponlevels.WLPlugin;
import com.coffeecup.novus.weaponlevels.Weapon;
import com.coffeecup.novus.weaponlevels.configuration.BlockChecker;
import com.coffeecup.novus.weaponlevels.configuration.Config;
import com.coffeecup.novus.weaponlevels.configuration.ItemChecker;

public class ToolListener implements Listener
{
	private WLPlugin plugin;

	public ToolListener(WLPlugin instance)
	{
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block block = event.getBlock();

		BlockChecker.addPlacedBlock((block.getLocation()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlock();
		ItemStack item = player.getItemInHand();

		if (!BlockChecker.isNaturallyPlaced(block))
		{
			return;
		}

		if (ItemChecker.isTool(plugin, item.getType()))
		{
			if (ItemChecker.isCorrectTool(plugin, ToolType.getByItemName(item.getType().name()), block))
			{
				Weapon weapon = plugin.eventListener.tempWeaponStorage.get(player);

				int levelState = weapon.getLevel();
				weapon.addExperience(Config.EXP_PER_HIT);
				weapon.update();

				if (weapon.getLevel() > levelState)
				{
					Util.dropExperience(block.getLocation(), Config.EXP_ON_LEVEL, 3);
				}
			}
		}

		BlockChecker.removePlacedBlock((block.getLocation()));
	}
}
