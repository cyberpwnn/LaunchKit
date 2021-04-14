package ninja.bytecode.shuriken.collections;


import java.util.LinkedHashMap;
import java.util.Map;

public class LKMap<K, V> extends LinkedHashMap<K, V>
{
	private static final long serialVersionUID = 1527847670799761130L;

	public LKMap()
	{
		super();
	}

	public LKMap(Map<K, V> map)
	{
		super();

		for(K i : map.keySet())
		{
			put(i, map.get(i));
		}
	}

	/**
	 * Copy the map
	 *
	 * @return the copied map
	 */
	public LKMap<K, V> copy()
	{
		LKMap<K, V> m = new LKMap<K, V>();

		for(K k : this.keySet())
		{
			m.put(k, get(k));
		}

		return m;
	}

	/**
	 * Chain put
	 *
	 * @param k
	 *            the key
	 * @param v
	 *            the value
	 * @return the modified map
	 */
	public LKMap<K, V> qput(K k, V v)
	{
		put(k, v);
		return this.copy();
	}

	/**
	 * Flips the maps keys and values.
	 *
	 * @return KMap V, K instead of K, V
	 */
	public LKMap<V, KList<K>> flip()
	{
		LKMap<V, KList<K>> flipped = new LKMap<V, KList<K>>();

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

	@Override
	public String toString()
	{
		KList<String> s = new KList<String>();

		for(K i : keySet())
		{
			s.add(i.toString() + ": " + get(i).toString());
		}

		return "[" + s.toString() + "]";
	}

	/**
	 * Add maps contents into the current map
	 *
	 * @param umap
	 *            the map to add in
	 * @return the modified current map
	 */
	public LKMap<K, V> append(LKMap<K, V> umap)
	{
		for(K i : umap.keySet())
		{
			put(i, umap.get(i));
		}

		return this;
	}

	/**
	 * Get a copied KList of the keys (modification safe)
	 *
	 * @return keys
	 */
	public KList<K> k()
	{
		return new KList<K>(keySet());
	}

	/**
	 * Get a copied KSet of the keys (modification safe)
	 *
	 * @return keys
	 */
	public KSet<K> kset()
	{
		return new KSet<K>(keySet());
	}

	/**
	 * Get a copied KList of the values (modification safe)
	 *
	 * @return values
	 */
	public KList<V> v()
	{
		return new KList<V>(values());
	}

	/**
	 * Get a copied KSet of the values (modification safe)
	 *
	 * @return values
	 */
	public KSet<V> vset()
	{
		return new KSet<V>(values());
	}

	/**
	 * Put if and only if the key does not yet exist
	 *
	 * @param k
	 *            the key
	 * @param v
	 *            the value
	 */
	public void putNVD(K k, V v)
	{
		if(!containsValue(v))
		{
			put(k, v);
		}
	}

	/**
	 * Get a KList of values from a list of keys
	 *
	 * @param keys
	 *            the requested keys
	 * @return the resulted values
	 */
	public KList<V> get(KList<K> keys)
	{
		KList<V> ulv = new KList<V>();

		for(K i : keys)
		{
			if(get(i) != null)
			{
				ulv.add(get(i));
			}
		}

		return ulv;
	}

	/**
	 * Removes duplicate values by removing keys with values that match other values
	 * with different keys
	 *
	 * @return the modified map
	 */
	public LKMap<K, V> removeDuplicateValues()
	{
		LKMap<K, V> map = this.copy();
		KList<K> keys = map.k().dedupe();

		clear();

		for(K i : keys)
		{
			put(i, map.get(i));
		}

		return this;
	}

	/**
	 * Put a bunch of keys and values does nothing if the lists sizes dont match.
	 * The order of the list is how assignment will be determined
	 *
	 * @param k
	 *            the keys
	 * @param v
	 *            the values
	 */
	public void put(KList<K> k, KList<V> v)
	{
		if(k.size() != v.size())
		{
			return;
		}

		for(int i = 0; i < k.size(); i++)
		{
			put(k, v);
		}
	}

	/**
	 * Sort keys based on values sorted
	 *
	 * @return the sorted keys
	 */
	public KList<K> sortK()
	{
		KList<K> k = new KList<K>();
		KList<V> v = v();

		v.sort();

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

		return k;
	}

	/**
	 * Sort values based on keys sorted
	 *
	 * @return the sorted values
	 */
	public KList<V> sortV()
	{
		KList<V> v = new KList<V>();
		KList<K> k = k();

		k.sort();

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
}
