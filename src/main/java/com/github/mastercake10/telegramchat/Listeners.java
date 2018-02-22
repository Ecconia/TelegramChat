package com.github.mastercake10.telegramchat;

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
		if (plugin.getTelegramHook().isConnected())
		{
			ChatJSON chat = new ChatJSON();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getPlayer().getName() + " joined the game.`";
			plugin.getTelegramHook().sendToAllChats(chat);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		if (!plugin.getConfig().getBoolean("enable-deathmessages"))
		{
			return;
		}
		if (plugin.getTelegramHook().isConnected())
		{
			ChatJSON chat = new ChatJSON();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getDeathMessage() + "`";
			plugin.getTelegramHook().sendToAllChats(chat);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		if (!plugin.getConfig().getBoolean("enable-joinquitmessages"))
		{
			return;
		}
		if (plugin.getTelegramHook().isConnected())
		{
			ChatJSON chat = new ChatJSON();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getPlayer().getName() + " left the game.`";
			plugin.getTelegramHook().sendToAllChats(chat);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		if (!plugin.getConfig().getBoolean("enable-chatmessages"))
		{
			return;
		}
		if (plugin.getTelegramHook().isConnected())
		{
			ChatJSON chat = new ChatJSON();
			chat.parse_mode = "Markdown";
			chat.text = escape(e.getPlayer().getName()) + ": " + escape(e.getMessage()).replaceAll("§.", "");
			plugin.getTelegramHook().sendToAllChats(chat);
		}
	}
	
	public String escape(String str)
	{
		return str.replace("_", "\\_");
	}
}
