package com.github.mastercake10.TelegramChat;

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
		//TODO: Use bukkit command permission instead of this
		if (!cs.hasPermission("telegram.settoken"))
		{
			cs.sendMessage(ChatColor.RED + "You don't have permissions to use this command!");
			return true;
		}
		
		//TODO: Return false
		if (args.length != 1)
		{
			cs.sendMessage(ChatColor.RED + "Usage: /telegram <token>");
			return true;
		}
		
		//TODO: this won't ever happen!
		if (plugin.getData() == null)
		{
			plugin.resetData();
		}
		
		plugin.getData().token = args[0];
		plugin.save();

		if (plugin.getTelegramHook().auth(plugin.getData().token))
		{
			cs.sendMessage(ChatColor.GREEN + "Successfully connected to Telegram!");
			cs.sendMessage(ChatColor.GREEN + "Add " + plugin.getTelegramHook().authJson.getAsJsonObject("result").get("username").getAsString() + " to Telegram!");
		}
		else
		{
			cs.sendMessage(ChatColor.RED + "Wrong token. Paste the whole token!");
		}
		
		return true;
	}
}
