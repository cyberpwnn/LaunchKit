package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;

public interface Command
{
	public String getName();

	public void handle(Commander sender, String[] args);
}
