package com.github.mastercake10.telegramchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataJSON
{
	public String token = "";
	
	public HashMap<Integer, UUID> linkedChats = new HashMap<Integer, UUID>();
	
	public HashMap<String, UUID> pendingLinkTokens = new HashMap<String, UUID>();
	
	public List<Integer> ids = new ArrayList<Integer>();
	
	boolean firstUse = true;
}
