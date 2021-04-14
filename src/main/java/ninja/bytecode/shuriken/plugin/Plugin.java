package ninja.bytecode.shuriken.plugin;

public interface Plugin 
{
	public boolean isEnabled();
	
	public void onEnable();
	
	public void onDisable();
	
	public PluginManager getPluginManager();
	
	public void enable();
	
	public void setManager(PluginManager manager);
	
	public void disable();
}
