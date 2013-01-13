package me.innoko.weaponlevels.listeners;

import me.innoko.weaponlevels.Util;
import me.innoko.weaponlevels.WL;
import me.innoko.weaponlevels.Weapon;
import me.innoko.weaponlevels.WeaponType;
import me.innoko.weaponlevels.configuration.Config;
import me.innoko.weaponlevels.configuration.ItemChecker;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorListener implements Listener
{
	private WL plugin;

	public ArmorListener(WL instance)
	{
		plugin = instance;
	}

	@EventHandler
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
