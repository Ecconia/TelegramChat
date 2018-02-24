package com.ecconia.rsisland.plugin.telegramchat.telegrambot;

public interface BotEvents
{
	void botDisconnected();
	
	void botConnected();
	
	void message(int userID, String chatType, int chatID, String text);
	
	void chatRefusedMessage(int chatID);
}
