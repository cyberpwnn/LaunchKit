package ninja.bytecode.shuriken.execution.queue;

import ninja.bytecode.shuriken.execution.Looper;

public class QueueExecutor extends Looper
{
	private Queue<Runnable> queue;
	private boolean shutdown;
	
	public QueueExecutor()
	{
		queue = new ShurikenQueue<Runnable>();
		shutdown = false;
	}
	
	public Queue<Runnable> queue()
	{
		return queue;
	}
	
	@Override
	protected long loop()
	{
		while(queue.hasNext())
		{
			try
			{
				queue.next().run();
			}
			
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}

		if(shutdown && !queue.hasNext())
		{
			interrupt();
			return -1;
		}
		
		return Math.max(500, (long) getRunTime() * 10);
	}

	public double getRunTime() {
		return 10; //TODO: ACTUALLY CHECK
	}

	public void shutdown()
	{
		shutdown = true;
	}
}
