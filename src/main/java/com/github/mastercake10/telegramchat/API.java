package com.github.mastercake10.telegramchat;

public class API
{
	private static Telegram telegramInst;
	
	public static Telegram getTelegramHook()
	{
		return telegramInst;
	}

	public static void setHook(Telegram telegramHook)
	{
		telegramInst = telegramHook;
	}
}
