package org.cyberpwn.launchkit.net;

@FunctionalInterface
public interface ProgressCallBack
{
	public void callback(CallbackByteChannel rbc, double progress);
}