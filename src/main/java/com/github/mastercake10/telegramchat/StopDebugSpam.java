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
	
	public void good()
	{
		goodState = true;
	}
	
	public void bad()
	{
		goodState = false;
	}
	
	public void good(String info)
	{
		if(!goodState)
		{
			logger.info(info);
		}
		goodState = true;
	}
	
	public void bad(String error)
	{
		if(goodState)
		{
			logger.severe(error);
		}
		goodState = false;
	}
}
