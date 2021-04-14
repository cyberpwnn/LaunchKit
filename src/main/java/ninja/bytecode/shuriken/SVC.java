package ninja.bytecode.shuriken;

import ninja.bytecode.shuriken.service.IService;

public class SVC
{
	public static <T extends IService> T get(Class<? extends T> c)
	{
		return Shuriken.getService(c);
	}
}
