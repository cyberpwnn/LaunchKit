package ninja.bytecode.shuriken.logging;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import ninja.bytecode.shuriken.format.Form;
import ninja.bytecode.shuriken.math.M;
import ninja.bytecode.shuriken.tools.ExceptionTools;
import ninja.bytecode.shuriken.collections.KList;
import ninja.bytecode.shuriken.execution.Looper;
import ninja.bytecode.shuriken.execution.queue.Queue;
import ninja.bytecode.shuriken.execution.queue.ShurikenQueue;

public class L
{
	private static final L l = new L();
	private static String lastTag = "";
	private static Looper looper;
	private static Queue<String> logBuffer;
	public static Consumer<String> consoleConsumer;
	public static KList<Consumer<String>> logConsumers;
	public static boolean DEDUPLICATE_LOGS = true;
	public static long IDLE_FLUSH_INTERVAL = 850;
	public static long ACTIVE_FLUSH_INTERVAL = 250;
	
	public static void clearLogConsumers()
	{
		logConsumers.clear();
	}
	
	public static void addLogConsumer(Consumer<String> m)
	{
		logConsumers.add(m);
	}
	
	public static void i(Object... o)
	{
		l.info(o);
	}
	
	public static void l(Object... o)
	{
		l.info(o);
	}

	public static void w(Object... o)
	{
		l.warn(o);
	}

	public static void v(Object... o)
	{
		l.verbose(o);
	}

	public static void f(Object... o)
	{
		l.fatal(o);
	}
	
	public static void e(Object... o)
	{
		l.fatal(o);
	}

	protected void info(Object... l)
	{
		log("I", l);
	}
	
	protected void log(Object... l)
	{
		log("I", l);
	}

	protected void verbose(Object... l)
	{
		log("V", l);
	}

	protected void warn(Object... l)
	{
		log("W", l);
	}

	protected void fatal(Object... l)
	{
		log("F", l);
	}
	
	protected void error(Object... l)
	{
		log("F", l);
	}

	protected void exception(Throwable e)
	{
		for(String i : ExceptionTools.toStrings(e))
		{
			fatal(i);
		}
	}

	public static void ex(Throwable e)
	{
		for(String i : ExceptionTools.toStrings(e))
		{
			l.fatal(i);
		}
	}

	private void log(String tag, Object... l)
	{
		try
		{
			if(l.length == 0)
			{
				return;
			}

			String tagger = "[" + Form.stampTime(M.ms()) + "]: ";

			if(lastTag.equals(tagger))
			{
				tagger = Form.repeat(" ", lastTag.length());
			}

			else
			{
				lastTag = tagger;
			}

			if(l.length == 1)
			{
				synchronized(logBuffer)
				{
					logBuffer.queue(tagger + "|" + tag + "/" + Thread.currentThread().getName() + "| " + (l[0] != null ? l[0].toString() : "null"));
				}
				return;
			}

			StringBuilder sb = new StringBuilder();
			for(Object i : l)
			{
				sb.append(i == null ? "null" : i.toString());
				sb.append(" ");
			}

			synchronized(logBuffer)
			{
				logBuffer.queue(tagger + "|" + Thread.currentThread().getName() + "/" + tag + "| " + sb.toString());
			}
		}
		
		catch(Throwable e)
		{
			
		}
	}

	static
	{
		logConsumers = new KList<Consumer<String>>();
		logBuffer = new ShurikenQueue<String>();
		consoleConsumer = (s) -> System.out.println(s);
		looper = new Looper()
		{
			@Override
			protected long loop()
			{
				try
				{
					return flush() ? ACTIVE_FLUSH_INTERVAL : IDLE_FLUSH_INTERVAL;
				}
				
				catch(Throwable e)
				{
					return ACTIVE_FLUSH_INTERVAL;
				}
			}
		};
		looper.setPriority(Thread.MIN_PRIORITY);
		looper.setName("Log Manager");
		looper.start();
	}

	private static void dump(String f)
	{
		if(consoleConsumer != null)
		{
			consoleConsumer.accept(f);
		}

		for(Consumer<String> i : logConsumers)
		{
			i.accept(f);
		}
	}

	public static boolean flush()
	{
		Map<String, Integer> c = DEDUPLICATE_LOGS ? new LinkedHashMap<String, Integer>() : null;

		boolean logged = false;

		while(logBuffer.hasNext())
		{
			String l = logBuffer.next();

			if(DEDUPLICATE_LOGS)
			{
				if(!c.containsKey(l))
				{
					c.put(l, 1);
				}

				else
				{
					c.put(l, c.get(l) + 1);
				}
			}

			else
			{
				dump(l);
			}
		}

		if(DEDUPLICATE_LOGS)
		{
			for(String ix : c.keySet())
			{
				dump(ix + (c.get(ix) > 1 ? (" [x" + c.get(ix) + "]") : ""));
			}
		}

		return logged;
	}
}
