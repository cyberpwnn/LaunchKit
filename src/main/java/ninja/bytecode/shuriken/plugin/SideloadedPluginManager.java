package ninja.bytecode.shuriken.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.zip.ZipException;

import ninja.bytecode.shuriken.logging.L;

public class SideloadedPluginManager implements PluginManager {

	private PluginClassLoader classLoader;
	private PluginConfig config;
	private Plugin plugin;
	
	public SideloadedPluginManager(String name, String pc) throws ZipException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		config = new PluginConfig("internal", pc, name);
		this.classLoader = new PluginClassLoader(new URL[] {getClass().getProtectionDomain().getCodeSource().getLocation()}, getClassLoader());
		classLoader.setSideload(true);
		Class<?> pclass = classLoader.loadClass(config.getMain());
		plugin = (Plugin) pclass.getConstructor().newInstance();
		plugin.setManager(this);
		L.i("Loaded Plugin " + config.getName());
	}
	
	@Override
	public PluginClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public File getJar() {
		return null;
	}

	@Override
	public void unload()
	{
		if(plugin.isEnabled())
		{
			plugin.disable();
		}
		
		plugin.setManager(null);
		plugin = null;
		try {
			classLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		classLoader = null;
		L.i("Unloaded " + config.getName());
	}

	@Override
	public PluginConfig getConfig() {
		return config;
	}
}
