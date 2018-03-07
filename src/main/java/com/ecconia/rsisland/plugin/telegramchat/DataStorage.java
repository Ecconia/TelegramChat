package com.ecconia.rsisland.plugin.telegramchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DataStorage
{
	private Data data;
	
	private final File path; 
	private final FormattedLogger logger;
	
	public DataStorage(File path, FormattedLogger logger) throws IOException, JsonSyntaxException, ClassNotFoundException
	{
		this.path = path;
		this.logger = logger;
		load();
	}
	
	public void load() throws IOException, JsonSyntaxException, ClassNotFoundException
	{
		//The first time it won't exist.
		if (path.exists())
		{
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
			data = (Data) new Gson().fromJson((String) ois.readObject(), Data.class);
			ois.close();
		}
		else
		{
			data = new Data();
			save();
		}
	}
	
	public void save()
	{
		//TODO: Threading this?
		try
		{
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fout);

			oos.writeObject(new Gson().toJson(data));
			
			fout.close();
			oos.close();
		}
		catch (IOException e)
		{
			//Throw it as often as needed.
			logger.error("Could not save data file. (IOException)");
		}
	}
	
	public void addReceiver(int id)
	{
		data.receiverChatIDs.add(id);
		save();
	}
	
	public void removeReceiver(int id)
	{
		data.receiverChatIDs.remove(id);
		save();
	}

	public void addSender(int userID, UUID playerUUID)
	{
		data.senderUserIDs.put(userID, playerUUID);
		save();
	}
	
	public void removeSender(int userID)
	{
		data.senderUserIDs.remove(userID);
		save();
	}
	
	public void removeSender(UUID playerUUID)
	{
		data.senderUserIDs.remove(playerUUID);
		save();
	}

	public UUID getSender(int userID)
	{
		return data.senderUserIDs.get(userID);
	}

	public Set<Integer> getReceiverCopy()
	{
		return new HashSet<>(data.receiverChatIDs);
	}
	
	public boolean containsChat(int chatID)
	{
		return data.receiverChatIDs.contains(chatID);
	}
	
	public boolean containsSender(UUID uuid)
	{
		//TODO: Put them in a hashset too.
		return data.senderUserIDs.containsValue(uuid);
	}
	
	private class Data
	{
		/**
		 * Stores the user ID's which are allowed to send messages to the server.
		 */
		private final Map<Integer, UUID> senderUserIDs = new HashMap<>();
		
		/**
		 * Stores the chat ID's which are allowed to receive messages from the server.
		 */
		private final Set<Integer> receiverChatIDs = new HashSet<>();
	}
}
