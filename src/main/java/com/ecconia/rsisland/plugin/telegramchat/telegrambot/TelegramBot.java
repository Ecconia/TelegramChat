package com.ecconia.rsisland.plugin.telegramchat.telegrambot;

import java.util.Set;

import com.ecconia.rsisland.plugin.telegramchat.FormattedLogger;
import com.ecconia.rsisland.plugin.telegramchat.Message;
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions.AnswerException;
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions.ConnectionException;
import com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions.InvalidTokenException;
import com.google.gson.Gson;

//TODO: Switch from polling to webhook
public class TelegramBot implements UpdateHandler
{
	private String name;
	private String token;
	
	private final BotEvents eventHandler;
	private final FormattedLogger logger;
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
		eventHandler.message(userID, chatType, chatID, text);
	}
	
	@Override
	public void updateDone()
	{
		updating = false;
	}
	
	//#########################################################################
	
	//TODO: authentification should be threaded!
	public TelegramBot(BotEvents eventHandler, FormattedLogger logger, String token)
	{
		this.token = token;
		this.logger = logger;
		this.eventHandler = eventHandler;
		sds = new StopDebugSpam(true, logger);
		
		try
		{
			authentificate();
			logger.info("Logged in as " + name);
			eventHandler.botConnected();
		}
		catch (AnswerException e)
		{
			logger.error("TelegramAPI refuses Token: " + e.getMessage());
		}
		catch (ConnectionException e)
		{
			logger.error("Error connecting to TelegramAPI: " + e.getMessage());
		}
		catch (InvalidTokenException e)
		{
			logger.error("Stored token is invalid please remove it. Token >" + e.getMessage() + "<");
		}
	}
	
	public void changeToken(String token)
	{
		eventHandler.botDisconnected();
		
		this.token = token;
		name = null;
		
		authentificate();

		sds.setState(true);
	}

	private void authentificate()
	{
		if(token != null && token.matches("[0-9]+:[A-Za-z0-9]+"))
		{
			logger.info("Login attempt with token: >%v<", token);
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

	public String getBotName()
	{
		return name;
	}

	public boolean isUpdating()
	{
		return updating;
	}

	public boolean isGoodConnection()
	{
		return sds.isGood();
	}

	//#########################################################################
	
	public void sendToChat(Message message)
	{
		String mainThreadToken = token;
		new Thread(new Runnable()
		{
			public void run()
			{
				sendMessage(mainThreadToken, message);
			}
		}).start();
	}
	
	public void sendToChat(Set<Integer> receiverIDs, Message message)
	{
		//Get the token, as long as we are in the safe mainthread.
		String mainThreadToken = token;
		
		new Thread(new Runnable()
		{
			public void run()
			{
				for (int id : receiverIDs)
				{
					message.setChatID(id);
					sendMessage(mainThreadToken, message);
				}
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
			if(e.getErrorCode() == 403 && 
					(e.getContent().equals("Forbidden: bot was blocked by the user") ||
					e.getContent().equals("Forbidden: bot was kicked from the group chat")))
			{
				//Remove that chat from receivers:
				eventHandler.chatRefusedMessage(message.getChatID());
			}
			else
			{
				sds.bad("Server sent negative answer on sending message: " + e.getMessage());
			}
		}
	}
}
