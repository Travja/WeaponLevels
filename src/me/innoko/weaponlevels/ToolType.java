package me.innoko.weaponlevels;

public enum ToolType
{
	PICKAXE,
	AXE,
	SPADE,
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
