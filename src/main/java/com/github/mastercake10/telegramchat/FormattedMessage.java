package com.github.mastercake10.telegramchat;

public class FormattedMessage extends Message
{
	public FormattedMessage(String content)
	{
		super("`" + content + "`");
	}
}
