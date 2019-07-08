package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;

public class CommandLaunchkit implements Command
{
	@Override
	public String getName()
	{
		return "launchkit";
	}

	@Override
	public void handle(Commander sender, String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equals("stop"))
			{
				System.exit(0);
			}
		}
	}
}
