package me.innoko.weaponlevels.listeners;

import me.innoko.weaponlevels.ToolType;
import me.innoko.weaponlevels.Util;
import me.innoko.weaponlevels.WL;
import me.innoko.weaponlevels.Weapon;
import me.innoko.weaponlevels.configuration.BlockChecker;
import me.innoko.weaponlevels.configuration.Config;
import me.innoko.weaponlevels.configuration.ItemChecker;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class ToolListener implements Listener
{
	private WL plugin;

	public ToolListener(WL instance)
	{
		plugin = instance;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Block block = event.getBlock();

		WL.placedBlockStore.add(block.getLocation());
	}

	@EventHandler
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
				Weapon weapon = new Weapon(plugin, item);

				int levelState = weapon.getLevel();
				weapon.addExperience(Config.EXP_PER_HIT);
				weapon.update();

				if (weapon.getLevel() > levelState)
				{
					Util.dropExperience(block.getLocation(), Config.EXP_ON_LEVEL, 3);
				}
			}
		}

		WL.placedBlockStore.remove(block);
	}
}
