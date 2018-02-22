package com.github.mastercake10.telegramchat;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener
{
	private final TelegramChatPlugin plugin;
	
	public Listeners(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if (!plugin.getConfig().getBoolean("enable-joinquitmessages"))
		{
			return;
		}
		if (plugin.getTelegramConnector().isConnected())
		{
			Message message = new Message("`" + e.getPlayer().getName() + " joined the game.`");
			plugin.getTelegramConnector().sendToAllChats(message);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		if (!plugin.getConfig().getBoolean("enable-deathmessages"))
		{
			return;
		}
		if (plugin.getTelegramConnector().isConnected())
		{
			Message message = new Message("`" + e.getDeathMessage() + "`");
			plugin.getTelegramConnector().sendToAllChats(message);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		if (!plugin.getConfig().getBoolean("enable-joinquitmessages"))
		{
			return;
		}
		if (plugin.getTelegramConnector().isConnected())
		{
			Message message = new Message("`" + e.getPlayer().getName() + " left the game.`");
			plugin.getTelegramConnector().sendToAllChats(message);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		if (!plugin.getConfig().getBoolean("enable-chatmessages"))
		{
			return;
		}
		if (plugin.getTelegramConnector().isConnected())
		{
			Message message = new Message(escape(e.getPlayer().getName()) + ": " + escape(e.getMessage()).replaceAll(ChatColor.COLOR_CHAR + ".", ""));
			plugin.getTelegramConnector().sendToAllChats(message);
		}
	}
	
	public String escape(String str)
	{
		return str.replace("_", "\\_");
	}
}
