package com.ecconia.rsisland.plugin.telegramchat.command.framework;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import com.ecconia.rsisland.plugin.telegramchat.F;

public class CommandHandler implements CommandExecutor, TabCompleter
{
	private final Map<String, Subcommand> subCommands;
	
	public CommandHandler(Plugin plugin, Subcommand... commands)
	{
		subCommands = new HashMap<>();
		for(Subcommand command : commands)
		{
			subCommands.put(command.getName(), command);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(args.length == 0)
		{
			F.n(sender, "Use tabcomplete to get a list of subcomands.");
			return true;
		}
		
		Subcommand subCommand = subCommands.get(args[0]);
		if(subCommand == null)
		{
			F.e(sender, "No such subcommand: %v", args[0]);
			return true;
		}
		
		try
		{
			subCommand.exec(sender, Arrays.copyOfRange(args, 1, args.length));
		}
		catch (NotAPlayerException e)
		{
			F.e(sender, "Only Players can use this command.");
		}
		catch (FeedbackException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if(args.length == 1)
		{
			String typed = args[0];
			return subCommands.keySet().stream().filter(c -> {
				return StringUtils.startsWithIgnoreCase(c, typed);
			}).collect(Collectors.toList());
		}
		else
		{
			String subCommandName = args[0];
			Subcommand subCommand = subCommands.get(subCommandName);
			if(subCommand != null)
			{
				return subCommand.onTabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
			}
		}
		
		return Collections.emptyList();
	}
}
