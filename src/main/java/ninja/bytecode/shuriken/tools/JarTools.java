package ninja.bytecode.shuriken.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import ninja.bytecode.shuriken.collections.KList;

public class JarTools
{
	public static InputStream readJarEntry(String path)
	{
		return readJarEntry(JarTools.class, path);
	}

	public static InputStream readJarEntry(Class<?> codeSource, String path)
	{
		return codeSource.getResourceAsStream(path.startsWith("/") ? path : ("/" + path));
	}

	/**
	 * Get the shuriken jar
	 * 
	 * @return the shuriken jar
	 */
	public static File getJar()
	{
		return getJar(JarTools.class);
	}

	/**
	 * Get the jar file (path) which contains the specified class
	 * 
	 * @return
	 */
	public static File getJar(Class<?> c)
	{
		try
		{
			return new File(c.getProtectionDomain().getCodeSource().getLocation().toURI());
		}

		catch(URISyntaxException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get all classes of the specified type in the given package in shuriken
	 * 
	 * @param <T>
	 *            the inferred type
	 * @param superPackage
	 *            the package
	 * @param superClass
	 *            the class that any returned class must be subclasses of (otherwise
	 *            its ignored)
	 * @return the list
	 */
	public static <T> KList<Class<? extends T>> getClassesInPackage(String superPackage, Class<T> superClass)
	{
		return getClassesInPackage(getJar(), superPackage, superClass);
	}

	/**
	 * Get all classes of the specified type in the given jar package
	 * 
	 * @param <T>
	 *            the inferred type
	 * @param codeSource
	 *            the jar
	 * @param superPackage
	 *            the package
	 * @param superClass
	 *            the class that any returned class must be subclasses of (otherwise
	 *            its ignored)
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public static <T> KList<Class<? extends T>> getClassesInPackage(File codeSource, String superPackage, Class<T> superClass)
	{
		KList<Class<?>> g = getClassesInPackage(codeSource, superPackage);
		return g.convert((c) -> superClass.isAssignableFrom(c) ? (Class<? extends T>) c : null);
	}

	/**
	 * Get all classes in the given package in the shuriken jar
	 * 
	 * @param superPackage
	 *            the package (i.e. something.xxx.types)
	 * @return KList of classes
	 */
	public static KList<Class<?>> getClassesInPackage(String superPackage)
	{
		return getClassesInPackage(getJar(), superPackage);
	}

	/**
	 * Get all classes in the given jar / package
	 * 
	 * @param codeSource
	 *            the jar containing the package
	 * @param superPackage
	 *            the package (i.e. something.xxx.types)
	 * @return KList of classes
	 */
	public static KList<Class<?>> getClassesInPackage(File codeSource, String superPackage)
	{
		KList<Class<?>> g = new KList<Class<?>>();
		JarScanner sc = new JarScanner(codeSource, superPackage);

		try
		{
			sc.scan();
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		g.addAll(sc.getClasses());

		return g;
	}

	/**
	 * Inject a jar into the system classloader. This action cannot be reverted.
	 * 
	 * @param jar
	 *            The jar file
	 * @throws NoSuchMethodException
	 *             Impossible
	 * @throws SecurityException
	 *             Why is secure?
	 * @throws IllegalAccessException
	 *             Impossible
	 * @throws IllegalArgumentException
	 *             Impossible
	 * @throws InvocationTargetException
	 *             Impossible
	 * @throws MalformedURLException
	 *             Why is bad jar path
	 */
	public static void injectJarToCP(File jar) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, MalformedURLException
	{
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		Class<?> clazz = cl.getClass();
		Method method = clazz.getSuperclass().getDeclaredMethod("addURL", new Class[] {URL.class});
		method.setAccessible(true);
		method.invoke(cl, new Object[] {jar.toURI().toURL()});
	}
}
