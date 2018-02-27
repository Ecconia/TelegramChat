package com.ecconia.rsisland.plugin.telegramchat.commands;

import org.bukkit.command.CommandSender;

import com.ecconia.rsisland.framework.cofami.Subcommand;
import com.ecconia.rsisland.plugin.telegramchat.TelegramPlugin;

public class CommandReload extends Subcommand
{
private final TelegramPlugin plugin;
	
	public CommandReload(TelegramPlugin plugin)
	{
		super("reload");
		this.plugin = plugin;
	}

	@Override
	public void exec(CommandSender sender, String[] args)
	{
		
	}
}
