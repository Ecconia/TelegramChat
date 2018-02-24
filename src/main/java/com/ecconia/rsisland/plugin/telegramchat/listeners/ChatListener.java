package com.ecconia.rsisland.plugin.telegramchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ecconia.rsisland.plugin.telegramchat.Message;
import com.ecconia.rsisland.plugin.telegramchat.TelegramChatPlugin;

public class ChatListener implements Listener
{
	private final TelegramChatPlugin plugin;
	
	public ChatListener(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (!event.isCancelled())
		{
			String playername = escape(event.getPlayer().getName());
			String content = escape(event.getMessage()).replaceAll(ChatColor.COLOR_CHAR + ".", "");
			
			Message message = new Message("*" + playername + "*: " + content);
			plugin.sendToAllReceivers(message);
		}
	}
	
	public String escape(String str)
	{
		//TODO: Config for this.
		//TODO: investigate this commit: https://github.com/jpsheehan/TelegramChat17/commit/fecfe30bcb24e8e352fd761a35898ba936265bed
		str = str.replace("_", "\\_");
		str = str.replace("*", "\\*");
		return str;
	}
}
