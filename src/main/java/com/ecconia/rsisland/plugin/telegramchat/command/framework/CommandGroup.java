package com.ecconia.rsisland.plugin.telegramchat.command.framework;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

import com.ecconia.rsisland.plugin.telegramchat.F;

public class CommandGroup extends Subcommand
{
	private final Map<String, Subcommand> subCommands;
	
	public CommandGroup(String name, Subcommand... commands)
	{
		super(name);
		
		subCommands = new HashMap<>();
		registerSubcommands(commands);
	}
		
	private void registerSubcommands(Subcommand... commands)
	{
		for(Subcommand command : commands)
		{
			subCommands.put(command.getName(), command);
		}
	}

	@Override
	public void exec(CommandSender sender, String[] args)
	{
		if(args.length == 0)
		{
			exec(sender);
			return;
		}
		
		Subcommand subCommand = subCommands.get(args[0]);
		if(subCommand == null)
		{
			F.e(sender, "No such subcommand: %v", args[0]);
			return;
		}
		
		subCommand.exec(sender, Arrays.copyOfRange(args, 1, args.length));
	}
	
	public void exec(CommandSender sender)
	{
		F.n(sender, "Use tabcomplete to get a list of subcomands. You can manage your selections with them.");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args)
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
