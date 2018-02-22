package com.ecconia.rsisland.plugin.telegramchat.telegramapi;

public interface UpdateHandler
{
	void setNextID(int i);

	void message(String chatType, int chatID, String text);
	
	void updateDone();
}
