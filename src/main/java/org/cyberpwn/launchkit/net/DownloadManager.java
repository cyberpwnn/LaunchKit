package org.cyberpwn.launchkit.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import javax.net.ssl.HttpsURLConnection;

import org.cyberpwn.launchkit.Environment;
import org.cyberpwn.launchkit.util.Hasher;
import org.cyberpwn.launchkit.util.L;
import org.cyberpwn.launchkit.util.M;
import org.cyberpwn.launchkit.util.VIO;

import com.google.common.io.Files;

public class DownloadManager
{
	private ExecutorService downloadService;
	private ExecutorService slowService;
	private HashMap<File, DownloadMetrics> progress;
	private File cache;

	public DownloadManager(File cache)
	{
		createService();
		this.cache = cache;
		slowService = Executors.newSingleThreadExecutor();
		cleanCache();
	}

	public void cleanCache()
	{
		cache.mkdirs();
		clean(cache);
	}

	private void clean(File f)
	{
		if(f.isDirectory())
		{
			for(File i : f.listFiles())
			{
				clean(i);
			}
		}

		else
		{
			if(!f.getName().split("\\Q.\\E")[1].equals("" + getChronoSegment()))
			{
				slowService.submit(() ->
				{
					VIO.deleteUp(f);
					L.LOG.v("Cleaning Cache: " + f.getName());
				});
			}
		}
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

	public void downloadCached(String url, File file)
	{
		L.LOG.v("Downloading " + url + " to " + file.getPath());
		downloadCached(url, file, -1, () -> L.LOG.v("Downloaded " + url + " to " + file.getPath()));
	}

	public void downloadCached(String url, File file, long length)
	{
		L.LOG.v("Downloading " + url + " to " + file.getPath());
		downloadCached(url, file, length, () -> L.LOG.v("Downloaded " + url + " to " + file.getPath()));
	}

	public void downloadCached(String url, File file, long length, Runnable r)
	{
		String hash = Hasher.hash(url);
		File f = new File(cache, hash.substring(0, 2) + "/" + hash + "." + getChronoSegment());
		f.getParentFile().mkdirs();

		if(f.exists())
		{
			try
			{
				L.LOG.v("Usinc Cached Copy of " + url + " to " + file.getPath());
				Files.copy(f, file);
			}

			catch(IOException e)
			{
				e.printStackTrace();
			}

			return;
		}

		download(url, f, length, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Files.copy(f, file);
				}

				catch(IOException e)
				{
					e.printStackTrace();
				}

				r.run();
			}
		});
	}

	private long getChronoSegment()
	{
		return M.ms() / 1000 / 60 / 60 / 6;
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
					InputStream str = null;

					if(url.startsWith("https://"))
					{
						HttpsURLConnection con = (HttpsURLConnection) urlx.openConnection();
						con.setRequestMethod("GET");
						con.setRequestProperty("Content-Type", "application/json");
						con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
						str = con.getInputStream();
					}

					else
					{
						str = urlx.openStream();
					}

					long mx = System.currentTimeMillis();
					long size = length <= 0 ? getFileSize(urlx) : length;
					ReadableByteChannel rbc = new CallbackByteChannel(Channels.newChannel(str), size, (rbx, pct) -> progress.put(file, new DownloadMetrics(pct, size, rbx.getReadSoFar(), mx)));
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
