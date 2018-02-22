package com.ecconia.rsisland.plugin.telegramchat.telegram;

import java.util.Set;
import java.util.UUID;

import com.ecconia.rsisland.plugin.telegramchat.Message;
import com.ecconia.rsisland.plugin.telegramchat.TelegramChatPlugin;
import com.ecconia.rsisland.plugin.telegramchat.http.ConnectionException;
import com.ecconia.rsisland.plugin.telegramchat.telegramapi.AnswerException;
import com.ecconia.rsisland.plugin.telegramchat.telegramapi.TelegramAPI;
import com.ecconia.rsisland.plugin.telegramchat.telegramapi.UpdateHandler;
import com.google.gson.Gson;

public class TelegramConnector implements UpdateHandler
{
	private String name;
	private String token;
	
	private final TelegramChatPlugin plugin;
	private final StopDebugSpam sds;
	
	//#########################################################################
	
	private int updateCounter;
	
	private boolean updating;
	
	@Override
	public void setNextID(int i)
	{
		updateCounter = i;
	}
	
	@Override
	public void message(int userID, String chatType, int chatID, String text)
	{
//		plugin.getLogger().info("Telegram message; Type:" + chatType + " ID:" + chatID + " Text:" + text);
		
//		if (chatType.equals("private"))
//		{
			//TODO: other types, sendMessage - proper access.
			//TODO: (super-)group support :)
			
//			if (text != null)
//			{
				if (text.length() == 0)
				{
					plugin.getLogger().warning("Incomming text was empty.");
					return;
				}
				
//				if (text.equals("/start"))
//				{
//					For whom??
//					if (plugin.getData().firstUse)
//					{
//						plugin.getData().firstUse = false;
//						
//						ChatJSON chat = new ChatJSON();
//						chat.chat_id = chatID;
//						chat.parse_mode = "Markdown";
//						chat.text = "Congratulations, your bot is working! Have fun with this Plugin.";
//						sendMessage(chat);
//					}
//					
//					this.sendMessage(chatID, "You can see the chat but you can't chat at the moment. Type */linktelegram ingame* to chat!");
//				}
//				else 
				if (text.indexOf(' ') == -1)
				{
					UUID tokenOwner = plugin.getToken(text);
					if(tokenOwner != null)
					{
						plugin.link(chatID, userID, tokenOwner, text);

						return;
					}
				}
				
				UUID senderUUID = plugin.getSender(userID);
				if(senderUUID != null)
				{
					plugin.broadcastTelegramMessage(senderUUID, text, chatID);
					return;
				}
				
//				else
//				{
//					this.sendMessage(chatID, "Sorry, please link your account with */linktelegram ingame* to use the chat!");
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
	
	@Override
	public void updateDone()
	{
		updating = false;
	}
	
	//#########################################################################
	
	//TODO: authentification should be threaded!
	public TelegramConnector(TelegramChatPlugin plugin, String token)
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
		name = null;
		
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
			updating = true;
			TelegramAPI.update(this, token, updateCounter + 1);
			sds.good("Bot is available again.");
		}
		catch (AnswerException e)
		{
			sds.bad("Server sent negative answer: " + e.getMessage());
		}
		catch (ConnectionException e)
		{
			updating = false;
			sds.bad("Error connecting to TelegramAPI: " + e.getMessage());
		}
	}

	public boolean isRegistered()
	{
		return name != null;
	}
	
	public String getName()
	{
		return name;
	}

	public boolean isUpdating()
	{
		return updating;
	}

	//#########################################################################
	
	public void sendToChat(int chatID, String content)
	{
		Message message = new Message(chatID, content);
		
		//Move on a thread too, since it may freeze (and it did in the past) the server.
		String mainThreadToken = token;
		new Thread(new Runnable()
		{
			public void run()
			{
				sendMessage(mainThreadToken, message);
			}
		}).start();
	}

	private void sendMessage(String mainThreadToken, Message message)
	{
		try
		{
			TelegramAPI.sendMessage(mainThreadToken, new Gson().toJson(message, Message.class));
			sds.good("Bot is available again.");
		}
		catch (ConnectionException e)
		{
			sds.bad("Error connecting to TelegramAPI: " + e.getMessage());
		}
		catch (AnswerException e)
		{
			//Handle 403:"Forbidden: bot was blocked by the user"
			if(e.getErrorCode() == 403 && e.getContent().equals("Forbidden: bot was blocked by the user"))
			{
				//Remove that chat from receivers:
				plugin.removeChat(message.getChatID());
			}
			else
			{
				sds.bad("Server sent negative answer on sending message: " + e.getMessage());
			}
		}
	}

	public void sendToAllChats(final Message message)
	{
		//Get the token, as long as we are in the safe mainthread.
		String mainThreadToken = token;
		Set<Integer> ids = plugin.getReceivingChatIDs();
		
		new Thread(new Runnable()
		{
			public void run()
			{
				for (int id : ids)
				{
					message.setChatID(id);
					sendMessage(mainThreadToken, message);
				}
			}
		}).start();
	}
}
