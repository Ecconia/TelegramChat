package com.ecconia.rsisland.plugin.telegramchat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DataStorage
{
	/**
	 * Stores the user ID's which are allowed to send messages to the server.
	 */
	private final Map<Integer, UUID> senderUserIDs = new HashMap<>();
	
	/**
	 * Stores the chat ID's which are allowed to receive messages from the server.
	 */
	private final Set<Integer> receiverChatIDs = new HashSet<>();
	
	public void addReceiver(int id)
	{
		receiverChatIDs.add(id);
	}
	
	public void removeReceiver(int id)
	{
		receiverChatIDs.remove(id);
	}

	public void addSender(int userID, UUID playerUUID)
	{
		senderUserIDs.put(userID, playerUUID);
	}
	
	public void removeSender(int userID)
	{
		senderUserIDs.remove(userID);
	}
	
	public void removeSender(UUID playerUUID)
	{
		senderUserIDs.remove(playerUUID);
	}

	public UUID getSender(int userID)
	{
		return senderUserIDs.get(userID);
	}

	public Set<Integer> getReceiverCopy()
	{
		return new HashSet<>(receiverChatIDs);
	}
}
