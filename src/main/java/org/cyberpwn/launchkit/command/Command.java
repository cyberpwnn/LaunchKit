package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;

import ninja.bytecode.shuriken.collections.GList;

public interface Command
{
	public GList<String> getAliases();

	public String getName();

	public void handle(Commander sender, String[] args);
}
