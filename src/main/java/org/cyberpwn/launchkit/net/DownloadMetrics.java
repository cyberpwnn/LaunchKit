package org.cyberpwn.launchkit.net;

public class DownloadMetrics
{
	private double progress;
	private long length;
	private long soFar;
	private long startedAt;

	public DownloadMetrics(double progress, long length, long soFar, long startedAt)
	{
		this.progress = progress;
		this.length = length;
		this.soFar = soFar;
		this.startedAt = startedAt;
	}

	public long getETA()
	{
		return System.currentTimeMillis() + getETL();
	}

	public long getETL()
	{
		return (long) (1000D * ((double) getRemainingBytes() / (double) getBytesPerSecond()));
	}

	public long getBytesPerSecond()
	{
		return (long) ((double) soFar / (double) getElapsed() / 1000D);
	}

	public long getRemainingBytes()
	{
		return getLength() <= 0 ? -1 : length - soFar;
	}

	public long getElapsed()
	{
		return System.currentTimeMillis() - getStartedAt();
	}

	public long getStartedAt()
	{
		return startedAt;
	}

	public void setStartedAt(long startedAt)
	{
		this.startedAt = startedAt;
	}

	public double getProgress()
	{
		return progress;
	}

	public void setProgress(double progress)
	{
		this.progress = progress;
	}

	public long getLength()
	{
		return length;
	}

	public void setLength(long length)
	{
		this.length = length;
	}

	public long getSoFar()
	{
		return soFar;
	}

	public void setSoFar(long soFar)
	{
		this.soFar = soFar;
	}
}
