package ninja.bytecode.shuriken.collections.functional;

public interface NastyFuture<R>
{
	public R run() throws Throwable;
}
