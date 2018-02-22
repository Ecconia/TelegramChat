package com.github.mastercake10.telegramchat.telegram;

import com.google.gson.JsonObject;

@SuppressWarnings("serial")
public class AnswerException extends RuntimeException
{
	public AnswerException(JsonObject json)
	{
		super(json.get("error_code").getAsString() + ":\"" + json.get("description").getAsString() + "\"");
	}

	public AnswerException(String message)
	{
		super(message);
	}
}
