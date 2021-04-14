package ninja.bytecode.shuriken.collections.functional;

public interface NastyFunction<T, R>
{
	public R run(T t) throws Throwable;
}
