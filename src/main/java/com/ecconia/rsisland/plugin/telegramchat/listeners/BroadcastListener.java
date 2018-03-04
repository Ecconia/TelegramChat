package com.ecconia.rsisland.plugin.telegramchat.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.BroadcastMessageEvent;

import com.ecconia.rsisland.plugin.telegramchat.Message;
import com.ecconia.rsisland.plugin.telegramchat.TelegramPlugin;

public class BroadcastListener implements Listener
{
	private final TelegramPlugin plugin;
	
	public BroadcastListener(TelegramPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBroadcast(BroadcastMessageEvent event)
	{
		//Maybe test for size: event.getRecipients().size() == plugin.getServer().getOnlinePlayers().size()
		if(!event.isCancelled())
		{
			//TODO: Config to enable formatting
			String content = TelegramPlugin.escape(event.getMessage().replaceAll(ChatColor.COLOR_CHAR + ".", ""));
			
			Message message = new Message(content);
			plugin.sendToAllReceivers(message);
		}
	}
}
