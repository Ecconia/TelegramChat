package com.ecconia.rsisland.plugin.telegramchat.commands;

import org.bukkit.command.CommandSender;

import com.ecconia.rsisland.framework.cofami.Subcommand;
import com.ecconia.rsisland.plugin.telegramchat.TelegramPlugin;
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions.AnswerException;
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions.ConnectionException;
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions.InvalidTokenException;

public class CommandToken extends Subcommand
{
private final TelegramPlugin plugin;
	
	public CommandToken(TelegramPlugin plugin)
	{
		super("token");
		this.plugin = plugin;
	}

	@Override
	public void exec(CommandSender sender, String[] args)
	{
		checkPermission(sender);
		
		if (args.length != 1)
		{
			f.e(sender, "Usage: %v", path + " <token>");
			return;
		}
		
		String token = args[0];
		
		try
		{
			plugin.setToken(token);
			plugin.getLogger().info("Logged in as " + plugin.getBotName());
			plugin.enableTriggers();
			
			f.n(sender, "Successfully connected to Telegram!");
			f.n(sender, "Add " + plugin.getBotName() + " to Telegram!");
		}
		catch (AnswerException e)
		{
			plugin.getLogger().severe("TelegramAPI refuses Token: " + e.getMessage());
			f.e(sender, "TelegramAPI refuses Token: " + e.getMessage());
		}
		catch (ConnectionException e)
		{
			plugin.getLogger().severe("Error connecting to TelegramAPI: " + e.getMessage());
			f.e(sender, "Error connecting to TelegramAPI: " + e.getMessage());
		}
		catch (InvalidTokenException e)
		{
			plugin.getLogger().severe("Applied an invalid token, please change. Token >" + e.getMessage() + "<");
			f.e(sender, "The token has a wrong format. Token >" + e.getMessage() + "<");
		}
	}
}
