package com.github.mastercake10.telegramchat;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

//TODO: change level
public class Listeners implements Listener
{
	private final TelegramChatPlugin plugin;
	
	public Listeners(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerJoinEvent event)
	{
		if (!plugin.getConfig().getBoolean("enable-joinquitmessages"))
		{
			return;
		}
		Message message = new Message("`" + event.getPlayer().getName() + " joined the game.`");
		plugin.getTelegramConnector().sendToAllChats(message);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(PlayerDeathEvent event)
	{
		if (!plugin.getConfig().getBoolean("enable-deathmessages"))
		{
			return;
		}
		Message message = new Message("`" + event.getDeathMessage() + "`");
		plugin.getTelegramConnector().sendToAllChats(message);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event)
	{
		if (!plugin.getConfig().getBoolean("enable-joinquitmessages"))
		{
			return;
		}
		Message message = new Message("`" + event.getPlayer().getName() + " left the game.`");
		plugin.getTelegramConnector().sendToAllChats(message);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (!plugin.getConfig().getBoolean("enable-chatmessages"))
		{
			return;
		}
		if (!event.isCancelled())
		{
			Message message = new Message(escape(event.getPlayer().getName()) + ": " + escape(event.getMessage()).replaceAll(ChatColor.COLOR_CHAR + ".", ""));
			plugin.getTelegramConnector().sendToAllChats(message);
		}
	}
	
	public String escape(String str)
	{
		return str.replace("_", "\\_");
	}
}
