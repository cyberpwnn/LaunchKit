package org.cyberpwn.launchkit.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.cyberpwn.launchkit.Environment;
import org.cyberpwn.launchkit.util.L;

public class DownloadManager
{
	private ExecutorService downloadService;
	private HashMap<File, DownloadMetrics> progress;

	public DownloadManager()
	{
		createService();
	}

	public int getDownloadingFiles()
	{
		return progress.size();
	}

	public DownloadMetrics getProgress()
	{
		DownloadMetrics dm = new DownloadMetrics(0, 0, 0, 0);
		dm.setStartedAt(System.currentTimeMillis());

		for(DownloadMetrics i : progress.values())
		{
			dm.setLength(i.getLength() + dm.getLength());
			dm.setSoFar(i.getSoFar() + dm.getSoFar());

			if(i.getStartedAt() < dm.getStartedAt())
			{
				dm.setStartedAt(i.getStartedAt());
			}

			dm.setProgress(i.getProgress() + dm.getProgress());
		}

		dm.setProgress(dm.getProgress() / (double) progress.size());

		return dm;
	}

	public Map<File, DownloadMetrics> getProgressMap()
	{
		return progress;
	}

	private void createService()
	{
		downloadService = Executors.newWorkStealingPool(Environment.download_threads);
		progress = new HashMap<>();
	}

	public void waitForAll() throws InterruptedException
	{
		downloadService.shutdown();
		downloadService.awaitTermination(10000, TimeUnit.MINUTES);
		createService();
	}

	public void download(String url, File file)
	{
		L.LOG.v("Downloading " + url + " to " + file.getPath());
		download(url, file, -1, () -> L.LOG.v("Downloaded " + url + " to " + file.getPath()));
	}

	public void download(String url, File file, long length)
	{
		L.LOG.v("Downloading " + url + " to " + file.getPath());
		download(url, file, length, () -> L.LOG.v("Downloaded " + url + " to " + file.getPath()));
	}

	public void download(String url, File file, long length, Runnable r)
	{
		downloadService.submit(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					file.getParentFile().mkdirs();
					URL urlx = new URL(url);
					long mx = System.currentTimeMillis();
					long size = length <= 0 ? getFileSize(urlx) : length;
					ReadableByteChannel rbc = new CallbackByteChannel(Channels.newChannel(urlx.openStream()), size, (rbx, pct) -> progress.put(file, new DownloadMetrics(pct, size, rbx.getReadSoFar(), mx)));
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					FileChannel fileChannel = fileOutputStream.getChannel();
					fileChannel.transferFrom(rbc, 0, Long.MAX_VALUE);
					fileChannel.close();
					fileOutputStream.close();
					rbc.close();
					r.run();
				}

				catch(Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public long getFileSize(URL url)
	{
		HttpURLConnection conn = null;

		try
		{
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			long k = conn.getContentLength();
			return k;
		}

		catch(IOException e)
		{
			return -1;
		}

		finally
		{
			conn.disconnect();
		}
	}

}
