package ninja.bytecode.shuriken.io;

import java.io.IOException;
import java.io.OutputStream;

import ninja.bytecode.shuriken.math.M;

public class MeteredOutputStream extends OutputStream
{
	private OutputStream stream;
	private long writtenBytes;
	private long timeStart;
	
	public MeteredOutputStream(OutputStream stream)
	{
		this.stream = stream;
		writtenBytes = 0;
		timeStart = M.ms();
	}
	
	@Override
	public void write(int b) throws IOException
	{
		stream.write(b);
		writtenBytes++;
	}
	
	public void close() throws IOException
	{
		stream.close();
	}
	
	public double getBPS()
	{
		return ((double)writtenBytes) / (((double)getTimeElapsed()) / 1000D);
	}
	
	public long getTimeElapsed()
	{
		return M.ms() - timeStart;
	}

	public long getWrittenBytes() {
		return writtenBytes;
	}

	public long getTimeStart() {
		return timeStart;
	}
}
