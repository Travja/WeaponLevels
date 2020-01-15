package com.coffeecup.novus.weaponlevels.type;

import org.bukkit.Material;

public enum ToolType
{
	PICKAXE,
	AXE,
	SHOVEL,
	HOE;

	public static ToolType getByItemName(String string)
	{
		String[] temp = string.split("_");

		if (temp.length > 0)
			return ToolType.valueOf(string.split("_")[1]);
		else
			return null;
	}
}
