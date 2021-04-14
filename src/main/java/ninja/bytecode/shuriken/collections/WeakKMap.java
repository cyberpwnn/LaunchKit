package ninja.bytecode.shuriken.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import ninja.bytecode.shuriken.collections.functional.Consumer2;
import ninja.bytecode.shuriken.collections.functional.Consumer3;
import ninja.bytecode.shuriken.execution.queue.Queue;

public class WeakKMap<K, V> extends WeakHashMap<K, V>
{
	public WeakKMap()
	{
		super();
	}

	public WeakKMap(WeakKMap<K, V> KMap)
	{
		this();
		put(KMap);
	}

	/**
	 * Puts a value into a map-value-list based on the key such that if KMap<K,
	 * KList<S>> where V is KList<S>
	 *
	 * @param <S>
	 *            the list type in the value type
	 * @param k
	 *            the key to look for
	 * @param vs
	 *            the values to put into the list of the given key
	 * @return the same list (builder)
	 */
	@SuppressWarnings("unchecked")
	public <S> WeakKMap<K, V> putValueList(K k, S... vs)
	{
		try
		{
			WeakKMap<K, KList<S>> s = (WeakKMap<K, KList<S>>) this;

			if(!s.containsKey(k))
			{
				s.put(k, new KList<S>());
			}

			s.get(k).add(vs);
		}

		catch(Throwable e)
		{

		}

		return this;
	}

	/**
	 * Returns a sorted list of keys from this map, based on the sorting order of
	 * the values.
	 *
	 * @return the value-sorted key list
	 */
	public KList<K> sortK()
	{
		KList<K> k = new KList<K>();
		KList<V> v = v();

		Collections.sort(v, new Comparator<V>()
		{
			@Override
			public int compare(V v, V t1)
			{
				return v.toString().compareTo(t1.toString());
			}
		});

		for(V i : v)
		{
			for(K j : k())
			{
				if(get(j).equals(i))
				{
					k.add(j);
				}
			}
		}

		k.dedupe();
		return k;
	}

	/**
	 * Returns a sorted list of keys from this map, based on the sorting order of
	 * the values. Sorting is based on numerical values
	 *
	 * @return the value-sorted key list
	 */
	public KList<K> sortKNumber()
	{
		KList<K> k = new KList<K>();
		KList<V> v = v();

		Collections.sort(v, new Comparator<V>()
		{
			@Override
			public int compare(V v, V t1)
			{
				Number n1 = (Number) v;
				Number n2 = (Number) t1;

				return (int) ((n1.doubleValue() - n2.doubleValue()) * 1000);
			}
		});

		for(V i : v)
		{
			for(K j : k())
			{
				if(get(j).equals(i))
				{
					k.add(j);
				}
			}
		}

		k.dedupe();
		return k;
	}

	/**
	 * Put another map's values into this map
	 *
	 * @param m
	 *            the map to insert
	 * @return this map (builder)
	 */
	public WeakKMap<K, V> put(Map<K, V> m)
	{
		putAll(m);
		return this;
	}

	/**
	 * Return a copy of this map
	 *
	 * @return the copied map
	 */
	public WeakKMap<K, V> copy()
	{
		return new WeakKMap<K, V>(this);
	}

	/**
	 * Loop through each keyvalue set (copy of it) with the map parameter
	 *
	 * @param f
	 *            the function
	 * @return the same KMap
	 */
	public WeakKMap<K, V> rewrite(Consumer3<K, V, WeakKMap<K, V>> f)
	{
		WeakKMap<K, V> m = copy();

		for(K i : m.k())
		{
			f.accept(i, get(i), this);
		}

		return this;
	}

	/**
	 * Loop through each keyvalue set (copy of it)
	 *
	 * @param f
	 *            the function
	 * @return the same KMap
	 */
	public WeakKMap<K, V> each(Consumer2<K, V> f)
	{
		for(K i : k())
		{
			f.accept(i, get(i));
		}

		return this;
	}

