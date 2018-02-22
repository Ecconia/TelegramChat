package com.github.mastercake10.telegramchat.http;

@SuppressWarnings("serial")
public class ConnectionException extends RuntimeException
{
	public ConnectionException(String message)
	{
		super(message);
	}
}
