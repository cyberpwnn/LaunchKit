package org.cyberpwn.launchkit.command;

import org.cyberpwn.launchkit.Commander;
import org.cyberpwn.launchkit.util.GList;
import org.cyberpwn.launchkit.util.L;

public class CommandHelp implements Command
{
	@Override
	public String getName()
	{
		return "help";
	}

	@Override
	public void handle(Commander sender, String[] args)
	{
		System.out.println("==============================================================================");
		System.out.println("/help - This page");
		System.out.println("/exit - Kill LaunchKit (this jvm)");
		System.out.println("    ");
		System.out.println("/lk stop,kill,exit - Exit Launchkit (same as /exit)");
		System.out.println("/lk env,set [key] [val] - Modify a variable in Environment");
		System.out.println("/lk env [key] - Get a variable in Environment");
		System.out.println("/lk env - List Environment");
		System.out.println("    ");
		System.out.println("/mc stop,kill,exit - Kills the running game (if any)");
		System.out.println("/mc invalidate - Invalidate Game's cache & Uninstall game files");
		System.out.println("/mc validate,update - Installs game files based on pack json");
		System.out.println("/mc start,launch - Starts the game (validating first)");
		System.out.println("/mc auth - Authenticates with mojang from previous token");
		System.out.println("/mc auth [user] [pass] - Authenticates with mojang (INSECURE VIA CONSOLE)");
		System.out.println("/mc auth [profile] [type] [uuid] [token] - Authenticates with mojang.");
		System.out.println("==============================================================================");
	}

	@Override
	public GList<String> getAliases()
	{
		return new GList<String>().qadd("kill").qadd("stop").qadd("die");
	}
}
