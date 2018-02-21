package com.github.mastercake10.telegramchat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandLinkTelegram implements CommandExecutor
{
	private final TelegramChatPlugin plugin;
	
	public CommandLinkTelegram(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args)
	{
		if (!(cs instanceof Player))
		{
			cs.sendMessage(ChatColor.RED + "Sorry, but you can't link the console currently.");
		}
		
		if (plugin.getTelegramHook().authJson == null)
		{
			cs.sendMessage(ChatColor.RED + "Please add a bot to your server first! /telegram");
			return true;
		}

		String token = TelegramChatPlugin.generateLinkToken();
		plugin.getData().linkCodes.put(token, ((Player) cs).getUniqueId());
		
		cs.sendMessage(ChatColor.GREEN + "Add " + plugin.getTelegramHook().authJson.getAsJsonObject("result").get("username").getAsString() + " to Telegram and send this message to " + plugin.getTelegramHook().authJson.getAsJsonObject("result").get("username").getAsString() + ":");
		//TODO: Its spigot... why isn't it onclick?
		cs.sendMessage(ChatColor.GREEN + token);

		return true;
	}
}
