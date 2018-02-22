package com.github.mastercake10.telegramchat;

@SuppressWarnings("serial")
public class InvalidTokenException extends RuntimeException
{
	public InvalidTokenException(String message)
	{
		super(message);
	}
}
