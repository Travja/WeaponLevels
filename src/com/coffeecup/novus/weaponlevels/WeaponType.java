package com.coffeecup.novus.weaponlevels;


import org.bukkit.configuration.file.YamlConfiguration;

import com.coffeecup.novus.weaponlevels.configuration.Config;

public enum WeaponType
{
	WEAPON(Config.WEAPONCONFIG, "weapons.yml"),
	TOOL(Config.TOOLCONFIG, "tools.yml"),
	ARMOR(Config.ARMORCONFIG, "armor.yml"),
	ITEM(Config.ITEMCONFIG, "items.yml");

	public YamlConfiguration config;
	public String filename;

	WeaponType(YamlConfiguration config, String filename)
	{
		this.config = config;
		this.filename = filename;
	}
}
