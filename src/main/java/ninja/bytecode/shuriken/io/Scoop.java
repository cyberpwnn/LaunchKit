package ninja.bytecode.shuriken.io;

public class Scoop
{
	private final byte[] buf;
	private final int size;
	private final boolean done;

	public Scoop(byte[] buf, int size, boolean done)
	{
		this.buf = buf;
		this.size = size;
		this.done = done;
	}

	public boolean isDone()
	{
		return done;
	}

	public byte[] getBuf()
	{
		return buf;
	}

	public int getSize()
	{
		return size;
	}
}
