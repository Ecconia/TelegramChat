package com.ecconia.rsisland.plugin.telegramchat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataJSON
{
	public String token = "";
	
	public HashMap<Integer, UUID> linkedChats = new HashMap<Integer, UUID>();
	
	public HashMap<String, UUID> pendingLinkTokens = new HashMap<String, UUID>();
	
	public Set<Integer> ids = new HashSet<Integer>();
	
	boolean firstUse = true;
}
