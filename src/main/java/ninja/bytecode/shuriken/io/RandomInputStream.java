package ninja.bytecode.shuriken.io;

import java.io.IOException;
import java.io.InputStream;

import ninja.bytecode.shuriken.random.RNG;

public class RandomInputStream extends InputStream
{
	private RNG rng;
	
	public RandomInputStream(String seed)
	{
		rng = new RNG(seed);
	}
	
	public RandomInputStream(long seed)
	{
		rng = new RNG(seed);
	}
	
	public RandomInputStream()
	{
		rng = new RNG();
	}
	
	@Override
	public int read() throws IOException
	{
		return (int) (rng.imax() % 256);
	}
}
