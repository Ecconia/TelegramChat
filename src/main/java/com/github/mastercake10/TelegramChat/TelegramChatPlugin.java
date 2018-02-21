package com.github.mastercake10.TelegramChat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.mastercake10.TelegramComponents.Chat;
import com.github.mastercake10.TelegramComponents.ChatMessageToMc;
import com.google.gson.Gson;

public class TelegramChatPlugin extends JavaPlugin implements Listener
{
	private File dataFile;
	private FileConfiguration config;

	private Data data;
	private Telegram telegramHook;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable()
	{
		saveDefaultConfig();

		dataFile = new File(getDataFolder(), "data.json");
		data = new Data();
		config = getConfig();
		
		getCommand("telegram").setExecutor(new TelegramCmd(this));
		getCommand("linktelegram").setExecutor(new LinkTelegramCmd(this));
		
		getServer().getPluginManager().registerEvents(this, this);
		
		File dir = getDataFolder();
		dir.mkdir();
		
		load();
		
		telegramHook = new Telegram(this);
		API.setHook(telegramHook);
		telegramHook.auth(data.token);

		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable()
		{
			boolean connectionLost = false;

			public void run()
			{
				if (connectionLost)
				{
					if (telegramHook.reconnect())
					{
						connectionLost = false;
					}
				}
				if (telegramHook.connected)
				{
					connectionLost = !telegramHook.getUpdate();
				}
			}
		}, 20L, 20L);
	}

	@Override
	public void onDisable()
	{
		save();
	}

	public void load()
	{
		if (dataFile.exists())
		{
			try
			{
				FileInputStream fin = new FileInputStream(dataFile);
				ObjectInputStream ois = new ObjectInputStream(fin);
				
				data = (Data) new Gson().fromJson((String) ois.readObject(), Data.class);
				
				ois.close();
				fin.close();
			}
			catch (Exception e)
			{
				getLogger().severe("Could not load data file. (IOException)");
			}
		}
	}
	
	public void save()
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(dataFile);
			ObjectOutputStream oos = new ObjectOutputStream(fout);

			oos.writeObject(new Gson().toJson(data));
			
			fout.close();
			oos.close();
		}
		catch (IOException e)
		{
			getLogger().severe("Could not save data file. (IOException)");
		}
	}

	public void sendToMC(ChatMessageToMc chatMsg)
	{
		sendToMC(chatMsg.getSenderUUID(), chatMsg.getContent(), chatMsg.getSenderChatID());
	}

	private void sendToMC(UUID playerUUID, String message, int senderID)
	{
		OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerUUID);
		List<Integer> receivers = new ArrayList<Integer>();
		receivers.addAll(data.ids);
		receivers.remove((Object) senderID);

		String msgF = config.getString("chat-format").replace('&', ChatColor.COLOR_CHAR).replace("%player%", offlinePlayer.getName()).replace("%message%", message);
		
		for (int id : receivers)
		{
			telegramHook.sendMessage(id, msgF);
		}
		
		//TODO: config to allow this??
		getServer().broadcastMessage(msgF.replace('&', ChatColor.COLOR_CHAR));
	}

	public void link(UUID playerUUID, int chatID)
	{
		data.linkedChats.put(chatID, playerUUID);
		OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerUUID);
		telegramHook.sendMessage(chatID, "Success! Linked " + offlinePlayer.getName());
	}

	//TODO: Resulting token looks suspicious, check usage, rewrite
	public static String generateLinkToken()
	{
		Random random = new Random();
		int randomInt = random.nextInt(9999999);
		String finalToken = "";
		
		for (char digitChar : String.valueOf(randomInt).toCharArray())
		{
			if (random.nextInt(2) == 0)
			{
				int digitInt = Integer.parseInt(digitChar + "");
				digitInt += 97;
				finalToken += (char) digitInt;
			}
			else
			{
				finalToken += digitChar;
			}
		}
		
		return finalToken;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		if (!this.getConfig().getBoolean("enable-joinquitmessages"))
		{
			return;
		}
		if (telegramHook.connected)
		{
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getPlayer().getName() + " joined the game.`";
			telegramHook.sendAll(chat);
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		if (!this.getConfig().getBoolean("enable-deathmessages"))
		{
			return;
		}
		if (telegramHook.connected)
		{
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getDeathMessage() + "`";
			telegramHook.sendAll(chat);
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e)
	{
		if (!this.getConfig().getBoolean("enable-joinquitmessages"))
		{
			return;
		}
		if (telegramHook.connected)
		{
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = "`" + e.getPlayer().getName() + " left the game.`";
			System.out.println(chat.text);
			telegramHook.sendAll(chat);
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		if (!this.getConfig().getBoolean("enable-chatmessages"))
		{
			return;
		}
		if (telegramHook.connected)
		{
			Chat chat = new Chat();
			chat.parse_mode = "Markdown";
			chat.text = escape(e.getPlayer().getName()) + ": " + escape(e.getMessage()).replaceAll("§.", "");
			telegramHook.sendAll(chat);
		}
	}

	public String escape(String str)
	{
		return str.replace("_", "\\_");
	}
	
	public Data getData()
	{
		return data;
	}
	
	public void resetData()
	{
		data = new Data();
	}
	
	public Telegram getTelegramHook()
	{
		return telegramHook;
	}
}
