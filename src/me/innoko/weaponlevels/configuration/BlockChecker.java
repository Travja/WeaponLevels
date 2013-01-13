package me.innoko.weaponlevels.configuration;

import me.innoko.weaponlevels.WL;

import org.bukkit.block.Block;

public class BlockChecker
{
	/**
	 * Checks if a block was spawned naturally.
	 * 
	 * @param block
	 *            - The block to check.
	 * @return True if the block was spawned naturally.
	 */
	public static boolean isNaturallyPlaced(Block block)
	{
		return !WL.placedBlockStore.contains(block.getLocation());
	}
}