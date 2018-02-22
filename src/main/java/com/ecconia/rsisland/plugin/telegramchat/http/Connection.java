package com.ecconia.rsisland.plugin.telegramchat.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Connection
{
	//TODO: Several exceptions
	public static Result getRequest(String url)
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

			String content = "";
			int responseCode = connection.getResponseCode();
			
			InputStream is = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			String inputLine;
			while ((inputLine = br.readLine()) != null)
			{
				content += inputLine;
			}
			
			br.close();
			
			return new Result(responseCode, content);
		}
		catch (MalformedURLException e)
		{
			//Should never happen, if it happens print the stacktrace - so that somebody complains.
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//Throw it, something is wrong.
			throw new ConnectionException("IOException: " + e.getMessage());
		}
		
		return null;
	}
	
	public static void postRequest(String url, String json)
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json; ; Charset=UTF-8");
			connection.setRequestProperty("Content-Length", String.valueOf(json.length()));

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
			
			writer.write(json);
			
			writer.close();
			wr.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while(reader.readLine() != null);
			reader.close();
		}
		catch (MalformedURLException e)
		{
			//Should never happen, if it happens print the stacktrace - so that somebody complains.
			e.printStackTrace();
		}
		catch (ProtocolException e)
		{
			//No clue when this happens.
			throw new ConnectionException("ProtocolException: " + e.getMessage());
		}
		catch (UnsupportedEncodingException e)
		{
			//No clue when this happens.
			throw new ConnectionException("UnsupportedEncodingException: " + e.getMessage());
		}
		catch (IOException e)
		{
			//Throw it, something is wrong.
			throw new ConnectionException("IOException: " + e.getMessage());
		}
	}
}
