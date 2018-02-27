package com.ecconia.rsisland.plugin.telegramchat.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ecconia.rsisland.framework.cofami.Subcommand;
import com.ecconia.rsisland.plugin.telegramchat.TelegramPlugin;

public class CommandLink extends Subcommand
{
	private final TelegramPlugin plugin;
	
	public CommandLink(TelegramPlugin plugin)
	{
		super("link");
		this.plugin = plugin;
		onlyPlayer();
	}

	@Override
	public void exec(CommandSender sender, String[] args)
	{
		checkPermission(sender);
		
		if(args.length != 0)
		{
			f.e(sender, "Usage: %v", path);
		}
		
		Player player = getPlayer(sender);
		
		if (!plugin.isRegistered())
		{
			f.e(sender, "Please register a bot for this server first.");
			return;
		}

		if(plugin.isPlayerValidated(player.getUniqueId()))
		{
			f.n(sender, "You are already verified.");
			return;
		}
		
		//Get and print new token:
		String token = plugin.getNewLinkToken(((Player) sender).getUniqueId());
		f.n(sender, "Add @%v to Telegram and send following command to it: %v", plugin.getBotName(), "/verify " + token);
	}
}
