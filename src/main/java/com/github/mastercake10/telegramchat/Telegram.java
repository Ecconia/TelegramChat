package com.github.mastercake10.telegramchat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Telegram
{
	//Bot info:
	private String name;
	//Token for authentification:
	private String token;
	
	private int lastUpdate = 0;

	private final TelegramChatPlugin plugin;
	
	public Telegram(TelegramChatPlugin plugin, String token)
	{
		this.plugin = plugin;
		this.token = token;
	}
	
	public boolean changeToken(String token)
	{
		this.token = token;
	}

	//Should only be called once, to register the bot (not every second)
	private boolean authentificate()
	{
		try
		{
			JsonObject authJson = sendGet("https://api.telegram.org/bot" + token + "/getMe");
			name = authJson.getAsJsonObject("result").get("username").getAsString();
			
			plugin.getLogger().info(name + " login successfully.");
			return true;
		}
		catch (IOException e)
		{
			//TODO: Filter cause.
			plugin.getLogger().warning("Something happend while attempting to login with the bot (invalid token, no connection).");
			return false;
		}
	}

	public boolean getUpdate()
	{
		JsonObject responseJson;
		try
		{
			responseJson = sendGet("https://api.telegram.org/bot" + token + "/getUpdates?offset=" + (lastUpdate + 1));
		}
		catch (IOException e)
		{
			return false;
		}
		
		if (responseJson == null)
		{
			return false;
		}
		
		if (responseJson.has("result"))
		{
			for (JsonElement resultElement : responseJson.getAsJsonArray("result"))
			{
				if (resultElement.isJsonObject())
				{
					JsonObject resultObject = (JsonObject) resultElement;
					
					if (resultObject.has("update_id"))
					{
						lastUpdate = resultObject.get("update_id").getAsInt();
					}
					
					if (resultObject.has("message"))
					{
						JsonObject chatObject = resultObject.getAsJsonObject("message").getAsJsonObject("chat");
						
						if (chatObject.get("type").getAsString().equals("private"))
						{
							int id = chatObject.get("id").getAsInt();
							//TODO: Set
							if (!plugin.getData().ids.contains(id))
							{
								plugin.getData().ids.add(id);
							}

							if (resultObject.getAsJsonObject("message").has("text"))
							{
								String text = resultObject.getAsJsonObject("message").get("text").getAsString();
								
								if (text.length() == 0)
								{
									return true;
								}
								
								if (text.equals("/start"))
								{
									if (plugin.getData().firstUse)
									{
										plugin.getData().firstUse = false;
										
										ChatJSON chat = new ChatJSON();
										chat.chat_id = id;
										chat.parse_mode = "Markdown";
										chat.text = "Congratulations, your bot is working! Have fun with this Plugin.";
										this.sendMsg(chat);
									}
									
									this.sendMessage(id, "You can see the chat but you can't chat at the moment. Type */linktelegram ingame* to chat!");
								}
								else if (plugin.getData().pendingLinkTokens.containsKey(text))
								{
									plugin.link(plugin.getData().pendingLinkTokens.get(text), id);
									plugin.getData().pendingLinkTokens.remove(text);
								}
								else if (plugin.getData().linkedChats.containsKey(id))
								{
									plugin.sendToMC(plugin.getData().linkedChats.get(id), text, id);
								}
								else
								{
									this.sendMessage(id, "Sorry, please link your account with */linktelegram ingame* to use the chat!");
								}
							}
						}
						else if (chatObject.get("type").getAsString().equals("group"))
						{
							int id = chatObject.get("id").getAsInt();
							//TODO: Set
							if (!plugin.getData().ids.contains(id))
							{
								plugin.getData().ids.add(id);
							}
						}
					}
				}
			}
		}
		
		return true;
	}

	public void sendMessage(int id, String msg)
	{
		ChatJSON chat = new ChatJSON();
		chat.chat_id = id;
		chat.text = msg;
		sendMsg(chat);
	}

	public void sendMsg(ChatJSON chat)
	{
		post("sendMessage", new Gson().toJson(chat, ChatJSON.class));
	}

	public void sendAll(final ChatJSON chat)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
				for (int id : plugin.getData().ids)
				{
					chat.chat_id = id;
					sendMsg(chat);
				}
			}
		}).start();
	}

	public void post(String method, String json)
	{
		try
		{
			String body = json;
			URL url = new URL("https://api.telegram.org/bot" + token + "/" + method);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json; ; Charset=UTF-8");
			connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
			
			writer.write(body);
			
			writer.close();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while(reader.readLine() != null);
			reader.close();
		}
		catch (Exception e)
		{
			//TODO: validate login
			plugin.getLogger().warning("Disconnected from Telegram, reconnect...");
		}
	}

	public JsonObject sendGet(String urlString) throws IOException
	{
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String content = "";
		String inputLine;
		while ((inputLine = br.readLine()) != null)
		{
			content += inputLine;
		}

		br.close();
		return new JsonParser().parse(content).getAsJsonObject();
	}
	
	public boolean isConnected()
	{
		//TODO: ehm nope...
		return true;
	}

	public boolean isRegistered()
	{
		return name != null;
	}
	
	public String getName()
	{
		return name;
	}
}
