package org.cyberpwn.launchkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.cyberpwn.launchkit.command.Command;
import org.cyberpwn.launchkit.command.CommandExit;
import org.cyberpwn.launchkit.command.CommandHelp;
import org.cyberpwn.launchkit.command.CommandLaunchkit;
import org.cyberpwn.launchkit.command.CommandMinecraft;

import ninja.bytecode.shuriken.collections.KList;

public class Commander extends Thread
{
	private KList<Command> commands;
	private BufferedReader bu;
	private String lastStatus;
	private String lastProgress;

	public Commander()
	{
		lastStatus = "";
		lastProgress = "";
		commands = new KList<>();
		commands.add(new CommandLaunchkit());
		commands.add(new CommandMinecraft());
		commands.add(new CommandExit());
		commands.add(new CommandHelp());
		bu = new BufferedReader(new InputStreamReader(System.in));
		start();
	}

	public void updateProgress(String state, double progress)
	{
		sendMessage("status=" + state);
		sendMessage("progress=" + (double) ((int) (progress * 10000) / 10000D));
	}

	public void sendMessage(String message)
	{
		if(message.startsWith("status="))
		{
			if(message.equals(lastStatus))
			{
				return;
			}
			
			lastStatus = message;
		}
		
		if(message.startsWith("progress="))
		{
			try
			{
				double progress = Double.valueOf(message.split("\\Q=\\E")[1]);
				int pg = (int) (progress * 100);
				double pgx = ((double)pg / 100D);
				LaunchKit.launcher.publishProgress(pgx);
				message = "progress=" + pgx;
			}
			
			catch(Throwable e)
			{
				
			}
			
			if(message.equals(lastProgress))
			{
				return;
			}
			
			lastProgress = message;
		}
		
		System.out.println("@ppm:" + message);
	}

	@Override
	public void run()
	{
		while(!interrupted())
		{
			try
			{
				receiveMessage();
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void receiveMessage() throws IOException
	{
		String command = bu.readLine();

		if(command.startsWith("/"))
		{
			String node = command.substring(1).trim();
			String[] args = new String[0];

			if(node.contains(" "))
			{
				KList<String> nodes = new KList<String>(node.split("\\Q \\E"));
				node = nodes.get(0);
				nodes.remove(0);
				args = nodes.toArray(new String[nodes.size()]);
			}

			for(Command i : commands)
			{
				if(i.getName().equalsIgnoreCase(node) || i.getAliases().contains(node.toLowerCase()))
				{
					i.handle(this, args);
					break;
				}
			}
		}
	}
}
