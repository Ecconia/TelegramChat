package com.github.mastercake10.telegramchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandTelegram implements CommandExecutor
{
	private final TelegramChatPlugin plugin;
	
	public CommandTelegram(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args)
	{
		if (args.length != 1)
		{
			return false;
		}
		
		String token = args[0];
		
		plugin.setToken(token);

		if (plugin.getTelegramHook().changeToken(token))
		{
			cs.sendMessage(ChatColor.GREEN + "Successfully connected to Telegram!");
			cs.sendMessage(ChatColor.GREEN + "Add " + plugin.getTelegramHook().getName() + " to Telegram!");
		}
		else
		{
			cs.sendMessage(ChatColor.RED + "Invalid token, please check the token again.");
		}
		
		return true;
	}
}
