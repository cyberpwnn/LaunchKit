package ninja.bytecode.shuriken.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Future;

import ninja.bytecode.shuriken.collections.functional.Callback;
import ninja.bytecode.shuriken.execution.ChronoLatch;
import ninja.bytecode.shuriken.execution.J;

public class Download
{
	private DownloadMetrics metrics;
	private InputStream in;
	private OutputStream out;
	private Callback<DownloadMetrics> metricsCallback;
	private long monitorInterval;

	public Download(InputStream in)
	{
		this(in, -1);
	}
	
	public Download(InputStream in, long length)
	{
		this.in = in;
		metrics = new DownloadMetrics(length);
		monitorInterval = 250;
	}
	
	public Download agent(String userAgent)
	{
		return this;
	}
	
	public Download monitor(Callback<DownloadMetrics> metricsCallback)
	{
		this.metricsCallback = metricsCallback;
		return this;
	}

	public Download to(File file)
	{
		try
		{
			file.getParentFile().mkdirs();
		}
		
		catch(Throwable e)
		{
			
		}

		try
		{
			this.out = new BufferedOutputStream(new FileOutputStream(file), 8192 * 4);
			return this;
		}

		catch(FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Download to(OutputStream out)
	{
		this.out = out;
		return this;
	}

	public Future<Download> start()
	{
		return J.a(() -> download());
	}
	
	public Download monitorInterval(long monitorInterval)
	{
		this.monitorInterval = monitorInterval;
		return this;
	}

	private Download download() throws IOException
	{
		ChronoLatch latch = new ChronoLatch(monitorInterval);
		ReadableByteChannel rbc = Channels.newChannel(in);
		WritableByteChannel wbc = Channels.newChannel(out);
		ByteBuffer buffer = ByteBuffer.allocate(8192 * 4);
		
		long write = 0;
		int read;
		
		while((read = rbc.read(buffer)) > 0)
		{
			buffer.rewind();
			buffer.limit(read);
			write += read;
			
			while(read > 0)
			{
				read -= wbc.write(buffer);
			}
			
			buffer.clear();
			
			if(metricsCallback != null && latch.flip())
			{
				metricsCallback.run(getMetrics());
				getMetrics().push(write);
				write = 0;
			}
		}
		
		out.flush();
		out.close();
		in.close();
		
		return this;
	}

	public DownloadMetrics getMetrics()
	{
		return metrics;
	}
	
	public static Download fromURL(String url)
	{
		return fromURL(url, "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
	}

	public static Download fromURL(String url, String agent)
	{
		try
		{
			URLConnection c = new URL(url).openConnection();
			c.addRequestProperty("User-Agent", agent);
			Download d = fromStream(c.getInputStream());
			d.getMetrics().setLength(J.attempt(() -> c.getContentLengthLong(), -1L));
			return d;
		}

		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static Download fromStream(InputStream in)
	{
		return new Download(in, -1);
	}
	
	public static Download fromStream(InputStream in, long length)
	{
		return new Download(in, length);
	}
}
