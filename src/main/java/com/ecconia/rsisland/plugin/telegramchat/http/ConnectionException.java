package com.ecconia.rsisland.plugin.telegramchat.http;

@SuppressWarnings("serial")
public class ConnectionException extends RuntimeException
{
	public ConnectionException(String message)
	{
		super(message);
	}
}
