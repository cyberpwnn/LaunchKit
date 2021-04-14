package ninja.bytecode.shuriken.plugin;

import java.io.File;

import ninja.bytecode.shuriken.collections.KList;

public interface PluginSystem
{
	public KList<PluginManager> getPlugins();
	
	public PluginManager load(File p) throws PluginException;
	
	public void loadAll(File folder) throws PluginException;
	
	public void disableAll();
	
	public void enableAll();
	
	public void unloadAll();
	
	public PluginManager getPlugin(String name);
	
	public PluginManager sideload(String pluginName, String classname) throws PluginException;
}
