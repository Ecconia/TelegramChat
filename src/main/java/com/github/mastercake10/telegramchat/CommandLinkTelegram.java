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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(ChatColor.RED + "Chatting as Console is currently not supported.");
		}
		
		if (plugin.getTelegramHook().isRegistered())
		{
			sender.sendMessage(ChatColor.RED + "Please register a bot for this server first.");
			return true;
		}

		String token = TelegramChatPlugin.generateLinkToken();
		
		plugin.getData().linkCodes.put(token, ((Player) sender).getUniqueId());
		
		sender.sendMessage(ChatColor.GREEN + "Add " + plugin.getTelegramHook().getName() + " to Telegram and send following message to it: " + token);

		return true;
	}
}
