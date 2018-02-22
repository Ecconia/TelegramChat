package com.ecconia.rsisland.plugin.telegramchat.http;

public class Result
{
	private final int response;
	private final String content;
	
	public Result(int response, String content)
	{
		this.response = response;
		this.content = content;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public int getResponse()
	{
		return response;
	}
}