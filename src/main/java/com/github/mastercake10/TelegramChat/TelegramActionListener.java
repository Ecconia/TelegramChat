package com.github.mastercake10.TelegramChat;

import com.github.mastercake10.TelegramComponents.Chat;
import com.github.mastercake10.TelegramComponents.ChatMessageToMc;

public interface TelegramActionListener
{
	public void onSendToTelegram(Chat chat);

	public void onSendToMinecraft(ChatMessageToMc chatMsg);
}
