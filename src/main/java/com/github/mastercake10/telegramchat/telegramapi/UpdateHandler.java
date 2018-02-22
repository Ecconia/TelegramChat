package com.github.mastercake10.telegramchat.telegramapi;

public interface UpdateHandler
{
	void setNextUpdate(int i);

	void message(String chatType, int chatID, String text);
}
