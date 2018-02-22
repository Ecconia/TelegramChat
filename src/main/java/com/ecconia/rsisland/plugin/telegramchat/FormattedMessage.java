package com.ecconia.rsisland.plugin.telegramchat;

public class FormattedMessage extends Message
{
	public FormattedMessage(String content)
	{
		super("`" + content + "`");
	}
}
