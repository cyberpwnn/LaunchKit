package ninja.bytecode.shuriken.collections;

@FunctionalInterface
public interface Resolver<K, V>
{
	public V resolve(K k);
}
