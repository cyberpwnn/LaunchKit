package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;
import org.cyberpwn.launchkit.Environment;
import org.cyberpwn.launchkit.util.GList;

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

			if(args[0].equals("env"))
			{
				if(args.length >= 3)
				{
					GList<String> m = new GList<String>(args);
					m.remove(0);
					m.remove(0);
					Environment.set(args[1], m.toString(" "));
				}
			}
		}
	}
}
