package ninja.bytecode.shuriken.collections.functional;

@SuppressWarnings("hiding")
@FunctionalInterface
public interface Consumer5<A, B, C, D, E>
{
	public void accept(A a, B b, C c, D d, E e);
}
