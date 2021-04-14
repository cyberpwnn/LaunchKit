package ninja.bytecode.shuriken.reaction;

@FunctionalInterface
public interface Observer<T>
{
	public void onChanged(T from, T to);
}
