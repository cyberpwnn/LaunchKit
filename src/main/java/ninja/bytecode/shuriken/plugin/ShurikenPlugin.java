package ninja.bytecode.shuriken.plugin;

import ninja.bytecode.shuriken.execution.queue.QueueExecutor;
import ninja.bytecode.shuriken.logging.L;

public abstract class ShurikenPlugin extends L implements Plugin
{
	private boolean enabled;
	private PluginManager manager;
	private QueueExecutor executor;
	
	public ShurikenPlugin()
	{
		
	}
	
	@Override
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public void setManager(PluginManager manager)
	{
		this.manager = manager;
	}

	@Override
	public void enable() {
		if(isEnabled())
		{
			return;
		}
		
		executor = new QueueExecutor();
		executor.setName(getPluginManager().getConfig().getName());
		executor.start();
		enabled = true;
		run(this::onEnable);
		run(() -> info(getPluginManager().getConfig().getName() + " Enabled"));
	}
	
	public void run(Runnable r)
	{
		executor.queue().queue(r);
	}

	@Override
	public void disable() {
		if(!isEnabled())
		{
			return;
		}
		
		enabled = false;
		run(this::onDisable);
		run(() -> info(getPluginManager().getConfig().getName() + " Disabled"));
		executor.shutdown();

		try {
			executor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PluginManager getPluginManager() {
		return manager;
	}
}
