package org.cyberpwn.launchkit.command;

import java.io.IOException;

import org.cyberpwn.launchkit.Commander;
import org.cyberpwn.launchkit.LaunchKit;

import ninja.bytecode.shuriken.collections.KList;

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
			if(args[0].equals("stop") || args[0].equals("exit") || args[0].equals("kill"))
			{
				LaunchKit.launcher.killGame();
				sender.sendMessage("stopped");
			}

			else if(args[0].equals("invalidate"))
			{
				try
				{
					LaunchKit.launcher.invalidate();
					sender.sendMessage("cleared .minecraft+.launchkit");
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

			else if(args[0].equals("validate") || args[0].equals("update"))
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
			
			else if(args[0].equals("isalive"))
			{
				try
				{
					if(LaunchKit.launcher.isRunning())
					{
						sender.sendMessage("running");
					}
					
					else
					{
						sender.sendMessage("notrunning");
					}
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}

			else if(args[0].equals("start") || args[0].equals("launch"))
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

			else if(args[0].equals("authenticate") || args[0].equals("auth"))
			{
				if(args.length == 1)
				{
					try
					{
						LaunchKit.launcher.authenticateWithToken();
					}

					catch(ClassNotFoundException | IOException e)
					{
						e.printStackTrace();
					}
				}

				else if(args.length == 3)
				{
					try
					{
						for(int i = 0; i < 300; i++)
						{
							System.out.println("       ");
						}

						LaunchKit.launcher.authenticateWithCredentials(args[1], args[2]);
					}

					catch(ClassNotFoundException | IOException e)
					{
						e.printStackTrace();
					}
				}

				else if(args.length == 5)
				{
					LaunchKit.launcher.authenticateExternal(args[1], args[2], args[3], args[4]);
				}

				else
				{
					System.out.println("Use /mc auth (for token use)");
					System.out.println("Or  /mc auth <user> <pass> (for credentials)");
					System.out.println("Or  /mc auth <profilename> <profiletype> <uuid> <accesstoken> (for external)");
				}
			}
		}
	}

	@Override
	public KList<String> getAliases()
	{
		return new KList<String>().addNonNull("mc");
	}
}
