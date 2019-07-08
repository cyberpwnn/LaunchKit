package org.cyberpwn.launchkit.util;

import java.io.InputStream;

@FunctionalInterface
public interface InputHandler
{
	public void read(InputStream in);
}
