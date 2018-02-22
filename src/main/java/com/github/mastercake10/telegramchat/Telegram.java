package com.github.mastercake10.telegramchat;

import com.github.mastercake10.telegramchat.telegram.TelegramAPI;
import com.github.mastercake10.telegramchat.telegram.UpdateHandler;
import com.google.gson.Gson;

public class Telegram implements UpdateHandler
{
	//Bot info:
	private String name;
	
	//Token for authentification:
	private String token;
	
	/*
	 * True - notify on error, set to false then.
	 * False - notify on success, set to true then.
	 */
	//TODO: Sync or so
	private boolean healthyConnection = true;
	
	private final TelegramChatPlugin plugin;
	
	//#########################################################################
	
	private int updateCounter = 0;
	
	@Override
	public void setNextUpdate(int i)
	{
		updateCounter = i;
	}
	
	@Override
	public void message(String chatType, int chatID, String text)
	{
		plugin.getLogger().info("Telegram message; Type:" + chatType + " ID:" + chatID + " Text:" + text);
//		if (chatObject.get("type").getAsString().equals("private"))
//		{
//			int id = chatObject.get("id").getAsInt();
//			//TODO: Set
//			if (!plugin.getData().ids.contains(id))
//			{
//				plugin.getData().ids.add(id);
//			}
//
//			if (resultObject.getAsJsonObject("message").has("text"))
//			{
//				String text = resultObject.getAsJsonObject("message").get("text").getAsString();
//				
//				if (text.length() == 0)
//				{
//					return true;
//				}
//				
//				if (text.equals("/start"))
//				{
//					if (plugin.getData().firstUse)
//					{
//						plugin.getData().firstUse = false;
//						
//						ChatJSON chat = new ChatJSON();
//						chat.chat_id = id;
//						chat.parse_mode = "Markdown";
//						chat.text = "Congratulations, your bot is working! Have fun with this Plugin.";
//						this.sendMsg(chat);
//					}
//					
//					this.sendMessage(id, "You can see the chat but you can't chat at the moment. Type */linktelegram ingame* to chat!");
//				}
//				else if (plugin.getData().pendingLinkTokens.containsKey(text))
//				{
//					plugin.link(plugin.getData().pendingLinkTokens.get(text), id);
//					plugin.getData().pendingLinkTokens.remove(text);
//				}
//				else if (plugin.getData().linkedChats.containsKey(id))
//				{
//					plugin.sendToMC(plugin.getData().linkedChats.get(id), text, id);
//				}
//				else
//				{
//					this.sendMessage(id, "Sorry, please link your account with */linktelegram ingame* to use the chat!");
//				}
//			}
//		}
//		else if (chatObject.get("type").getAsString().equals("group"))
//		{
//			int id = chatObject.get("id").getAsInt();
//			//TODO: Set
//			if (!plugin.getData().ids.contains(id))
//			{
//				plugin.getData().ids.add(id);
//			}
//		}
	}
	
	//#########################################################################
	
	public Telegram(TelegramChatPlugin plugin, String token)
	{
		this.plugin = plugin;
		this.token = token;
		if(!authentificate())
		{
			healthyConnection = false;
			plugin.getLogger().severe("Could not login as bot with token: " + token);
			plugin.getLogger().severe("Please update or remove token.");
		}
	}
	
	public boolean changeToken(String token)
	{
		//TODO: Disconnect properly
		this.token = token;
		boolean success = authentificate();
		if(!success)
		{
			healthyConnection = false;
			plugin.getLogger().warning("Could not login as bot with token: " + token);
		}
		else
		{
			healthyConnection = true;
		}
		return success;
	}

	//Should only be called once, to register the bot (not every second)
	private void authentificate()
	{
		//TODO: Catch special exception
		if(token != null && token.matches("[0-9]+:[A-Za-z0-9]+"))
		{
			plugin.getLogger().info("Token: >" + token + "<");
			name = TelegramAPI.login(token);
			plugin.getLogger().info(name + " login successfully.");
		}
		else
		{
			plugin.getLogger().warning("Aborted login - malformed or empty token.");
		}
	}

	public boolean getUpdate()
	{
		try
		{
			TelegramAPI.update(this, token, updateCounter+1);
		}
		catch (Exception e)
		{
			
		}
		
		return true;
	}

	public void sendMessage(int id, String msg)
	{
		ChatJSON chat = new ChatJSON();
		chat.chat_id = id;
		chat.text = msg;
		sendMsg(chat);
	}

	public void sendMsg(ChatJSON chat)
	{
		post("sendMessage", new Gson().toJson(chat, ChatJSON.class));
	}

	public void sendAll(final ChatJSON chat)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				for (int id : plugin.getData().ids)
				{
					chat.chat_id = id;
					sendMsg(chat);
				}
			}
		}).start();
	}

	{
		{
			if(!healthyConnection)
			{
				healthyConnection = true;
				plugin.getLogger().info("Bot is available again.");
			}
		}
		{
			if(healthyConnection)
			{
				healthyConnection = false;
			}
		}
	}

	
	public boolean isConnected()
	{
		//TODO: ehm nope...
		return true;
	}

	public boolean isRegistered()
	{
		return name != null;
	}
	
	public String getName()
	{
		return name;
	}

	public void update()
	{
		if(getUpdate())
		{
			if(!healthyConnection)
			{
				healthyConnection = true;
				plugin.getLogger().info("Bot is available again.");
			}
		}
		else
		{
			if(healthyConnection)
			{
				healthyConnection = false;
				plugin.getLogger().warning("Error loading messages. Attempting to reconnect silently.");
			}
		}
	}
}
