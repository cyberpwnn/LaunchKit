package ninja.bytecode.shuriken;

import java.lang.reflect.InvocationTargetException;

import ninja.bytecode.shuriken.math.Profiler;
import ninja.bytecode.shuriken.service.IService;

public class Shuriken
{
	public static String DIR = System.getenv("APPDATA") + "/Shuriken";
	public static final Profiler profiler = new Profiler();
	public static final ServiceManager serviceManager = new ServiceManager();

	public static void main(String[] a) throws Throwable
	{
		
	}

	public static <T extends IService> T getService(Class<? extends T> c)
	{
		try
		{
			return serviceManager.getService(c);
		}

		catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
