package com.github.mastercake10.telegramchat.telegramapi;

import com.github.mastercake10.telegramchat.http.Connection;
import com.github.mastercake10.telegramchat.http.Result;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TelegramAPI
{
	public static String login(String token)
	{
		Result res = Connection.getRequest("https://api.telegram.org/bot" + token + "/getMe");
		
		JsonObject json = new JsonParser().parse(res.getContent()).getAsJsonObject();
		
		if(res.getResponse() != 200)
		{
			throw new AnswerException(json);
		}
		
		return json.getAsJsonObject("result").get("username").getAsString();
	}
	
	public static void sendMessage(String token, String json)
	{
		String url = "https://api.telegram.org/bot" + token + "/sendMessage";
		Connection.postRequest(url, json);
	}

	public static JsonObject update(UpdateHandler handler, String token, int i)
	{
		Result res = Connection.getRequest("https://api.telegram.org/bot" + token + "/getUpdates?offset=" + i);
		
		JsonObject json = new JsonParser().parse(res.getContent()).getAsJsonObject();
		
		if(res.getResponse() != 200)
		{
			throw new AnswerException(json);
		}
		
		if(json == null)
		{
			//TODO: Find cause
			throw new AnswerException("Wuuut? Json is null - what why?");
		}
		
		if (json.has("result"))
		{
			for (JsonElement resultElement : json.getAsJsonArray("result"))
			{
				if (resultElement.isJsonObject())
				{
					JsonObject resultObject = (JsonObject) resultElement;
					
					if (resultObject.has("update_id"))
					{
						handler.setNextUpdate(resultObject.get("update_id").getAsInt());
					}
					
					if (resultObject.has("message"))
					{
						JsonObject chatObject = resultObject.getAsJsonObject("message").getAsJsonObject("chat");
						
						//TODO: Check if groups and supergroups have the same format... lets hope they do for now.
						String chatType = chatObject.get("type").getAsString();
						int chatID = chatObject.get("id").getAsInt();
						String text = null;
						
						if (resultObject.getAsJsonObject("message").has("text"))
						{
							text = resultObject.getAsJsonObject("message").get("text").getAsString();
						}
						
						handler.message(chatType, chatID, text);
					}
				}
			}
		}
		
		return null;
	}
}
