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
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DataStorage
{
	private Data data;
	
	private final File path; 
	private final Logger logger;
	
	public DataStorage(File path, Logger logger) throws IOException, JsonSyntaxException, ClassNotFoundException
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
	}
	
	public void save()
	{
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
			logger.severe("Could not save data file. (IOException)");
		}
	}
	
	public void addReceiver(int id)
	{
		data.receiverChatIDs.add(id);
	}
	
	public void removeReceiver(int id)
	{
		data.receiverChatIDs.remove(id);
	}

	public void addSender(int userID, UUID playerUUID)
	{
		data.senderUserIDs.put(userID, playerUUID);
	}
	
	public void removeSender(int userID)
	{
		data.senderUserIDs.remove(userID);
	}
	
	public void removeSender(UUID playerUUID)
	{
		data.senderUserIDs.remove(playerUUID);
	}

	public UUID getSender(int userID)
	{
		return data.senderUserIDs.get(userID);
	}

	public Set<Integer> getReceiverCopy()
	{
		return new HashSet<>(data.receiverChatIDs);
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
