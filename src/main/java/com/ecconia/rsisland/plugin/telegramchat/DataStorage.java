package com.ecconia.rsisland.plugin.telegramchat;

import java.util.ArrayList;
import java.util.List;

public class DataStorage
{
	/**
	 * Stores the user ID's which are allowed to send messages to the server.
	 */
	List<Integer> senderUserIDs = new ArrayList<>();
	
	/**
	 * Stores the chat ID's which are allowed to receive messages from the server.
	 */
	List<Integer> receiverChatIDs = new ArrayList<>();
}
