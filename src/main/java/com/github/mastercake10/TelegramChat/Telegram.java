package com.github.mastercake10.TelegramChat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.github.mastercake10.TelegramComponents.Chat;
import com.github.mastercake10.TelegramComponents.ChatMessageToMc;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Telegram
{
	public JsonObject authJson;
	public boolean connected = false;

	static int lastUpdate = 0;
	public String token;

	private List<TelegramActionListener> listeners = new ArrayList<TelegramActionListener>();

	private final TelegramChatPlugin plugin;
	
	public Telegram(TelegramChatPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public void addListener(TelegramActionListener actionListener)
	{
		listeners.add(actionListener);
	}

	public boolean auth(String token)
	{
		this.token = token;
		return reconnect();
	}

	public boolean reconnect()
	{
		try
		{
			JsonObject obj = sendGet("https://api.telegram.org/bot" + token + "/getMe");
			authJson = obj;
			System.out.print("[Telegram] Established a connection with the telegram servers.");
			connected = true;
			return true;
		}
		catch (Exception e)
		{
			connected = false;
			System.out.print("[Telegram] Sorry, but could not connect to Telegram servers. The token could be wrong.");
			return false;
		}
	}

	public boolean getUpdate()
	{
		JsonObject up = null;
		try
		{
			up = sendGet("https://api.telegram.org/bot" + plugin.getData().token + "/getUpdates?offset=" + (lastUpdate + 1));
		}
		catch (IOException e)
		{
			return false;
		}
		if (up == null)
		{
			return false;
		}
		if (up.has("result"))
		{
			for (JsonElement ob : up.getAsJsonArray("result"))
			{
				if (ob.isJsonObject())
				{
					JsonObject obj = (JsonObject) ob;
					if (obj.has("update_id"))
					{
						lastUpdate = obj.get("update_id").getAsInt();
					}
					if (obj.has("message"))
					{
						JsonObject chat = obj.getAsJsonObject("message").getAsJsonObject("chat");
						if (chat.get("type").getAsString().equals("private"))
						{
							int id = chat.get("id").getAsInt();
							if (!plugin.getData().ids.contains(id))
							{
								plugin.getData().ids.add(id);
							}

							if (obj.getAsJsonObject("message").has("text"))
							{
								String text = obj.getAsJsonObject("message").get("text").getAsString();
//								for (char c : text.toCharArray())
//								{
//									if((int) c == 55357){
//										this.sendMsg(id, "Emoticons are not allowed, sorry!");
//										return true;
//									}
//
//								}
								if (text.length() == 0)
								{
									return true;
								}
								if (text.equals("/start"))
								{
									if (plugin.getData().firstUse)
									{
										plugin.getData().firstUse = false;
										Chat chat2 = new Chat();
										chat2.chat_id = id;
										chat2.parse_mode = "Markdown";
										chat2.text = "Congratulations, your bot is working! Have fun with this Plugin. Feel free to donate via *PayPal* to keep this project up to date! [PayPal Donation URL](http://donate.spaceio.xyz/)";
										this.sendMsg(chat2);
									}
									this.sendMessage(id, "You can see the chat but you can't chat at the moment. Type */linktelegram ingame* to chat!");
								}
								else if (plugin.getData().linkCodes.containsKey(text))
								{
									//LINK
									plugin.link(plugin.getData().linkCodes.get(text), id);
									plugin.getData().linkCodes.remove(text);
								}
								else if (plugin.getData().linkedChats.containsKey(id))
								{
									ChatMessageToMc chatMsg = new ChatMessageToMc(plugin.getData().linkedChats.get(id), text, id);
									for (TelegramActionListener actionListener : listeners)
									{
										actionListener.onSendToMinecraft(chatMsg);
									}

									plugin.sendToMC(chatMsg);
								}
								else
								{
									this.sendMessage(id, "Sorry, please link your account with */linktelegram ingame* to use the chat!");
								}
							}

						}
						else if (chat.get("type").getAsString().equals("group"))
						{
							int id = chat.get("id").getAsInt();
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
		Chat chat = new Chat();
		chat.chat_id = id;
		chat.text = msg;
		sendMsg(chat);
	}

	public void sendMsg(Chat chat)
	{
		for (TelegramActionListener actionListener : listeners)
		{
			actionListener.onSendToTelegram(chat);
		}
		Gson gson = new Gson();

		post("sendMessage", gson.toJson(chat, Chat.class));

	}

	public void sendAll(final Chat chat)
	{
		new Thread(new Runnable()
		{
			public void run()
			{
//				Gson gson = new Gson();
				for (int id : plugin.getData().ids)
				{
					chat.chat_id = id;
//					post("sendMessage", gson.toJson(chat, Chat.class));
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
			URL url = new URL("https://api.telegram.org/bot" + plugin.getData().token + "/" + method);
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

			//OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			//writer.write(body);
			//writer.flush();

//			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//			for (String line; (line = reader.readLine()) != null;)
//			{
//
//			}

//			writer.close();
//			reader.close();
		}
		catch (Exception e)
		{
			reconnect();
			System.out.print("[Telegram] Disconnected from Telegram, reconnect...");
		}

	}

	public JsonObject sendGet(String url) throws IOException
	{
		String a = url;
		URL url2 = new URL(a);
		URLConnection conn = url2.openConnection();

		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String all = "";
		String inputLine;
		while ((inputLine = br.readLine()) != null)
		{
			all += inputLine;
		}

		br.close();
		JsonParser parser = new JsonParser();
		return parser.parse(all).getAsJsonObject();

	}
}
