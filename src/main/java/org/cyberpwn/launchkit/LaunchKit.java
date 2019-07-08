package org.cyberpwn.launchkit;

import java.io.IOException;

import org.cyberpwn.launchkit.util.JSONException;

public class LaunchKit
{
	public static void main(String[] a) throws JSONException, InterruptedException, IOException, ClassNotFoundException
	{
		new Launcher().validate().launch();
	}
}
