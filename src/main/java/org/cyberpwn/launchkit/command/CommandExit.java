package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;
import org.cyberpwn.launchkit.util.GList;
import org.cyberpwn.launchkit.util.L;

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
	public GList<String> getAliases()
	{
		return new GList<String>().qadd("kill").qadd("stop").qadd("die");
	}
}
