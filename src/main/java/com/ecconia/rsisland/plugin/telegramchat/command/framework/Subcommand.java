package com.ecconia.rsisland.plugin.telegramchat.command.framework;

import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Subcommand implements CommandCompleter
{
	private final String name;
	
	public Subcommand(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args)
	{
		return Collections.emptyList();
	}

	public abstract void exec(CommandSender sender, String[] args);
	
	protected Player getPlayer(CommandSender sender)
	{
		if(!(sender instanceof Player))
		{
			throw new NotAPlayerException();
		}
		return (Player) sender;
	}
}
