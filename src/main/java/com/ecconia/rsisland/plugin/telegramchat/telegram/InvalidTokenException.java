package com.ecconia.rsisland.plugin.telegramchat.telegram;

@SuppressWarnings("serial")
public class InvalidTokenException extends RuntimeException
{
	public InvalidTokenException(String message)
	{
		super(message);
	}
}
