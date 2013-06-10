package com.coffeecup.novus.weaponlevels.type;


import org.bukkit.configuration.file.YamlConfiguration;

import com.coffeecup.novus.weaponlevels.Config;

public enum ItemType
{
	WEAPON(Config.WEAPONCONFIG, "weapons.yml"),
	TOOL(Config.TOOLCONFIG, "tools.yml"),
	ARMOR(Config.ARMORCONFIG, "armor.yml"),
	ITEM(Config.ITEMCONFIG, "items.yml");

	public YamlConfiguration config;
	public String filename;

	ItemType(YamlConfiguration config, String filename)
	{
		this.config = config;
		this.filename = filename;
	}
}