	/**
	 * Flip the hashmap and flatten the value list even if there are multiple keys
	 *
	 * @return the flipped and flattened hashmap
	 */
	public WeakKMap<V, K> flipFlatten()
	{
		WeakKMap<V, KList<K>> f = flip();
		WeakKMap<V, K> m = new WeakKMap<>();

		for(V i : f.k())
		{
			m.putNonNull(i, m.isEmpty() ? null : m.get(0));
		}

		return m;
	}

	/**
	 * Flip the hashmap so keys are now list-keys in the value position
	 *
	 * @return the flipped hashmap
	 */
	public WeakKMap<V, KList<K>> flip()
	{
		WeakKMap<V, KList<K>> flipped = new WeakKMap<V, KList<K>>();

		for(K i : keySet())
		{
			if(i == null)
			{
				continue;
			}

			if(!flipped.containsKey(get(i)))
			{
				flipped.put(get(i), new KList<K>());
			}

			flipped.get(get(i)).add(i);
		}

		return flipped;
	}

	/**
	 * Sort values based on the keys sorting order
	 *
	 * @return the values (sorted)
	 */
	public KList<V> sortV()
	{
		KList<V> v = new KList<V>();
		KList<K> k = k();

		Collections.sort(k, new Comparator<K>()
		{
			@Override
			public int compare(K v, K t1)
			{
				return v.toString().compareTo(t1.toString());
			}
		});

		for(K i : k)
		{
			for(V j : v())
			{
				if(get(i).equals(j))
				{
					v.add(j);
				}
			}
		}

		v.dedupe();
		return v;
	}

	public KList<V> sortVNoDedupe()
	{
		KList<V> v = new KList<V>();
		KList<K> k = k();

		Collections.sort(k, new Comparator<K>()
		{
			@Override
			public int compare(K v, K t1)
			{
				return v.toString().compareTo(t1.toString());
			}
		});

		for(K i : k)
		{
			for(V j : v())
			{
				if(get(i).equals(j))
				{
					v.add(j);
				}
			}
		}

		return v;
	}

	/**
	 * Get a copy of this maps keys
	 *
	 * @return the keys
	 */
	public KList<K> k()
	{
		KList<K> k = new KList<K>();
		Set<K> kk = keySet();
		k.addAll(kk);
		return k;
	}

	/**
	 * Get a copy of this maps values
	 *
	 * @return the values
	 */
	public KList<V> v()
	{
		return new KList<V>(values());
	}

	/**
	 * Still works as it normally should except it returns itself (builder)
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value (single only supported)
	 * @return
	 */
	public WeakKMap<K, V> qput(K key, V value)
	{
		super.put(key, value);
		return this;
	}

	/**
	 * Works just like put, except it wont put anything unless the key and value are
	 * nonnull
	 *
	 * @param key
	 *            the nonnull key
	 * @param value
	 *            the nonnull value
	 * @return the same map
	 */
	public WeakKMap<K, V> putNonNull(K key, V value)
	{
		if(key != null || value != null)
		{
			put(key, value);
		}

		return this;
	}

	public V putThen(K key, V valueIfKeyNotPresent)
	{
		if(!containsKey(key))
		{
			put(key, valueIfKeyNotPresent);
		}

		return get(key);
	}

	/**
	 * Clear this map and return it
	 *
	 * @return the cleared map
	 */
	public WeakKMap<K, V> qclear()
	{
		super.clear();
		return this;
	}

	/**
	 * Convert this map to keypairs
	 *
	 * @return the keypair list
	 */
	public KList<KeyPair<K, V>> keypair()
	{
		KList<KeyPair<K, V>> g = new KList<>();
		each((k, v) -> g.add(new KeyPair<K, V>(k, v)));
		return g;
	}

	/**
	 * Create a keypair queue
	 *
	 * @return the queue
	 */
	public Queue<KeyPair<K, V>> enqueue()
	{
		return Queue.create(keypair());
	}

	/**
	 * Create a key queue
	 *
	 * @return the queue
	 */
	public Queue<K> enqueueKeys()
	{
		return Queue.create(k());
	}

	/**
	 * Create a value queue
	 *
	 * @return the queue
	 */
	public Queue<V> enqueueValues()
	{
		return Queue.create(v());
	}
}
