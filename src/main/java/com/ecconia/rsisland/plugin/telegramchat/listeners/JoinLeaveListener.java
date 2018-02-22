package com.ecconia.rsisland.plugin.telegramchat.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ecconia.rsisland.plugin.telegramchat.FormattedMessage;
import com.ecconia.rsisland.plugin.telegramchat.Message;
import com.ecconia.rsisland.plugin.telegramchat.TelegramChatPlugin;

public class JoinLeaveListener implements Listener
{
	private final TelegramChatPlugin plugin;
	
	public JoinLeaveListener(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event)
	{
		Message message = new FormattedMessage(event.getPlayer().getName() + " joined the game.");
		plugin.getTelegramConnector().sendToAllChats(message);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event)
	{
		Message message = new FormattedMessage(event.getPlayer().getName() + " left the game.");
		plugin.getTelegramConnector().sendToAllChats(message);
	}
}
