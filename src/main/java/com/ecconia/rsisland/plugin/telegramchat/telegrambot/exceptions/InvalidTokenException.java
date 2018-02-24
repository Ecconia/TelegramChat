package com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions;

@SuppressWarnings("serial")
public class InvalidTokenException extends RuntimeException
{
	public InvalidTokenException(String message)
	{
		super(message);
	}
}
