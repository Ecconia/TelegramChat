package com.github.mastercake10.telegramchat.telegram;

@SuppressWarnings("serial")
public class InvalidTokenException extends RuntimeException
{
	public InvalidTokenException(String message)
	{
		super(message);
	}
}
