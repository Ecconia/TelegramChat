package com.ecconia.rsisland.plugin.telegramchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ecconia.rsisland.plugin.telegramchat.Message;
import com.ecconia.rsisland.plugin.telegramchat.TelegramPlugin;

public class ChatListener implements Listener
{
	private final TelegramPlugin plugin;
	
	public ChatListener(TelegramPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (!event.isCancelled())
		{
			String playername = event.getPlayer().getName();
			//TODO: Config to enable formatting
			String content = TelegramPlugin.escape(event.getMessage().replaceAll(ChatColor.COLOR_CHAR + ".", ""));
			
			Message message = new Message("<b>" + playername + "</b>: " + content);
			plugin.sendToAllReceivers(message);
		}
	}
}
