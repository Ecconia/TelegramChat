package com.github.mastercake10.telegramchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTelegram implements CommandExecutor
{
	private final TelegramChatPlugin plugin;
	
	public CommandTelegram(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	/*
	 * /telegram <setBotToken|getToken>
	 * /telegram token <token>
	 * /telegram link
	 */
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		int count = args.length;
		
		if(count == 0)
		{
			return false;
		}
		else
		{
			String subcommand = args[0];
			if("link".equals(subcommand))
			{
				if (!(sender instanceof Player))
				{
					sender.sendMessage(ChatColor.RED + "Chatting as Console is currently not supported.");
					return true;
				}
				
				if (plugin.getTelegramHook().isRegistered())
				{
					sender.sendMessage(ChatColor.RED + "Please register a bot for this server first.");
					return true;
				}

				//TODO:
				//Check if registered:
				
				
				
				//Get and print new token:
				String token = plugin.getNewLinkToken((Player) sender);
				
				sender.sendMessage(ChatColor.GREEN + "Add @" + plugin.getTelegramHook().getName() + " to Telegram and send following token to it: " + token);
				return true;
			}
			else if("bot".equals(subcommand))
			{
				if (args.length != 2)
				{
					sender.sendMessage(ChatColor.RED + "Usage: /telegram bot <token>");
					return true;
				}
				String token = args[1];
				
				plugin.setToken(token);
				if (plugin.getTelegramHook().changeToken(token))
				{
					sender.sendMessage(ChatColor.GREEN + "Successfully connected to Telegram!");
					sender.sendMessage(ChatColor.GREEN + "Add " + plugin.getTelegramHook().getName() + " to Telegram!");
				}
				else
				{
					sender.sendMessage(ChatColor.RED + "Invalid token, please check the token again.");
				}
			}
			else
			{
				return false;
			}
		}

		return true;
	}
}
