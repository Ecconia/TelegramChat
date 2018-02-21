package com.github.mastercake10.telegramchat;

import com.github.mastercake10.telegramchat.components.Chat;
import com.github.mastercake10.telegramchat.components.ChatMessageToMc;

public interface TelegramActionListener
{
	public void onSendToTelegram(Chat chat);

	public void onSendToMinecraft(ChatMessageToMc chatMsg);
}
