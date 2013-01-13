package me.innoko.weaponlevels.listeners;

import java.util.HashMap;

import me.innoko.weaponlevels.Util;
import me.innoko.weaponlevels.WL;
import me.innoko.weaponlevels.Weapon;
import me.innoko.weaponlevels.configuration.Config;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class WeaponListener implements Listener
{
	private WL plugin;

	private HashMap<Entity, Weapon> arrows;

	public WeaponListener(WL instance)
	{
		plugin = instance;

		arrows = new HashMap<Entity, Weapon>();
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (event.getDamager() instanceof Player)
		{
			Player player = (Player) event.getDamager();
			ItemStack item = player.getItemInHand();

			if (item == null || item.getTypeId() == 0 || !Config.isItemEnabled(plugin, item.getTypeId()))
			{
				return;
			}

			if (player.getWorld().getPVP() == false || event.isCancelled())
			{
				return;
			}

			if (event.getEntity() instanceof Player)
			{
				// Check if the entity damaged is a Citizens NPC
				if (((Player) event.getEntity()).hasMetadata("NPC"))
				{
					return;
				}
			}

			Weapon weapon = new Weapon(plugin, item);

			event.setDamage(event.getDamage() + Config.getDamage(weapon.type, weapon.getLevelStats()));

			if (plugin.spawnedMobs.contains(event.getEntity()))
			{
				return;
			}

			int levelState = weapon.getLevel();
			weapon.addExperience(Config.EXP_PER_HIT);
			weapon.update();

			if (weapon.getLevel() > levelState)
			{
				Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
			}
		}
		else
		{
			if (arrows.containsKey(event.getDamager()))
			{
				Weapon weapon = arrows.get(event.getDamager());

				event.setDamage(event.getDamage() + Config.getDamage(weapon.type, weapon.getLevelStats()));

				if (plugin.spawnedMobs.contains(event.getEntity()))
				{
					return;
				}

				int levelState = weapon.getLevel();
				weapon.addExperience(Config.EXP_PER_HIT);
				weapon.update();

				if (weapon.getLevel() > levelState)
				{
					Util.dropExperience(event.getDamager().getLocation(), Config.EXP_ON_LEVEL, 3);
				}

				arrows.remove(event.getDamager());
			}
		}
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (plugin.spawnedMobs.contains(event.getEntity()))
		{
			plugin.spawnedMobs.remove(event.getEntity());
			return;
		}

		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getEntity()
					.getLastDamageCause();

			if (damageEvent.getDamager() instanceof Player)
			{
				Player player = (Player) damageEvent.getDamager();

				ItemStack item = player.getItemInHand();

				if (item == null || item.getTypeId() == 0 || !Config.isItemEnabled(plugin, item.getTypeId()))
					return;

				Weapon weapon = new Weapon(plugin, item);

				int levelState = weapon.getLevel();
				weapon.addExperience(Config.getDeathExperience(plugin, event.getEntity().getType()));
				weapon.update();

				if (weapon.getLevel() > levelState)
				{
					Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
				}
			}
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event)
	{
		if (Config.DISABLE_SPAWNERS && event.getSpawnReason() == SpawnReason.SPAWNER)
		{
			plugin.spawnedMobs.add(event.getEntity());
		}
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			ItemStack item = event.getBow();

			if (item == null || !Config.isItemEnabled(plugin, item.getTypeId()))
			{
				return;
			}

			Weapon weapon = new Weapon(plugin, item);
			int levelState = weapon.getLevel();

			if (weapon.getLevel() > levelState)
			{
				Util.dropExperience(event.getEntity().getLocation(), Config.EXP_ON_LEVEL, 3);
			}

			arrows.put(event.getProjectile(), weapon);
		}
	}
}
