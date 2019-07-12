package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;
import org.cyberpwn.launchkit.util.GList;

public interface Command
{
	public GList<String> getAliases();

	public String getName();

	public void handle(Commander sender, String[] args);
}
