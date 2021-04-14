package ninja.bytecode.shuriken.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class StreamSucker extends Thread
{
	InputStream is;
	String buffer = "";
	String type;
	Consumer<String> s;

	public StreamSucker(InputStream is, Consumer<String> s)
	{
		this.is = is;
		this.s = s;
		setName("Commander");
		start();
	}

	@Override
	public void run()
	{
		try
		{
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;

			while((line = br.readLine()) != null)
			{
				s.accept(line);
			}
		}

		catch(IOException ioe)
		{

		}
	}
}