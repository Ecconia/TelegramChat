package com.github.mastercake10.telegramchat;

import java.util.ArrayList;
import java.util.List;

import com.github.mastercake10.telegramchat.http.ConnectionException;
import com.github.mastercake10.telegramchat.telegram.AnswerException;
import com.github.mastercake10.telegramchat.telegram.TelegramAPI;
import com.github.mastercake10.telegramchat.telegram.UpdateHandler;
import com.google.gson.Gson;

public class Telegram implements UpdateHandler
{
	//Bot info:
	private String name;
	
	//Token for authentification:
	private String token;
	
	private final TelegramChatPlugin plugin;
	private final StopDebugSpam sds;
	
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
		sds = new StopDebugSpam(true, plugin.getLogger());
		
		try
		{
			authentificate();
			plugin.getLogger().info("Logged in as " + name);
			plugin.enableTriggers();
		}
		catch (AnswerException e)
		{
			plugin.getLogger().severe("TelegramAPI refuses Token: " + e.getMessage());
		}
		catch (ConnectionException e)
		{
			plugin.getLogger().severe("Error connecting to TelegramAPI: " + e.getMessage());
		}
		catch (InvalidTokenException e)
		{
			plugin.getLogger().severe("Stored token is invalid please remove it. Token >" + e.getMessage() + "<");
		}
	}
	
	public void changeToken(String token)
	{
		plugin.disableTriggers();
		
		this.token = token;
		authentificate();

		sds.setState(true);
	}

	private void authentificate()
	{
		if(token != null && token.matches("[0-9]+:[A-Za-z0-9]+"))
		{
			plugin.getLogger().info("Login attempt with token: >" + token + "<");
			name = TelegramAPI.login(token);
		}
		else
		{
			throw new InvalidTokenException(token);
		}
	}

	public void update()
	{
		try
		{
			TelegramAPI.update(this, token, updateCounter+1);
			sds.good("Bot is available again.");
		}
		catch (AnswerException e)
		{
			sds.bad("Server sent bad answer: " + e.getMessage());
		}
		catch (ConnectionException e)
		{
			sds.bad("Error connecting to TelegramAPI: " + e.getMessage());
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

	//#########################################################################
	
	public void sendToChat(int id, String msg)
	{
		ChatJSON chat = new ChatJSON();
		chat.chat_id = id;
		chat.text = msg;
		
		//Move on a thread too, since it may freeze (and it did in the past) the server.
		String mainThreadToken = token;
		new Thread(new Runnable()
		{
			public void run()
			{
				sendMessage(mainThreadToken, chat);
			}
		}).start();
	}

	private void sendMessage(String mainThreadToken, ChatJSON chat)
	{
		try
		{
			TelegramAPI.sendMessage(mainThreadToken, new Gson().toJson(chat, ChatJSON.class));
			sds.good("Bot is available again.");
		}
		catch (ConnectionException e)
		{
			sds.bad("Error connecting to TelegramAPI: " + e.getMessage());
		}
	}

	public void sendToAllChats(final ChatJSON chat)
	{
		//Get the token, as long as we are in the safe mainthread.
		String mainThreadToken = token;
		List<Integer> ids = new ArrayList<>(plugin.getIDs());
		
		new Thread(new Runnable()
		{
			public void run()
			{
				for (int id : ids)
				{
					chat.chat_id = id;
					sendMessage(mainThreadToken, chat);
				}
			}
		}).start();
	}

}
