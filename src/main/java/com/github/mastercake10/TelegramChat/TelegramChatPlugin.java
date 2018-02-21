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
	public static FileConfiguration config;

	public Data data = new Data();
	public Telegram telegramHook;

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable()
	{
		dataFile = new File(getDataFolder(), "data.json");
		data = new Data();

		
		this.saveDefaultConfig();
		config = this.getConfig();
		
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
		sendToMC(chatMsg.getUuid_sender(), chatMsg.getContent(), chatMsg.getChatID_sender());
	}

	private void sendToMC(UUID uuid, String msg, int sender)
	{
		OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
		List<Integer> recievers = new ArrayList<Integer>();
		recievers.addAll(data.ids);
		recievers.remove((Object) sender);
		String msgF = TelegramChatPlugin.config.getString("chat-format").replace('&', '§').replace("%player%", op.getName()).replace("%message%", msg);
		for (int id : recievers)
		{
			telegramHook.sendMsg(id, msgF);
		}
		Bukkit.broadcastMessage(msgF.replace("&", "§"));

	}

	public void link(UUID player, int chatID)
	{
		data.linkedChats.put(chatID, player);
		OfflinePlayer p = Bukkit.getOfflinePlayer(player);
		telegramHook.sendMsg(chatID, "Success! Linked " + p.getName());
	}

	public static String generateLinkToken()
	{
		Random rnd = new Random();
		int i = rnd.nextInt(9999999);
		String s = i + "";
		String finals = "";
		for (char m : s.toCharArray())
		{
			int m2 = Integer.parseInt(m + "");
			int rndi = rnd.nextInt(2);
			if (rndi == 0)
			{
				m2 += 97;
				char c = (char) m2;
				finals = finals + c;
			}
			else
			{
				finals = finals + m;
			}
		}
		return finals;
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
