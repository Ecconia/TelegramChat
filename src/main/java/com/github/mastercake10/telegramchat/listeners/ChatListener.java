package com.github.mastercake10.telegramchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.mastercake10.telegramchat.Message;
import com.github.mastercake10.telegramchat.TelegramChatPlugin;

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
			Message message = new Message(escape("*" + event.getPlayer().getName()) + "*: " + escape(event.getMessage()).replaceAll(ChatColor.COLOR_CHAR + ".", ""));
			plugin.getTelegramConnector().sendToAllChats(message);
		}
	}
	
	public String escape(String str)
	{
		return str.replace("_", "\\_");
	}
}
