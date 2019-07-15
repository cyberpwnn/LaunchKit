package org.cyberpwn.launchkit;

public class ProgressEmitter extends Thread
{
	private final Launcher launcher;
	private final long interval;

	public ProgressEmitter(Launcher launcher, long interval)
	{
		this.launcher = launcher;
		this.interval = interval;
	}

	@Override
	public void run()
	{
		while(!interrupted())
		{
			try
			{
				Thread.sleep(interval);
			}

			catch(InterruptedException e)
			{
				break;
			}

			launcher.emitProgress();
		}
	}
}
