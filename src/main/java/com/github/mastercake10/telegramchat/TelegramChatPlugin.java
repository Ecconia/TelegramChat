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
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

public class TelegramChatPlugin extends JavaPlugin
{
	private File dataFile;

	private DataJSON data;
	private Telegram telegramHook;

	@Override
	public void onEnable()
	{
		saveDefaultConfig();

		dataFile = new File(getDataFolder(), "data.json");
		data = new DataJSON();
		
		getCommand("telegram").setExecutor(new CommandTelegram(this));
		
		getServer().getPluginManager().registerEvents(new Listeners(this), this);
		
		load();
		
		telegramHook = new Telegram(this, data.token);

		//TODO: enable when token set
		getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable()
		{
			public void run()
			{
				if(telegramHook.isRegistered())
				{
					telegramHook.update();
				}
			}
		}, 20L, 20L);
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
	
	@Deprecated
	public DataJSON getData()
	{
		return data;
	}
	
	public Telegram getTelegramHook()
	{
		return telegramHook;
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
}
