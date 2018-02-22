package com.github.mastercake10.telegramchat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.mastercake10.telegramchat.FormattedMessage;
import com.github.mastercake10.telegramchat.Message;
import com.github.mastercake10.telegramchat.TelegramChatPlugin;

public class DeathListener implements Listener
{
	private final TelegramChatPlugin plugin;
	
	public DeathListener(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event)
	{
		Message message = new FormattedMessage(event.getDeathMessage());
		plugin.getTelegramConnector().sendToAllChats(message);
	}
}
