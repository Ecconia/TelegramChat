package com.github.mastercake10.telegramchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.github.mastercake10.telegramchat.listeners.ChatListener;
import com.github.mastercake10.telegramchat.listeners.DeathListener;
import com.github.mastercake10.telegramchat.listeners.JoinLeaveListener;
import com.github.mastercake10.telegramchat.telegram.TelegramConnector;
import com.google.gson.Gson;

public class TelegramChatPlugin extends JavaPlugin
{
	private File dataFile;

	private DataJSON data;
	private TelegramConnector telegramConnector;
	
	private BukkitTask timer;

	@Override
	public void onEnable()
	{
		saveDefaultConfig();

		dataFile = new File(getDataFolder(), "data.json");
		data = new DataJSON();
		
		getCommand("telegram").setExecutor(new CommandTelegram(this));
		
		load();
		
		telegramConnector = new TelegramConnector(this, data.token);
	}

	@Override
	public void onDisable()
	{
		//TODO: Required?
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
				
				data = (DataJSON) new Gson().fromJson((String) ois.readObject(), DataJSON.class);
				
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

	public void sendToMC(UUID playerUUID, String message, int senderID)
	{
		OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerUUID);
		List<Integer> receivers = new ArrayList<Integer>();
		receivers.addAll(data.ids);
		receivers.remove((Object) senderID);

		String msgF = getConfig().getString("chat-format").replace('&', ChatColor.COLOR_CHAR).replace("%player%", offlinePlayer.getName()).replace("%message%", message);
		
		for (int id : receivers)
		{
			//TODO: ensure correct thread.
			telegramConnector.sendToChat(id, msgF);
		}
		
		//TODO: config to allow this??
		getServer().broadcastMessage(msgF.replace('&', ChatColor.COLOR_CHAR));
	}

	public void link(UUID playerUUID, int chatID)
	{
		data.linkedChats.put(chatID, playerUUID);
		OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(playerUUID);

		telegramConnector.sendToChat(chatID, "Success! Linked " + offlinePlayer.getName());
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
	
	@Deprecated
	public DataJSON getData()
	{
		return data;
	}
	
	public TelegramConnector getTelegramConnector()
	{
		return telegramConnector;
	}

	public void setToken(String token)
	{
		data.token = token;
		save();
	}
	
	public String getNewLinkToken(Player player)
	{
		String linkToken = generateLinkToken();
		data.pendingLinkTokens.put(linkToken, player.getUniqueId());
		return linkToken;
	}

	
	public void disableTriggers()
	{
		HandlerList.unregisterAll(this);
		if(timer != null)
		{
			getServer().getScheduler().cancelTask(timer.getTaskId());
			timer = null;
		}
	}
	
	public void enableTriggers()
	{
		registerListeners();
		timer = getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable()
		{
			public void run()
			{
				if(telegramConnector.isRegistered())
				{
					if(telegramConnector.isUpdating())
					{
//						getLogger().warning("Skipped updating, since update still goin on.");
					}
					else
					{
						telegramConnector.update();
					}
				}
			}
		}, 20L, 10L);
	}
	
	private void registerListeners()
	{
		ConfigurationSection messages = (ConfigurationSection) getConfig().get("messages");
		if (messages.getBoolean("chat"))
		{
			getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		}
		if (messages.getBoolean("death"))
		{
			getServer().getPluginManager().registerEvents(new DeathListener(this), this);
		}
		if (messages.getBoolean("join-leave"))
		{
			getServer().getPluginManager().registerEvents(new JoinLeaveListener(this), this);
		}
	}

	public Set<Integer> getIDs()
	{
		return data.ids;
	}
}
