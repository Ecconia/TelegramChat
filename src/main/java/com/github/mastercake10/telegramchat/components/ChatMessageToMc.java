package com.github.mastercake10.telegramchat.components;

import java.util.UUID;

public class ChatMessageToMc
{
	private UUID senderUUID;
	private String content;
	private int senderChatID;

	public ChatMessageToMc(UUID senderUUID, String content, int senderChatID)
	{
		this.senderUUID = senderUUID;
		this.content = content;
		this.senderChatID = senderChatID;
	}

	public UUID getSenderUUID()
	{
		return senderUUID;
	}

	public void setSenderUUID(UUID senderUUID)
	{
		this.senderUUID = senderUUID;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public int getSenderChatID()
	{
		return senderChatID;
	}

	public void setSenderChatID(int senderChatID)
	{
		this.senderChatID = senderChatID;
	}
}
