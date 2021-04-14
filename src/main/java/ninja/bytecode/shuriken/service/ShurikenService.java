package ninja.bytecode.shuriken.service;

public abstract class ShurikenService implements IService
{
	public abstract void start();
	
	public abstract void stop();
	
	@Override
	public void onStart()
	{
		start();
	}

	@Override
	public void onStop()
	{
		stop();
	}

	@Override
	public String getName()
	{
		return getClass().getSimpleName().replaceAll("\\QSVC\\E", "").replaceAll("\\QService\\E", "");
	}
}
