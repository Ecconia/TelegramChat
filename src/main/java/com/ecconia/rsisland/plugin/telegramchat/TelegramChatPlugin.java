package com.ecconia.rsisland.plugin.telegramchat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.ecconia.rsisland.plugin.telegramchat.listeners.ChatListener;
import com.ecconia.rsisland.plugin.telegramchat.listeners.DeathListener;
import com.ecconia.rsisland.plugin.telegramchat.listeners.JoinLeaveListener;
import com.ecconia.rsisland.plugin.telegramchat.telegram.TelegramConnector;
import com.google.gson.JsonSyntaxException;

public class TelegramChatPlugin extends JavaPlugin
{
	private TelegramConnector telegramConnector;
	
	private DataStorage storage;
	private Map<String, UUID> pendingUserTokens;
	
	private BukkitTask timer;

	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		pendingUserTokens = new HashMap<>();

		try
		{
			storage = new DataStorage(new File(getDataFolder(), "data.json"), getLogger());
		}
		catch (JsonSyntaxException e)
		{
			getLogger().severe("Corrupted data file.");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		catch (ClassNotFoundException e)
		{
			getLogger().severe("Internal error, please report to the developer in charge.");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		catch (IOException e)
		{
			getLogger().severe("IOException while reading data-file.");
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		getCommand("telegram").setExecutor(new CommandTelegram(this));
		
		telegramConnector = new TelegramConnector(this, getConfig().getString("token"));
	}

	@Override
	public void onDisable()
	{
		pendingUserTokens = null;
		disableTriggers();
		storage = null;
		telegramConnector = null;
	}

	public void broadcastTelegramMessage(UUID playerUUID, String message, int senderID)
	{
		//TODO: Escape..
		String playerName = getServer().getOfflinePlayer(playerUUID).getName();
		
		Set<Integer> receivers = storage.getReceiverCopy();
		receivers.remove(senderID);
		
		ConfigurationSection formats = (ConfigurationSection) getConfig().get("format");

		String messageFormatted = formats.getString("telegram", "Please add a formatting to the plugin-config.");
		messageFormatted = messageFormatted.replace("%player%", playerName);
		messageFormatted = messageFormatted.replace("%message%", message);
		
		for (int chatID : receivers)
		{
			telegramConnector.sendToChat(chatID, messageFormatted);
		}
		
		messageFormatted = formats.getString("mc", "[TelegramChat] Please add a formatting to the plugin-config.");
		messageFormatted = messageFormatted.replace('&', ChatColor.COLOR_CHAR);
		messageFormatted = messageFormatted.replace("%player%", playerName);
		messageFormatted = messageFormatted.replace("%message%", message);
		
		//TODO: config to allow this?? -> default no.
		getServer().broadcastMessage(messageFormatted);//.replace('&', ChatColor.COLOR_CHAR));
	}

	public TelegramConnector getTelegramConnector()
	{
		return telegramConnector;
	}

	public void setToken(String token)
	{
		getConfig().set("token", token);
		saveConfig();
	}
	
	public Set<Integer> getReceivingChatIDs()
	{
		return storage.getReceiverCopy();
	}
	
	//#########################################################################
	
	public void link(int chatID, int userID, UUID playerUUID, String token)
	{
		storage.addReceiver(chatID);
		storage.addSender(userID, playerUUID);
		
		pendingUserTokens.remove(token);
		
		//TODO: Escape
		String playername = getServer().getOfflinePlayer(playerUUID).getName();

		//TODO: Change feedback message
		telegramConnector.sendToChat(chatID, "Success! Linked " + playername);
	}

	public String getNewLinkToken(UUID playerUUID)
	{
		String linkToken = generateLinkToken();
		pendingUserTokens.put(linkToken, playerUUID);
		
		return linkToken;
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
	
	public UUID getToken(String token)
	{
		return pendingUserTokens.get(token);
	}
	
	public UUID getSender(int userID)
	{
		return storage.getSender(userID);
	}
	
	public void removeChat(int chatID)
	{
		storage.removeReceiver(chatID);
	}
	
	//#########################################################################
	
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
				//TODO: find non-loop solution
				if(telegramConnector.isRegistered())
				{
					if(!telegramConnector.isUpdating())
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
}
