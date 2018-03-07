package com.ecconia.rsisland.plugin.telegramchat.telegrambot;

import com.ecconia.rsisland.plugin.telegramchat.FormattedLogger;

public class StopDebugSpam
{
	private boolean goodState;
	private final FormattedLogger logger;
	
	public StopDebugSpam(boolean init, FormattedLogger logger)
	{
		goodState = init;
		this.logger = logger;
		
	}
	
	public synchronized void setState(boolean state)
	{
		goodState = state;
	}
	
	public synchronized void good()
	{
		goodState = true;
	}
	
	public synchronized void bad()
	{
		goodState = false;
	}
	
	public synchronized void good(String info)
	{
		if(!goodState)
		{
			logger.info(info);
		}
		goodState = true;
	}
	
	public synchronized void bad(String error)
	{
		if(goodState)
		{
			logger.error(error);
		}
		goodState = false;
	}

	public boolean isGood()
	{
		return goodState;
	}
}
