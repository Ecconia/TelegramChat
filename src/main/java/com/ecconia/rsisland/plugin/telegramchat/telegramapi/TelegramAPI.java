package com.ecconia.rsisland.plugin.telegramchat.telegramapi;

import com.ecconia.rsisland.plugin.telegramchat.http.Connection;
import com.ecconia.rsisland.plugin.telegramchat.http.Result;
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
		Result res = Connection.postRequest(url, json);
		
		if(res.getResponse() != 200)
		{
			throw new AnswerException(new JsonParser().parse(res.getContent()).getAsJsonObject());
		}
	}

	public static void update(UpdateHandler handler, String token, int i)
	{
		Result res = Connection.getRequest("https://api.telegram.org/bot" + token + "/getUpdates?offset=" + i);
		//Request came back - next can be sent
		
		JsonObject json = new JsonParser().parse(res.getContent()).getAsJsonObject();
		
		if(res.getResponse() != 200)
		{
			throw new AnswerException(json);
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
						handler.setNextID(resultObject.get("update_id").getAsInt());
					}
					
					handler.updateDone();
					
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
					return;
				}
			}
		}
		handler.updateDone();

		return;
	}
}
