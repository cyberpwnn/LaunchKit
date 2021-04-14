package ninja.bytecode.shuriken.collections.functional;

@FunctionalInterface
public interface ConsumerNasty2<A, B>
{
	public void accept(A a, B b) throws Throwable;
}
