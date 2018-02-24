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
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.BotEvents;
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.TelegramBot;
import com.google.gson.JsonSyntaxException;

public class TelegramPlugin extends JavaPlugin implements BotEvents
{
	public static final String prefix = ChatColor.WHITE + "[" + ChatColor.AQUA + "Telegram" + ChatColor.WHITE + "] ";
	
	private TelegramBot telegramBot;
	private boolean botRegistered;
	
	private DataStorage storage;
	private Map<String, UUID> pendingUserTokens;
	
	private BukkitTask timer;

	@Override
	public void onEnable()
	{
		saveDefaultConfig();
		//Ensure that config values are set.
		getConfig().addDefault("format.mc", "&f[&bTG&f]&7 %player%&f: %message%");
		getConfig().addDefault("format.telegram", "[TG] *%player%*: %message%");
		getConfig().addDefault("format.telegram-escape-player", false);
		getConfig().addDefault("format.telegram-escape-message", true);
		getConfig().addDefault("messages.join-leave", true);
		getConfig().addDefault("messages.death", true);
		getConfig().addDefault("messages.chat", true);
		
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
		
		telegramBot = new TelegramBot(this, getLogger(), getConfig().getString("token"));
	}

	@Override
	public void onDisable()
	{
		pendingUserTokens = null;
		disableTriggers();
		storage = null;
		telegramBot = null;
	}

	public void broadcastTelegramMessage(UUID playerUUID, String message, int senderID)
	{
		//TODO: Escape..
		String playerName = getServer().getOfflinePlayer(playerUUID).getName();
		ConfigurationSection formats = (ConfigurationSection) getConfig().get("format");

		{
			Set<Integer> receivers = storage.getReceiverCopy();
			receivers.remove(senderID);
			
			String messageFormatted = formats.getString("telegram");
			String escapedPlayername = formats.getBoolean("telegram-escape-player") ? escape(playerName) : playerName;
			messageFormatted = messageFormatted.replace("%player%", escapedPlayername);
			String escapedMessage = formats.getBoolean("telegram-escape-message") ? escape(message) : message;
			messageFormatted = messageFormatted.replace("%message%", escapedMessage);
			
			telegramBot.sendToChat(receivers, new Message(messageFormatted));
		}
		
		String messageFormatted = formats.getString("mc");
		messageFormatted = messageFormatted.replace('&', ChatColor.COLOR_CHAR);
		messageFormatted = messageFormatted.replace("%player%", playerName);
		messageFormatted = messageFormatted.replace("%message%", message);
		
		//TODO: config to allow this?? -> default no.
		getServer().broadcastMessage(messageFormatted);//.replace('&', ChatColor.COLOR_CHAR));
	}

	public void sendToAllReceivers(Message message)
	{
		telegramBot.sendToChat(storage.getReceiverCopy(), message);
	}
	
	//#########################################################################
	
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
				if(botRegistered)
				{
					if(!telegramBot.isUpdating())
					{
						telegramBot.update();
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

	//#########################################################################

	public String getBotName()
	{
		return telegramBot.getBotName();
	}
	
	public boolean isRegistered()
	{
		return botRegistered;
	}
	
	public void setToken(String token)
	{
		getConfig().set("token", token);
		saveConfig();
		telegramBot.changeToken(token);
	}
	
	public static String escape(String str)
	{
		//TODO: investigate this commit: https://github.com/jpsheehan/TelegramChat17/commit/fecfe30bcb24e8e352fd761a35898ba936265bed
		str = str.replace("_", "\\_");
		str = str.replace("*", "\\*");
		return str;
	}
	
	//#########################################################################

	@Override
	public void botDisconnected()
	{
		botRegistered = false;
		disableTriggers();
	}
	
	@Override
	public void botConnected()
	{
		botRegistered = true;
		enableTriggers();
	}
	
	@Override
	public void message(int userID, String chatType, int chatID, String text)
	{
		//getLogger().info("Telegram message; Type:" + chatType + " ChatID:" + chatID + " UserID:" + userID + " Text:\"" + text + "\"");

		//TODO: other types, sendMessage - proper access.
		//TODO: (super-)group support :)
			
		if (text.length() == 0)
		{
			//TODO: If and when this can ever happen.
			getLogger().warning("Incomming text was empty.");
			return;
		}
		
		UUID senderUUID = storage.getSender(userID);
		
		if(text.charAt(0) == '/')
		{
			text = text.substring(1);
			String parts[] = text.split(" ");
			String command = parts[0].toLowerCase();
			
			if("verify".equals(command))
			{
				//TODO: This blocks an account from changing his MC-Account. - Fix for this.
				if(senderUUID != null)
				{
					telegramBot.sendToChat(new Message(chatID, "You are already verified."));
				}
				else
				{
					if (parts.length != 2)
					{
						telegramBot.sendToChat(new Message(chatID, "Usage: /verify <token>"));
					}
					else
					{
						String token = parts[1];
						UUID tokenOwner = pendingUserTokens.get(token);
						if(tokenOwner != null)
						{
							//TODO: If used in a group with output-disabled this will turn the output on again.
							storage.addReceiver(chatID);
							storage.addSender(userID, tokenOwner);
							
							pendingUserTokens.remove(token);
							
							//TODO: Escape
							String playername = getServer().getOfflinePlayer(tokenOwner).getName();
							telegramBot.sendToChat(new Message(chatID, "You have been verified as " + playername));
						}
					}
				}
			}
			else if("help".equals(command))
			{
				telegramBot.sendToChat(new Message(chatID, 
						"Commands:\n"
						+ "/verify <token> - Verify your account. (Use \"/telegram link\" on the MC-Server to get a token.)\n"
						+ "/relay <on/off> - turn on/off chat relay."));
			}
			else if(senderUUID == null)
			{
				telegramBot.sendToChat(new Message(chatID, "Please verify yourself to use bot-commands."));
			}
			else
			{
				if("relay".equals(command))
				{
					if (parts.length != 2 || !parts[1].toLowerCase().matches("(on|off)"))
					{
						telegramBot.sendToChat(new Message(chatID, "Usage: /relay <on/off>"));
					}
					else if(parts[1].toLowerCase().equals("on"))
					{
						if(storage.containsChat(chatID))
						{
							telegramBot.sendToChat(new Message(chatID, "Chat relay is already on."));
						}
						else
						{
							storage.addReceiver(chatID);
							telegramBot.sendToChat(new Message(chatID, "Started chat relay."));
						}
					}
					else
					{
						if(storage.containsChat(chatID))
						{
							storage.removeReceiver(chatID);
							telegramBot.sendToChat(new Message(chatID, "Stopped chat relay."));
						}
						else
						{
							telegramBot.sendToChat(new Message(chatID, "Chat relay is already off."));
						}
					}
				}
				else
				{
					telegramBot.sendToChat(new Message(chatID, "Unknown command, use /help."));
				}
			}
			
			return;
		}
		
		if(senderUUID != null && storage.containsChat(chatID))
		{
			broadcastTelegramMessage(senderUUID, text, chatID);
			return;
		}
		
		//TODO: Handle no perms yet - Enhance chat settings (relay-chat=true, warn-on-missing-perms=true)
	}
	
	@Override
	public void chatRefusedMessage(int chatID)
	{
		storage.removeReceiver(chatID);
	}
}
