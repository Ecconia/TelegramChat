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
		
		plugin.getData().token = args[0];
		plugin.save();

		if (plugin.getTelegramHook().auth(plugin.getData().token))
		{
			cs.sendMessage(ChatColor.GREEN + "Successfully connected to Telegram!");
			cs.sendMessage(ChatColor.GREEN + "Add " + plugin.getTelegramHook().getAuthJson().getAsJsonObject("result").get("username").getAsString() + " to Telegram!");
		}
		else
		{
			cs.sendMessage(ChatColor.RED + "Wrong token. Paste the whole token!");
		}
		
		return true;
	}
}
