package org.cyberpwn.launchkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.cyberpwn.launchkit.command.Command;
import org.cyberpwn.launchkit.command.CommandExit;
import org.cyberpwn.launchkit.command.CommandLaunchkit;
import org.cyberpwn.launchkit.command.CommandMinecraft;
import org.cyberpwn.launchkit.util.GList;

public class Commander extends Thread
{
	private GList<Command> commands;
	private BufferedReader bu;

	public Commander()
	{
		commands = new GList<>();
		commands.add(new CommandLaunchkit());
		commands.add(new CommandMinecraft());
		commands.add(new CommandExit());
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
				GList<String> nodes = new GList<String>(node.split("\\Q \\E"));
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
