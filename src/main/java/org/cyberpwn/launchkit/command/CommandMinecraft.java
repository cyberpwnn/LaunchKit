package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;
import org.cyberpwn.launchkit.LaunchKit;

public class CommandMinecraft implements Command
{
	@Override
	public String getName()
	{
		return "minecraft";
	}

	@Override
	public void handle(Commander sender, String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equals("stop"))
			{
				LaunchKit.launcher.killGame();
				sender.sendMessage("stopped");
			}

			else if(args[0].equals("invalidate"))
			{
				try
				{
					LaunchKit.launcher.invalidate();
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

			else if(args[0].equals("validate"))
			{
				try
				{
					LaunchKit.launcher.validate();
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

			else if(args[0].equals("start"))
			{
				try
				{
					LaunchKit.launcher.launch();
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
