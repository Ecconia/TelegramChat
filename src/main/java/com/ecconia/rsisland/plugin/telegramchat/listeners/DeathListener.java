package com.ecconia.rsisland.plugin.telegramchat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.ecconia.rsisland.plugin.telegramchat.FormattedMessage;
import com.ecconia.rsisland.plugin.telegramchat.Message;
import com.ecconia.rsisland.plugin.telegramchat.TelegramPlugin;

public class DeathListener implements Listener
{
	private final TelegramPlugin plugin;
	
	public DeathListener(TelegramPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event)
	{
		//TODO: Check if canceled by other plugin
		Message message = new FormattedMessage(event.getDeathMessage());
		plugin.sendToAllReceivers(message);
	}
}
