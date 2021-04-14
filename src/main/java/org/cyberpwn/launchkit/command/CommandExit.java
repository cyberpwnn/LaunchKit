package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;
import org.cyberpwn.launchkit.util.L;

import ninja.bytecode.shuriken.collections.KList;

public class CommandExit implements Command
{
	@Override
	public String getName()
	{
		return "exit";
	}

	@Override
	public void handle(Commander sender, String[] args)
	{
		L.LOG.w("Rerouting to /launchkit stop. If you wish to exit the client (if running), use /mc kill instead.");
		System.exit(0);
	}

	@Override
	public KList<String> getAliases()
	{
		return new KList<String>().add("kill", "stop", "die");
	}
}
