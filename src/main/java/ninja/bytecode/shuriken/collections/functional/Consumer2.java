package ninja.bytecode.shuriken.collections.functional;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface Consumer2<A, B>
{
	public void accept(A a, B b);
}
