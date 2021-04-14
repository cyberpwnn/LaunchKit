package ninja.bytecode.shuriken.io;

import java.io.IOException;
import java.io.InputStream;

import ninja.bytecode.shuriken.math.M;

public class MeteredInputStream extends InputStream
{
	private InputStream stream;
	private long readBytes;
	private long timeStart;
	
	public MeteredInputStream(InputStream stream)
	{
		this.stream = stream;
		readBytes = 0;
		timeStart = M.ms();
	}
	
	@Override
	public int read() throws IOException
	{
		readBytes++;
		return stream.read();
	}
	
	public void close() throws IOException
	{
		stream.close();
	}
	
	public double getBPS()
	{
		return ((double)readBytes) / (((double)getTimeElapsed()) / 1000D);
	}
	
	public long getTimeElapsed()
	{
		return M.ms() - timeStart;
	}

	public long getReadBytes() {
		return readBytes;
	}

	public long getTimeStart() {
		return timeStart;
	}
}
