package ninja.bytecode.shuriken.execution;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import ninja.bytecode.shuriken.collections.functional.NastyFunction;
import ninja.bytecode.shuriken.collections.functional.NastyFuture;
import ninja.bytecode.shuriken.collections.functional.NastyRunnable;
import ninja.bytecode.shuriken.logging.L;

public class J
{
	private static int tid = 0;
	private static final ExecutorService e = Executors.newCachedThreadPool(new ThreadFactory()
	{
		@Override
		public Thread newThread(Runnable r)
		{
			tid++;
			Thread t = new Thread(r);
			t.setName("Actuator " + tid);
			t.setPriority(Thread.MIN_PRIORITY);
			t.setUncaughtExceptionHandler((et, e) -> {
				L.f("Exception encountered in " + et.getName());
				L.ex(e);
			});
			
			return t;
		}
	});
	
	public static void dofor(int a, Function<Integer, Boolean> c, int ch, Consumer<Integer> d)
	{
		for(int i = a; c.apply(i); i+=ch)
		{
			c.apply(i);
		}
	}
	
	public static boolean doif(Supplier<Boolean> c, Runnable g)
	{
		if(c.get())
		{
			g.run();
			return true;
		}
		
		return false;
	}
	
	public static void a(Runnable a)
	{
		e.submit(a);
	}
	
	public static <T> Future<T> a(Callable<T> a)
	{
		return e.submit(a);
	}
	
	public static void attemptAsync(NastyRunnable r)
	{
		J.a(() -> J.attempt(r));
	}
	
	public static <R> R attemptResult(NastyFuture<R> r, R onError)
	{
		try
		{
			return r.run();
		}
		
		catch(Throwable e)
		{
			
		}
		
		return onError;
	}
	
	public static <T, R> R attemptFunction(NastyFunction<T, R> r, T param, R onError)
	{
		try
		{
			return r.run(param);
		}
		
		catch(Throwable e)
		{
			
		}
		
		return onError;
	}
	
	public static boolean sleep(long ms)
	{
		try
		{
			Thread.sleep(ms);
			return false;
		}

		catch(Throwable e)
		{

		}

		return false;
	}
	
	public static boolean attempt(NastyRunnable r)
	{
		return attemptCatch(r) == null;
	}
	
	public static Throwable attemptCatch(NastyRunnable r)
	{
		try
		{
			r.run();
		}
		
		catch(Throwable e)
		{
			return e;
		}
		
		return null;
	}

	public static <T> T attempt(Supplier<T> t, T i)
	{
		try
		{
			return t.get();
		}
		
		catch(Throwable e)
		{
			return i;
		}
	}
}
