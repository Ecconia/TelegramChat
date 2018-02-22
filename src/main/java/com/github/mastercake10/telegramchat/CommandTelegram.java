package com.github.mastercake10.telegramchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.mastercake10.telegramchat.http.ConnectionException;
import com.github.mastercake10.telegramchat.telegram.AnswerException;

//TODO: reload, status
//TODO: oops add permission check again.
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
				
				try
				{
					plugin.getTelegramHook().changeToken(token);
					plugin.getLogger().info("Logged in as " + plugin.getTelegramHook().getName());
					plugin.enableTriggers();
					
					sender.sendMessage(ChatColor.GREEN + "Successfully connected to Telegram!");
					sender.sendMessage(ChatColor.GREEN + "Add " + plugin.getTelegramHook().getName() + " to Telegram!");
				}
				catch (AnswerException e)
				{
					plugin.getLogger().severe("TelegramAPI refuses Token: " + e.getMessage());
					sender.sendMessage(ChatColor.RED + "TelegramAPI refuses Token: " + e.getMessage());
				}
				catch (ConnectionException e)
				{
					plugin.getLogger().severe("Error connecting to TelegramAPI: " + e.getMessage());
					sender.sendMessage(ChatColor.RED + "Error connecting to TelegramAPI: " + e.getMessage());
				}
				catch (InvalidTokenException e)
				{
					plugin.getLogger().severe("Applied an invalid token, please change. Token >" + e.getMessage() + "<");
					sender.sendMessage(ChatColor.RED + "The token has a wrong format. Token >" + e.getMessage() + "<");
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
