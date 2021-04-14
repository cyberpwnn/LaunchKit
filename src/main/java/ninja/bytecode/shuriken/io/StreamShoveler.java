package ninja.bytecode.shuriken.io;

import java.io.IOException;
import java.io.InputStream;

public class StreamShoveler
{
	private final InputStream input;
	private final byte[] buf;
	
	public StreamShoveler(InputStream input, int bufferSize)
	{
		this.input = input;
		buf = new byte[bufferSize];
	}
	
	public Scoop shovel() throws IOException
	{
		int read = input.read(buf);
		return new Scoop(buf, read, read == -1);
	}
	
	public void close() throws IOException
	{
		input.close();
	}
}
