package com.coffeecup.novus.weaponlevels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.coffeecup.novus.weaponlevels.item.BlockDataManager;
import com.coffeecup.novus.weaponlevels.item.LevelData;
import com.coffeecup.novus.weaponlevels.item.LevelDataManager;
import com.coffeecup.novus.weaponlevels.stages.Stage;
import com.coffeecup.novus.weaponlevels.type.ItemType;
import com.coffeecup.novus.weaponlevels.type.ToolType;
import com.coffeecup.novus.weaponlevels.type.TypeChecker;
import com.coffeecup.novus.weaponlevels.util.Util;

public class Events implements Listener
{
	private WLPlugin plugin;
	
	private HashMap<Player, LevelData> itemStorage = new HashMap<Player, LevelData>();
	private HashMap<Arrow, LevelData> arrowStorage = new HashMap<Arrow, LevelData>();
	private HashMap<Player, Block> craftStorage = new HashMap<Player, Block>();
	private List<UUID> spawnStorage = new ArrayList<UUID>();
	
	public Events(WLPlugin wlPlugin)
	{
		plugin = wlPlugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAnimate(PlayerAnimationEvent event)
	{
		Player player = event.getPlayer();
		ItemStack itemStack = player.getItemInHand();
		
		if (itemStack != null)
		{
			itemStorage.put(player, new LevelData(itemStack));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDamage(EntityDamageByEntityEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			ItemStack[] armor = player.getInventory().getArmorContents();
			
			for (ItemStack itemStack : armor)
			{
				if (!(itemStack == null || itemStack.getTypeId() == 0 || 
						!(Config.isItemEnabled(plugin, itemStack.getTypeId())) || event.isCancelled() || 
						(event.getDamager() instanceof Player && !(player.getWorld().getPVP()))))
				{
					LevelData data = new LevelData(itemStack);
					
					Stage stage = data.getStage();
					
					boolean hasPermission = Permissions.hasPermissionConfig(player, data.getStage().getName());

					if (hasPermission)
					{
						event.setDamage(event.getDamage() - stage.getBonus("armor"));

						if (data.addExperience(player, Config.EXP_PER_HIT))
						{
							Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
						}
					}
				}
			}
		}
		
		if (event.getDamager() instanceof Player)
		{
			Player player = (Player) event.getDamager();
			ItemStack itemStack = player.getItemInHand();
			
			if (!(itemStack == null || itemStack.getTypeId() == 0 || !(Config.isItemEnabled(plugin, itemStack.getTypeId())) || 
					event.isCancelled() || event.getEntity().hasMetadata("NPC") ||
					(event.getEntity() instanceof Player && !(player.getWorld().getPVP())) ||
					(Config.DISABLE_SPAWNERS && spawnStorage.contains(event.getEntity().getUniqueId())) ||
					!(Config.ALLOW_STACKS) && itemStack.getAmount() > 1))
			{
				LevelData data = itemStorage.get(player);
				
				if (data == null)
				{
					data = new LevelData(itemStack);
				}
				
				boolean hasPermission = Permissions.hasPermissionConfig(player, data.getStage().getName());
				
				if (hasPermission)
				{
					if (LevelDataManager.getType(itemStack) == ItemType.WEAPON)
					{
						Stage stage = data.getStage();
							
						event.setDamage(event.getDamage() + stage.getBonus("damage"));
					}
					
					if (data.addExperience(player, Config.EXP_PER_HIT))
					{
						Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
					}
				}	
				else
				{
					player.sendMessage(ChatColor.RED + "This weapon's level is too high for you to use!");
					event.setCancelled(true);
				}
				
				itemStorage.put(player, data);
			}
		}
		
		if (event.getDamager() instanceof Arrow)
		{
			Arrow arrow = (Arrow) event.getDamager();
			
			if (arrowStorage.containsKey(arrow))
			{
				LevelData data = arrowStorage.get(arrow);
				Stage stage = data.getStage();
				
				event.setDamage(event.getDamage() + stage.getBonus("damage"));
				
				if (data.addExperience((Player) arrow.getShooter(), Config.EXP_PER_HIT))
				{
					Util.dropExperience(arrow.getLocation(), Config.EXP_ON_LEVEL, 3);
				}
				
				arrowStorage.remove(arrow);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(EntityDeathEvent event)
	{
		if (spawnStorage.contains(event.getEntity().getUniqueId()))
		{
			spawnStorage.remove(event.getEntity().getUniqueId());
			
			if (Config.DISABLE_SPAWNERS)
			{
				return;
			}
		}
		
		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
			
			if (damageEvent.getDamager() instanceof Player)
			{
				Player player = (Player) damageEvent.getDamager();
				ItemStack itemStack = player.getItemInHand();
				
				if (itemStack == null || itemStack.getTypeId() == 0 || Config.isItemEnabled(plugin, itemStack.getTypeId()) ||
						!Config.ALLOW_STACKS && itemStack.getAmount() > 1)
				{
					return;
				}
				
				LevelData data = itemStorage.get(player);
				
				if (data == null)
				{
					data = new LevelData(itemStack);
				}
				
				boolean hasPermission = Permissions.hasPermissionConfig(player, data.getStage().getName());
				
				if (hasPermission)
				{
					if (data.addExperience(player, Config.getDeathExperience(plugin, event.getEntityType())))
					{
						Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
					}
				}
				
				itemStorage.put(player, data);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSpawn(CreatureSpawnEvent event)
	{
		if (event.getSpawnReason() == SpawnReason.SPAWNER)
		{
			spawnStorage.add(event.getEntity().getUniqueId());
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if (event.getClickedBlock().getType().equals(Material.WORKBENCH))
			{
				System.out.println("Stored");
				craftStorage.put(event.getPlayer(), event.getClickedBlock());
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onCraft(CraftItemEvent event)
	{
		Player player = (Player) event.getWhoClicked();
		ItemStack itemStack = event.getCurrentItem();
		
		if (!Config.ALLOW_STACKS && itemStack.getAmount() > 1)
		{
			return;
		}
		
		LevelData item = new LevelData(itemStack);
		item.setLevel(Util.getLevelOnCurve(1, Util.getMaxLevel(player, LevelDataManager.getType(itemStack)), Config.CRAFT_RATIO));
		item.update();
		
		Block block = craftStorage.get(player);
		
		if (block != null)
		{
			System.out.println("Added");
			BlockDataManager.addExperience(block, Config.EXP_PER_HIT);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		if (!event.isCancelled())
		{
			Blocks.add(event.getBlock());
			
			Block block = event.getBlock();
			ItemStack itemStack = event.getItemInHand();
			LevelData data = new LevelData(itemStack);
			BlockDataManager.put(block, data);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		ItemStack itemStack = player.getItemInHand();
		boolean cancel = false;
		
		if (itemStack != null && itemStack.getTypeId() != 0 && Config.isItemEnabled(plugin, itemStack.getTypeId()) &&
				!event.isCancelled() && Blocks.isNatural(event.getBlock()) &&  
				TypeChecker.getItemType(itemStack.getType()) == ItemType.TOOL &&
				TypeChecker.isCorrectTool(ToolType.getByItemName(itemStack.getType().name()), event.getBlock()))
		{
			LevelData data = itemStorage.get(player);
			
			if (data == null)
			{
				data = new LevelData(itemStack);
			}
			
			boolean hasPermission = Permissions.hasPermissionConfig(player, data.getStage().getName());
			
			if (hasPermission)
			{
				if (data.addExperience(player, Config.EXP_PER_HIT))
				{
					Util.dropExperience(event.getBlock().getLocation(), Config.EXP_ON_LEVEL, 3);
				}
			}
			else
			{
				player.sendMessage(ChatColor.RED + "This tool's level is too high for you to use!");
				cancel = true;
			}
			
			itemStorage.put(player, data);
		}
		
		if (!cancel)
		{
			ItemStack stack = new ItemStack(event.getBlock().getType(), 1);
			BlockDataManager.apply(player, event.getBlock(), stack);
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), stack);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerFish(PlayerFishEvent event)
	{
		Player player = event.getPlayer();
		Fish hook = event.getHook();
		ItemStack rod = player.getItemInHand();
		LevelData rodData = itemStorage.get(player);
		
		if (rodData == null)
		{
			rodData = new LevelData(rod);
		}
		
		boolean hasPermission = Permissions.hasPermissionConfig(player, rodData.getStage().getName());
		
		if (hasPermission)
		{
			switch (event.getState())
			{
			case FISHING:
				hook.setBiteChance(hook.getBiteChance() + rodData.getStage().getBonus("fishing"));
				break;
			case CAUGHT_FISH:
				ItemStack fish = new ItemStack(Material.RAW_FISH, 1);
				LevelData fishData = new LevelData(fish);
				
				fishData.setLevel(Util.getLevelOnCurve(1, rodData.getLevel(), Config.CRAFT_RATIO));
				
				if (rodData.addExperience(player, Config.EXP_PER_HIT))
				{
					Util.dropExperience(player.getLocation(), Config.EXP_ON_LEVEL, 3);
				}
				
				player.getInventory().addItem(fish);
				player.updateInventory();
				event.setCancelled(true);
				
				break;
			default:
				break;
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "This rod's level is too high for you to use!");
			hook.eject();
			event.setCancelled(true);
		}
		
		itemStorage.put(player, rodData);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerEat(FoodLevelChangeEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			ItemStack food = player.getItemInHand();
			LevelData data = new LevelData(food);
			
			player.setFoodLevel(player.getFoodLevel() + data.getStage().getBonus("food"));
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onFurnaceBurn(FurnaceBurnEvent event)
	{
		ItemStack fuel = event.getFuel();
		LevelData data = new LevelData(fuel);
		
		event.setBurnTime(event.getBurnTime() - data.getStage().getBonus("fuel"));
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onFurnaceSmelt(FurnaceSmeltEvent event)
	{
		ItemStack source = event.getSource();
		ItemStack result = event.getResult();
		LevelData sourceData = new LevelData(source);
		LevelData resultData = new LevelData(result);
		
		resultData.setLevel(sourceData.getLevel());
		
		BlockDataManager.addExperience(event.getBlock(), Config.EXP_PER_HIT);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onShootBow(EntityShootBowEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = (Player) event.getEntity();
			ItemStack bow = event.getBow();
			LevelData bowData = new LevelData(bow);
			
			boolean hasPermission = Permissions.hasPermissionConfig(player, bowData.getStage().getName());
			
			if (hasPermission)
			{
				Vector velocity = event.getProjectile().getVelocity();
				Arrow arrow = player.launchProjectile(Arrow.class);
				arrow.setVelocity(velocity);
				arrowStorage.put(arrow, bowData);
			}
			else
			{
				
			}
		}
	}
}
