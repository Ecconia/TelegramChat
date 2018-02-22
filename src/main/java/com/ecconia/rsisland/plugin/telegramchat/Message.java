package com.ecconia.rsisland.plugin.telegramchat;

@SuppressWarnings("unused")
public class Message
{
	private String text;

	private int chat_id;

	private String parse_mode;

	public Message()
	{
	}
	
	public Message(String text)
	{
		this(0, text);
	}
	
	public Message(int chatID, String text)
	{
		this(chatID, "Markdown", text);
	}
	
	public Message(int chatID, String parseMode, String text)
	{
		this.chat_id = chatID;
		this.parse_mode = parseMode;
		this.text = text;
	}
	
	public void setChatID(int chatID)
	{
		this.chat_id = chatID;
	}

	public int getChatID()
	{
		return chat_id;
	}
}
