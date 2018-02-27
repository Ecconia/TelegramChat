package com.ecconia.rsisland.plugin.telegramchat.commands;

import org.bukkit.command.CommandSender;

import com.ecconia.rsisland.framework.cofami.Subcommand;
import com.ecconia.rsisland.plugin.telegramchat.TelegramPlugin;

public class CommandStatus extends Subcommand
{
	private final TelegramPlugin plugin;

	public CommandStatus(TelegramPlugin plugin)
	{
		super("status");
		this.plugin = plugin;
	}

	@Override
	public void exec(CommandSender sender, String[] args)
	{
		if(plugin.isRegistered())
		{
			if(plugin.isGoodConnection())
			{
				f.n(sender, "Last connection to the bot went without issues.");
			}
			else
			{
				f.n(sender, "Last connection to the bot failed, please check console for the reason.");
			}
		}
		else
		{
			f.n(sender, "No bot is linked, telegram relay cannot be used.");
		}
	}

	@Override
	protected boolean hasCallRequirements()
	{
		//Nope no permissions - anyone can use it.
		return false;
	}
}
