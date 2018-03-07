package com.ecconia.rsisland.plugin.telegramchat;

import org.bukkit.command.ConsoleCommandSender;

import com.ecconia.rsisland.framework.cofami.Feedback;

public class FormattedLogger
{
	private final Feedback f;
	private final ConsoleCommandSender console;
	
	public FormattedLogger(Feedback f, ConsoleCommandSender console)
	{
		this.f = f;
		this.console = console;
	}
	
	public void info(String message, Object... parameter)
	{
		f.n(console, message, parameter);
	}
	
	public void error(String message, Object... parameter)
	{
		f.e(console, message, parameter);
	}
}
