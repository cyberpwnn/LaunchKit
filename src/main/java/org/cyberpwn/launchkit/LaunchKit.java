package org.cyberpwn.launchkit;

import java.io.IOException;

import org.cyberpwn.launchkit.util.JSONException;

public class LaunchKit
{
	public static Launcher launcher;

	public static void main(String[] a) throws JSONException, InterruptedException, IOException, ClassNotFoundException
	{
		injectEnvironment(a);
		launcher = new Launcher();
	}

	private static void injectEnvironment(String[] a)
	{
		for(String i : a)
		{
			if(i.startsWith("!"))
			{
				String kv = i.substring(1);
				String key = kv.split("\\Q=\\E")[0];
				String val = kv.split("\\Q=\\E")[1];
				Environment.set(key, val);
			}
		}
	}
}
