package com.ecconia.rsisland.plugin.telegramchat.telegrambot;

public interface UpdateHandler
{
	void setNextID(int i);

	void message(int userID, String chatType, int chatID, String text);
	
	void updateDone();
}
