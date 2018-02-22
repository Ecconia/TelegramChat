package com.ecconia.rsisland.plugin.telegramchat.telegramapi;

import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class AnswerException extends RuntimeException
{
	private final int errorCode;
	private final String content;
	
	public AnswerException(JsonObject json)
	{
		super(json.get("error_code").getAsString() + ":\"" + json.get("description").getAsString() + "\"");
		errorCode = json.get("error_code").getAsInt();
		content = json.get("description").getAsString();
	}
	
	public int getErrorCode()
	{
		return errorCode;
	}
	
	public String getContent()
	{
		return content;
	}
}
