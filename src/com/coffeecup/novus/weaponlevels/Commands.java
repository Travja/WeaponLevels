package com.coffeecup.novus.weaponlevels;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.coffeecup.novus.weaponlevels.data.LevelData;


public class Commands implements CommandExecutor
{
	private WLPlugin plugin;

	public Commands(WLPlugin wl)
	{
		plugin = wl;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args)
	{
		if (label.equalsIgnoreCase("wl"))
		{
			if (args.length == 0)
			{
				sendHelp(sender);
			} 
			else
			{
				if (args[0].equalsIgnoreCase("version"))
				{
					sender.sendMessage(ChatColor.GRAY + "This server is running WeaponLevels version " + plugin.pdf.getVersion());
					return true;
				} 
				else if (args[0].equalsIgnoreCase("reload"))
				{
					if (sender instanceof Player)
					{
						if (!Permissions.hasPermission((Player) sender, "reload"))
						{
							sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
							return true;
						}
					}

					plugin.onReload();
					sender.sendMessage(ChatColor.GRAY + "Reloaded WeaponLevels.");
					return true;
				}
				else if (args[0].equalsIgnoreCase("setlevel"))
				{
					if (sender instanceof Player)
					{
						if (!Permissions.hasPermission((Player) sender, "setlevel"))
						{
							sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
							return true;
						}
						
						if (args.length < 2)
						{
							sender.sendMessage(ChatColor.RED + "You must specify a level!");
							sender.sendMessage(ChatColor.GRAY + "/wl setlevel <level> - Sets the level of the item in hand.");
							return true;
						}
						
						if (args[1].length() > 7)
						{
							sender.sendMessage(ChatColor.RED + "That number is too high!");
						}
						
						ItemStack itemStack = ((Player) sender).getInventory().getItemInHand();
						
						if (itemStack == null || itemStack.getType() == Material.AIR)
						{
							sender.sendMessage(ChatColor.RED + "You need to be holding an item to use that command!");
							return true;
						}
						
						LevelData item = new LevelData(itemStack);
						item.setLevel(Integer.valueOf(args[1]));
						
						sender.sendMessage(ChatColor.GRAY + "Set item level to " + args[1] + ".");
						return true;
					}
					else
					{
						sender.sendMessage(ChatColor.RED + "You cannot use this command from the console!");
						return true;
					}
				}
			}

			return true;
		}
		
		return false;
	}

	public void sendHelp(CommandSender sender)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			
			if (Permissions.hasPermission(player, "reload"))
			{
				sender.sendMessage(ChatColor.GRAY + "/wl reload - Reloads plugin configuration.");
			}
			
			if (Permissions.hasPermission(player, "setlevel"))
			{
				sender.sendMessage(ChatColor.GRAY + "/wl setlevel <level> - Sets the level of the item in hand.");
			}
			
			sender.sendMessage(ChatColor.GRAY + "/wl version - Displays plugin version information.");
		}
		else
		{
			sender.sendMessage("wl reload - Reloads plugin configuration.");
			sender.sendMessage("wl setlevel <level> - Sets the level of the item in hand.");
			sender.sendMessage("wl version - Displays plugin version information.");
		}
	}
}
