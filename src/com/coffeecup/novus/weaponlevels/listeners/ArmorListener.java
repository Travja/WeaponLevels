package com.coffeecup.novus.weaponlevels.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.Util;
import com.coffeecup.novus.weaponlevels.WLPlugin;
import com.coffeecup.novus.weaponlevels.Weapon;
import com.coffeecup.novus.weaponlevels.WeaponType;
import com.coffeecup.novus.weaponlevels.configuration.Config;
import com.coffeecup.novus.weaponlevels.configuration.ItemChecker;

public class ArmorListener implements Listener
{
	private WLPlugin plugin;

	public ArmorListener(WLPlugin instance)
	{
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();

			ItemStack[] armorSlots = player.getInventory().getArmorContents();

			for (int i = 0; i < armorSlots.length; i++)
			{
				ItemStack armor = armorSlots[i];

				if (armor != null && armor.getTypeId() != 0 && ItemChecker.isArmor(plugin, armor.getType())
						&& Config.isItemEnabled(plugin, armor.getTypeId()))
				{
					Weapon weapon = new Weapon(plugin, armor);

					int armorBonus = Config.getArmor(WeaponType.ARMOR, weapon.getLevelStats());

					event.setDamage(event.getDamage() - armorBonus);

					int levelState = weapon.getLevel();
					weapon.addExperience(Config.EXP_PER_HIT);
					weapon.update();

					if (weapon.getLevel() > levelState)
					{
						Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
					}
				}
			}
		}
	}
}
