package com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions;

@SuppressWarnings("serial")
public class ConnectionException extends RuntimeException
{
	public ConnectionException(String message)
	{
		super(message);
	}
}
