package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;

public class CommandStop implements Command
{
	@Override
	public String getName()
	{
		return "stop";
	}

	@Override
	public void handle(Commander sender, String[] args)
	{
		sender.sendMessage("Shutting Down");
	}
}
