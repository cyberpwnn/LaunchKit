package ninja.bytecode.shuriken.plugin;

import java.net.URL;
import java.net.URLClassLoader;

public class PluginClassLoader extends URLClassLoader
{
	private boolean sideload;
	
	public PluginClassLoader(URL[] urls, ClassLoader parent) 
	{
		super(urls, parent);
		sideload = false;
	}
	
	public void setSideload(boolean f)
	{
		this.sideload = f;
	}

	public boolean isSideload() {
		return sideload;
	}
	
	public Class<?> loadClass(String p) throws ClassNotFoundException
	{
		if(sideload)
		{
			return Class.forName(p);
		}
		
		return super.loadClass(p);
	}
}
