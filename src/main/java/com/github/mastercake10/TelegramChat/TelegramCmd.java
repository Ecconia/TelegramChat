package com.github.mastercake10.TelegramChat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TelegramCmd implements CommandExecutor
{
	private final TelegramChatPlugin plugin;
	
	public TelegramCmd(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command arg1, String arg2, String[] args)
	{
		if (!cs.hasPermission("telegram.settoken"))
		{
			cs.sendMessage("§cYou don't have permissions to use this!");
			return true;
		}
		if (args.length == 0)
		{
			cs.sendMessage("§c/telegram [token]");
			return true;
		}
		if (plugin.getData() == null)
		{
			plugin.resetData();
		}
		plugin.getData().token = args[0];
		plugin.save();

		if (plugin.getTelegramHook().auth(plugin.getData().token))
		{
			cs.sendMessage("§cSuccessfully connected to Telegram!");
			cs.sendMessage("§aAdd " + plugin.getTelegramHook().authJson.getAsJsonObject("result").get("username").getAsString() + " to Telegram!");
		}
		else
		{
			cs.sendMessage("§cWrong token. Paste in the whole token!");
		}
		return true;
	}
}
