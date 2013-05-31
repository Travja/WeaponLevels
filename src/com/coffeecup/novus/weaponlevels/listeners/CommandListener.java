package com.coffeecup.novus.weaponlevels.listeners;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.coffeecup.novus.weaponlevels.WLPlugin;

public class CommandListener implements CommandExecutor
{
	private WLPlugin plugin;
	
	public CommandListener(WLPlugin wl)
	{
		plugin = wl;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
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
				}
				else if (args[0].equalsIgnoreCase("reload"))
				{
					if (sender instanceof Player)
					{
						if (!((Player) sender).hasPermission("WeaponLevels.reload"))
						{
							sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
							return true;
						}
					}
					
					plugin.onReload();
					sender.sendMessage(ChatColor.GRAY + "Reloaded WeaponLevels.");
				}
			}
			
			return true;
		}
		return false;
	}
	
	public void sendHelp(CommandSender sender)
	{
		
	}
}
