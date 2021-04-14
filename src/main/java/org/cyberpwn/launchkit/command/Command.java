package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;

import ninja.bytecode.shuriken.collections.KList;

public interface Command
{
	public KList<String> getAliases();

	public String getName();

	public void handle(Commander sender, String[] args);
}
