package com.ecconia.rsisland.plugin.telegramchat.telegrambot.exceptions;

@SuppressWarnings("serial")
public class MalformedException extends RuntimeException
{
	public MalformedException(String message)
	{
		super(message);
	}
}
