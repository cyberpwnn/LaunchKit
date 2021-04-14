package ninja.bytecode.shuriken.tools;

import ninja.bytecode.shuriken.math.M;
import ninja.bytecode.shuriken.math.RollingSequence;

public class DownloadMetrics
{
	private long length;
	private long downloaded;
	private long startTime;
	private long lastTime;
	private long finishTime;
	private RollingSequence bps;
	
	public DownloadMetrics(long length)
	{
		bps = new RollingSequence(32);
		this.length = length;
		startTime = M.ms();
		lastTime = M.ms();
		finishTime = -1;
		downloaded = 0;
	}
	
	public void setLength(long length)
	{
		this.length = length;
	}
	
	public DownloadMetrics complete()
	{
		finishTime = M.ms();
		return this;
	}
	
	public DownloadMetrics push(long downloadedSize)
	{
		long duration = M.ms() - lastTime;
		
		if(duration <= 0 || downloadedSize <= 0)
		{
			return this;
		}
		
		double seconds = (double) duration / 1000D;
		downloaded += downloadedSize;
		lastTime = M.ms();
		bps.put((double) downloadedSize / seconds);
		
		return this;
	}
	
	public boolean isFinished()
	{
		return getTimeFinished() > getTimeStarted();
	}
	
	public long getTimeFinished()
	{
		return finishTime;
	}
	
	public long getTimeStarted()
	{
		return startTime;
	}
	
	public long getTimeElapsed()
	{
		return (isFinished() ? getTimeFinished() : M.ms()) - startTime;
	}
	
	public RollingSequence getBytesPerSecond()
	{
		return bps;
	}
	
	public boolean isDeterminate()
	{
		return length > 0;
	}
	
	public double getPercentComplete()
	{
		if(!isDeterminate())
		{
			return -1;
		}
		
		return (double) downloaded / (double) length;
	}

	public long getLength()
	{
		return length;
	}

	public long getDownloaded()
	{
		return downloaded;
	}
	
	public long getRemainingBytes()
	{
		if(!isDeterminate())
		{
			return -1;
		}
		
		return length - downloaded;
	}
	
	public long getEstimatedTimeRemaining()
	{
		if(!isDeterminate())
		{
			return 1;
		}
				
		return (long) (((double)getRemainingBytes() / getBytesPerSecond().getAverage()) * 1000D);
	}
}
