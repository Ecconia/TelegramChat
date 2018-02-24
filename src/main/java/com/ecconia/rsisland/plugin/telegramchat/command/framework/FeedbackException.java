package com.ecconia.rsisland.plugin.telegramchat.command.framework;

@SuppressWarnings("serial")
public class FeedbackException extends RuntimeException
{
	public FeedbackException(String message)
	{
		super(message);
	}
}
