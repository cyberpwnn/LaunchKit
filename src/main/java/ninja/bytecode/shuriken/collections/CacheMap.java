package ninja.bytecode.shuriken.collections;

import ninja.bytecode.shuriken.json.JSONArray;

public class CacheMap<K, V>
{
	private int limit;
	private KList<K> order;
	private KMap<K, V> map;

	public CacheMap(int limit)
	{
		this.limit = limit;
		order = new KList<K>();
		map = new KMap<K, V>();
	}

	@SuppressWarnings("unchecked")
	public void fromJSON(JSONArray ja)
	{
		K m = null;

		for(int i = 0; i < ja.length(); i++)
		{
			if(i % 2 == 0)
			{
				order.add(m = (K) ja.get(i));
			}

			else
			{
				map.put(m, (V) ja.get(i));
			}
		}
	}

	public JSONArray toJSON()
	{
		JSONArray a = new JSONArray();

		for(K i : order)
		{
			a.put(i);
			a.put(map.get(i));
		}

		return a;
	}

	public int size()
	{
		return map.size();
	}

	public KList<K> k()
	{
		return map.k();
	}

	public void clear()
	{
		map.clear();
		order.clear();
	}

	public void invalidate(K k)
	{
		order.remove(k);
		map.remove(k);
	}

	public void put(K k, V v)
	{
		if(!order.contains(k))
		{
			order.add(k);
		}

		map.put(k, v);

		while(order.size() > limit)
		{
			K kf = order.pop();
			map.remove(kf);
		}
	}

	public V get(K k)
	{
		order.remove(k);
		order.add(0, k);
		return map.get(k);
	}

	public boolean has(K k)
	{
		return map.containsKey(k);
	}
}
