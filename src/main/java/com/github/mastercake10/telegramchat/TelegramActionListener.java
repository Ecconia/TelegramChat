package com.github.mastercake10.telegramchat;

import com.github.mastercake10.telegramchat.components.ChatJSON;
import com.github.mastercake10.telegramchat.components.ChatMessageToMc;

public interface TelegramActionListener
{
	public void onSendToTelegram(ChatJSON chat);

	public void onSendToMinecraft(ChatMessageToMc chatMsg);
}
