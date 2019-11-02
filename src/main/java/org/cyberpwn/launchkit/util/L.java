package org.cyberpwn.launchkit.util;

import java.io.PrintStream;

import org.cyberpwn.launchkit.Environment;

public class L
{
	public static class COM
	{

	}

	public static class LOG
	{
		public static void l(Object... m)
		{
			log(1, System.out, "Info", m);
		}

		public static void w(Object... m)
		{
			log(0, System.out, "Warning", m);
		}

		public static void v(Object... m)
		{
			log(2, System.out, "V", m);
		}

		public static void f(Object... m)
		{
			log(0, System.out, "Fatal", m);
			log(0, System.err, "Fatal", m);
		}

		private static void log(int lvl, PrintStream st, String s, Object... m)
		{
			if(Environment.log_level < lvl)
			{
				return;
			}

			String mx = "";

			for(Object i : m)
			{
				mx += i == null ? "null" : i.toString() + " ";
			}

			System.out.println("[LaunchKit|" + s + "]: " + mx.trim());
		}
	}
}
