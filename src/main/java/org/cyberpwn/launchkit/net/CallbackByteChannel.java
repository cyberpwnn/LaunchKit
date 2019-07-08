package org.cyberpwn.launchkit.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class CallbackByteChannel implements ReadableByteChannel
{
	private final ProgressCallBack delegate;
	private final long size;
	private final ReadableByteChannel rbc;
	private long sizeRead;

	CallbackByteChannel(ReadableByteChannel rbc, long expectedSize, ProgressCallBack delegate)
	{
		this.delegate = delegate;
		this.size = expectedSize;
		this.rbc = rbc;
	}

	@Override
	public void close() throws IOException
	{
		rbc.close();
	}

	public long getReadSoFar()
	{
		return sizeRead;
	}

	@Override
	public boolean isOpen()
	{
		return rbc.isOpen();
	}

	@Override
	public int read(ByteBuffer bb) throws IOException
	{
		int n;
		double progress;
		if((n = rbc.read(bb)) > 0)
		{
			sizeRead += n;
			progress = size > 0 ? (double) sizeRead / (double) size * 100.0 : -1.0;
			delegate.callback(this, progress);
		}
		return n;
	}
}