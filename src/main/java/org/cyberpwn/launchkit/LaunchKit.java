package org.cyberpwn.launchkit;

import java.io.IOException;

import org.cyberpwn.launchkit.util.JSONException;

public class LaunchKit
{
	public static Launcher launcher;

	public static void main(String[] a) throws JSONException, InterruptedException, IOException, ClassNotFoundException
	{
		launcher = new Launcher();
		launcher.getCommander().join();
	}
}
