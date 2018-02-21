package com.github.mastercake10.TelegramChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LinkTelegramCmd implements CommandExecutor
{
	private final TelegramChatPlugin plugin;
	
	public LinkTelegramCmd(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args)
	{
		if (!(cs instanceof Player))
		{
			cs.sendMessage("§cSorry, but you can't link the console currently.");
		}
		
		if (!cs.hasPermission("telegram.linktelegram"))
		{
			cs.sendMessage("§cYou don't have permissions to use this!");
			return true;
		}
		
		if (plugin.getData() == null)
		{
			plugin.resetData();
		}
		
		if (TelegramChatPlugin.telegramHook.authJson == null)
		{
			cs.sendMessage("§cPlease add a bot to your server first! /telegram");
			return true;
		}

		String token = TelegramChatPlugin.generateLinkToken();
		plugin.getData().linkCodes.put(token, ((Player) cs).getUniqueId());
		
		cs.sendMessage("§aAdd " + TelegramChatPlugin.telegramHook.authJson.getAsJsonObject("result").get("username").getAsString() + " to Telegram and send this message to " + TelegramChatPlugin.telegramHook.authJson.getAsJsonObject("result").get("username").getAsString() + ":");
		cs.sendMessage("§c" + token);

		return true;
	}
}
