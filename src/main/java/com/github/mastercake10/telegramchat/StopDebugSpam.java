package com.github.mastercake10.telegramchat;

import java.util.logging.Logger;

public class StopDebugSpam
{
	private boolean goodState;
	private final Logger logger;
	
	public StopDebugSpam(boolean init, Logger logger)
	{
		goodState = init;
		this.logger = logger;
		
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
			logger.severe(error);
		}
		goodState = false;
	}
}
